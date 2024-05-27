package fintech.spain.unnax.impl;

import com.google.common.base.Throwables;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.JsonUtils;
import fintech.TimeMachine;
import fintech.spain.unnax.UnnaxBankStatementsService;
import fintech.spain.unnax.db.BankStatementsRequestEntity;
import fintech.spain.unnax.db.BankStatementsRequestRepository;
import fintech.spain.unnax.db.BankStatementsRequestStatus;
import fintech.spain.unnax.event.BankStatementReceivedEvent;
import fintech.spain.unnax.event.BankStatementsUploadedEvent;
import fintech.spain.unnax.model.UnnaxResponse;
import fintech.spain.unnax.statement.BankStatementsUnnaxClient;
import fintech.spain.unnax.statement.model.BankStatementResponse;
import fintech.spain.unnax.statement.model.BankStatementsRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fintech.spain.unnax.db.BankStatementsRequestStatus.ERROR;
import static fintech.spain.unnax.db.BankStatementsRequestStatus.SUCCESS;
import static fintech.spain.unnax.db.Entities.bankStatementsRequestEntity;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UnnaxBankStatementsServiceImpl implements UnnaxBankStatementsService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private BankStatementsUnnaxClient client;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private BankStatementsRequestRepository bankStatementsRequestRepository;

    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("^(\\d+)\\.pdf$");

    private static final String DEFAULT_ZIP_PASSWORD = "1234";

    public void requestStatementsUpload(LocalDate from, LocalDate to, String sourceIban) {
        log.info("Requesting unnax statements from {} to {} for source iban {}", from, to, sourceIban);
        String reqCode = UUID.randomUUID().toString();
        UnnaxResponse<BankStatementResponse> unnaxResponse = client.request(
            new BankStatementsRequest().
                setRequestCode(reqCode)
                .setIban(sourceIban)
                .setStartDate(from)
                .setEndDate(to)
                .setZipPassword(DEFAULT_ZIP_PASSWORD)
        );

        BankStatementsRequestEntity entity = transactionTemplate.execute(action -> {
            BankStatementsRequestEntity requestEntity = new BankStatementsRequestEntity();
            requestEntity.setStatus(BankStatementsRequestStatus.NEW);
            requestEntity.setFromDate(from);
            requestEntity.setToDate(to);
            requestEntity.setRequestCode(reqCode);
            requestEntity.setIban(sourceIban);
            return bankStatementsRequestRepository.save(requestEntity);
        });

        if (unnaxResponse.isError()) {
            entity.setStatus(ERROR);
            entity.setError(JsonUtils.writeValueAsString((unnaxResponse.getErrorResponse())));
            bankStatementsRequestRepository.save(entity);
            log.error("Error while charge from payment card through Unnax: {}", unnaxResponse.getErrorResponse());
        }
    }

    @Override
    public Map<String, LocalDate> lastSuccessRequestedDateByIban() {

        List<Tuple> query = queryFactory.select(bankStatementsRequestEntity.iban, bankStatementsRequestEntity.toDate.max())
            .from(bankStatementsRequestEntity)
            .where(bankStatementsRequestEntity.status.eq(SUCCESS))
            .groupBy(bankStatementsRequestEntity.iban)
            .fetch();

        return query.stream()
        .collect(Collectors.toMap(t->t.get(bankStatementsRequestEntity.iban), t->t.get(bankStatementsRequestEntity.toDate.max())));

    }

    @SneakyThrows
    @EventListener
    public void handleBankStatementsUploaded(BankStatementsUploadedEvent event) {

        if (event.getErrorMessage() != null) {
            markProcessed(event.getRequestCode(), ERROR, String.format("Error code: [%s], message: [%s]", event.getErrorCode(), event.getErrorMessage()));
            return;
        }
        try (CloseableHttpClient httpClient = buildHttpClient()) {
            HttpGet request = new HttpGet(event.getLink());
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    LocalFileHeader localFileHeader;
                    int readLen;
                    byte[] readBuffer = new byte[4096];
                    try (ZipInputStream zipInputStream = new ZipInputStream(response.getEntity().getContent(), DEFAULT_ZIP_PASSWORD.toCharArray())) {
                        while ((localFileHeader = zipInputStream.getNextEntry()) != null) {
                            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                                while ((readLen = zipInputStream.read(readBuffer)) != -1) {
                                    outputStream.write(readBuffer, 0, readLen);
                                }
                                byte[] extracted = outputStream.toByteArray();
                                String fileName = localFileHeader.getFileName();
                                Matcher m = FILE_NAME_PATTERN.matcher(fileName);
                                if (m.matches()) {
                                    Long statementId = Long.parseLong(m.group(1));
                                    eventPublisher.publishEvent(new BankStatementReceivedEvent(statementId, extracted));
                                } else {
                                    log.info("Processing file [{}] failed. Request code {}", fileName, event.getRequestCode());
                                }
                            }
                        }
                    }
                } else {
                    log.error("Unzip error. Request code {}", event.getRequestCode());
                    markProcessed(event.getRequestCode(), ERROR, IOUtils.toString(response.getEntity().getContent(), UTF_8.name()));
                }
            } catch (Exception e) {
                log.error("Unzip ZIP error. Request code " + event.getRequestCode(), e);
                markProcessed(event.getRequestCode(), ERROR, JsonUtils.writeValueAsString(Throwables.getStackTraceAsString(e)));
                return;
            }
            markProcessed(event.getRequestCode(), SUCCESS, null);
        }
    }


    private void markProcessed(String orderCode, BankStatementsRequestStatus status, String error) {
        transactionTemplate.execute(action -> {
            bankStatementsRequestRepository.findByRequestCode(orderCode).ifPresent(
                req -> {
                    req.setStatus(status);
                    req.setProcessedAt(TimeMachine.now());
                    req.setError(error);
                }
            );
            return 0;
        });
    }

    private CloseableHttpClient buildHttpClient() {
        RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(5_000)
            .setSocketTimeout(60_000)
            .build();
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(config);
        return builder.build();
    }
}
