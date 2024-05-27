package fintech.spain.alfa.product.asnef.internal;

import com.google.common.base.Preconditions;
import fintech.JsonUtils;
import fintech.filestorage.FileStorageService;
import fintech.spain.asnef.LogRowStatus;
import fintech.spain.asnef.LogStatus;
import fintech.spain.asnef.LogType;
import fintech.spain.asnef.commands.ImportFileCommand;
import fintech.spain.asnef.db.Entities;
import fintech.spain.asnef.db.LogEntity;
import fintech.spain.asnef.db.LogRepository;
import fintech.spain.asnef.db.LogRowEntity;
import fintech.spain.asnef.models.RpInputControlRecord;
import fintech.spain.asnef.models.RpInputDevoControlRecord;
import fintech.spain.asnef.models.RpInputDevoHeaderRecord;
import fintech.spain.asnef.models.RpInputDevoRecord;
import fintech.spain.asnef.models.RpInputHeader;
import fintech.spain.asnef.models.RpInputHeaderRecord;
import fintech.spain.asnef.models.RpInputRecord;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.beanio.StreamFactory;
import org.beanio.Unmarshaller;
import org.beanio.builder.FixedLengthParserBuilder;
import org.beanio.builder.StreamBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Component
public class AsnefRpImport {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private LogRepository logRepository;

    void importRpFile(ImportFileCommand command) {
        Unmarshaller unmarshaller = streamFactory().createUnmarshaller("asnef");

        List<String> lines = lines(command.getFileId());

        RpInputHeader header = (RpInputHeader) unmarshaller.unmarshal(lines.get(0));

        switch (header.getType()) {
            case ERROR:
                error(command, (RpInputHeaderRecord) header, IntStream.range(1, lines.size() - 1).mapToObj(i -> (RpInputRecord) unmarshaller.unmarshal(lines.get(i))));
                break;
            case DEVO:
                devo(IntStream.range(1, lines.size() - 1).mapToObj(i -> (RpInputDevoRecord) unmarshaller.unmarshal(lines.get(i))));
                break;
            default:
                throw new IllegalStateException("Unknown import file rp type: " + header.getType());
        }
    }

    private void error(ImportFileCommand command, RpInputHeaderRecord headerRecord, Stream<RpInputRecord> inputRecordStream) {
        LogEntity log = logRepository.findOne(
            Entities.log.type.eq(LogType.NOTIFICA_RP)
                .and(Entities.log.preparedAt.eq(headerRecord.getInputFileHeaderRecord().getBatchDate()))
                .and(Entities.log.status.ne(LogStatus.DELETED)));

        Preconditions.checkNotNull(log, "Unable to find RP NOTIFICA log record for date: %s", headerRecord.getInputFileHeaderRecord().getBatchDate());

        Map<String, RpInputRecord> inputRecords = inputRecordStream
            .collect(Collectors.toMap(r -> r.getOriginalRecord().getIdentifierOfOperation(), r -> r));

        // EXHAUSTED status should not be rewritten
        log.getLogRows().stream()
            .filter(logRowEntity -> logRowEntity.getStatus() != LogRowStatus.EXHAUSTED)
            .forEach(logRow -> {
                Optional<RpInputRecord> maybeInputRecord = Optional.ofNullable(inputRecords.get(logRow.getOperationIdentifier()));

                logRow.setStatus(maybeInputRecord.map(r -> LogRowStatus.FAILED).orElse(LogRowStatus.SUCCEED));
                logRow.setIncomingRow(maybeInputRecord.map(JsonUtils::writeValueAsString).orElse(null));
            });

        log.setStatus(LogStatus.RESPONSE_RECEIVED);
        log.setResponseReceivedAt(command.getResponseReceivedAt());
        log.setIncomingFileId(command.getFileId());
    }

    // it means devolución
    private void devo(Stream<RpInputDevoRecord> inputRecordStream) {
        List<RpInputDevoRecord> inputRecords = inputRecordStream
            .collect(Collectors.toList());

        List<LocalDate> logDates = inputRecords.stream()
            .map(RpInputDevoRecord::getBatchDate)
            .collect(Collectors.toList());

        Map<LocalDate, LogEntity> logs = logRepository.findAll(
            Entities.log.type.eq(LogType.NOTIFICA_RP)
                .and(Entities.log.preparedAt.in(logDates))
                .and(Entities.log.status.ne(LogStatus.DELETED))).stream()
            .collect(Collectors.toMap(LogEntity::getPreparedAt, log -> log));

        inputRecords.forEach(inputRecord -> {
            LogEntity logEntity = logs.get(inputRecord.getBatchDate());

            Preconditions.checkNotNull(logEntity, "Unable to find RP NOTIFICA log record for date: %s", inputRecord.getBatchDate());

            Optional<LogRowEntity> maybeLogRow = logEntity.getLogRows().stream().filter(l -> l.getOperationIdentifier().equals(inputRecord.getIdentifierOfOperation())).findFirst();

            Preconditions.checkState(maybeLogRow.isPresent(), "Unable to find RP NOTIFICA log row record for operation identifier: %s", inputRecord.getIdentifierOfOperation());

            LogRowEntity logRow = maybeLogRow.get();

            logRow.setStatus(LogRowStatus.CANCELLED);
            logRow.setIncomingRow(JsonUtils.writeValueAsString(inputRecord));
        });
    }

    private StreamFactory streamFactory() {
        StreamFactory factory = StreamFactory.newInstance();
        factory.define(new StreamBuilder("asnef")
            .format("fixedlength")
            .parser(new FixedLengthParserBuilder())
            .addTypeHandler(BigDecimal.class, new RpBigDecimalTypeHandler())
            .addTypeHandler(LocalDate.class, new LocalDateTypeHandler())
            .addRecord(RpInputHeaderRecord.class)
            .addRecord(RpInputRecord.class)
            .addRecord(RpInputControlRecord.class)
            .addRecord(RpInputDevoHeaderRecord.class)
            .addRecord(RpInputDevoRecord.class)
            .addRecord(RpInputDevoControlRecord.class)
        );

        return factory;
    }

    private List<String> lines(Long fileId) {
        return fileStorageService.readContents(fileId, (Function<InputStream, List<String>>) this::lines);
    }

    @SneakyThrows
    private List<String> lines(InputStream input) {
        return IOUtils.readLines(input, StandardCharsets.US_ASCII);
    }
}
