package fintech.spain.unnax.transfer.impl;

import fintech.JsonUtils;
import fintech.TimeMachine;
import fintech.db.config.RequiresNew;
import fintech.spain.unnax.event.TransferAutoCreatedEvent;
import fintech.spain.unnax.event.TransferAutoProcessedEvent;
import fintech.spain.unnax.model.TransferAutoUpdateResponse;
import fintech.spain.unnax.model.UnnaxErrorResponse;
import fintech.spain.unnax.model.UnnaxResponse;
import fintech.spain.unnax.transfer.TransferAutoUnnaxClient;
import fintech.spain.unnax.transfer.model.TransferAutoDetails;
import fintech.spain.unnax.transfer.model.TransferAutoRequest;
import fintech.spain.unnax.transfer.model.TransferAutoResponse;
import fintech.spain.unnax.transfer.model.TransferAutoState;
import fintech.spain.unnax.transfer.model.TransferAutoUpdateRequest;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

@Component
public class MockTransferAutoUnnaxClient implements TransferAutoUnnaxClient {

    private static final String DEFAULT_SOURCE_ACCOUNT = "ES1800813631919552191544";
    public static boolean SEND_CREATED_CALLBACK = true;
    public static boolean THROW_EXCEPTION = false;

    private final ScheduledExecutorService scheduler;
    private final Map<String, TransferAutoRequest> requestMap;
    private final ApplicationEventPublisher eventPublisher;
    private final TransactionTemplate tx;

    public MockTransferAutoUnnaxClient(ApplicationEventPublisher eventPublisher, @RequiresNew TransactionTemplate tx) {
        this.eventPublisher = eventPublisher;
        this.requestMap = new ConcurrentHashMap<>();
        this.tx = tx;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    @Override
    @SneakyThrows
    public UnnaxResponse<TransferAutoResponse> transferAuto(TransferAutoRequest request) {
        if (THROW_EXCEPTION)
            throw new IllegalStateException("Unnax exception");

        if (requestMap.values().stream().anyMatch(req -> Objects.equals(req.getBankOrderCode(), request.getBankOrderCode())))
            return errorResponse("{ \"bank_order_code\": \"Bank order code already exists\" }", "");

        requestMap.put(request.getOrderCode(), request);

        if (SEND_CREATED_CALLBACK) {
            tx.execute(s -> {
                eventPublisher.publishEvent(createdEvent(request));
                return null;
            });
        }

        scheduler.schedule(() -> eventPublisher.publishEvent(toProcessedEvent()), 2, SECONDS);

        return new UnnaxResponse<>(new TransferAutoResponse().setDestinationAccount(request.getDestinationAccount())
            .setBankOrderCode(request.getBankOrderCode())
            .setCurrency(request.getCurrency())
            .setTime(TimeMachine.now().toLocalTime())
            .setDate(TimeMachine.today())
            .setAmount(request.getAmount())
            .setCustomerCode(request.getCustomerCode())
            .setOrderCode(request.getOrderCode())
            .setSourceAccount(DEFAULT_SOURCE_ACCOUNT));
    }

    @Override
    public UnnaxResponse<TransferAutoDetails> getDetails(String orderCode) {
        if (!requestMap.containsKey(orderCode))
            return errorResponse("", "No encontrado.");

        TransferAutoRequest order = requestMap.get(orderCode);
        return new UnnaxResponse<>(TransferAutoDetails.builder()
            .orderCode(order.getOrderCode())
            .bankOrderCode(order.getBankOrderCode())
            .amount(order.getAmount())
            .currency(order.getCurrency())
            .concept(order.getConcept())
            .sourceIp("192.168.1.1")
            .customerCode(order.getCustomerCode())
            .customerNames(order.getCustomerNames())
            .sourceAccount(order.getSourceAccount())
            .destinationAccount(order.getDestinationAccount())
            .callbackUrl(null)
            .state(TransferAutoState.NEW)
            .build()
        );
    }

    @Override
    public UnnaxResponse<TransferAutoUpdateResponse> update(String orderCode, TransferAutoUpdateRequest requestBody) {
        if (!requestMap.containsKey(orderCode))
            return errorResponse("", "No encontrado.");
        return new UnnaxResponse<>(new TransferAutoUpdateResponse());
    }

    private UnnaxResponse errorResponse(String jsonData, String detail) {
        return new UnnaxResponse<>(
            new UnnaxErrorResponse()
                .setStatus("fail")
                .setDetail(detail)
                .setData(JsonUtils.readTree(jsonData))
        );
    }

    private TransferAutoCreatedEvent createdEvent(TransferAutoRequest request) {
        return new TransferAutoCreatedEvent("", request.getOrderCode());
    }

    private TransferAutoProcessedEvent toProcessedEvent() {
        return TransferAutoProcessedEvent.success(new TransferAutoDetails());
    }


}
