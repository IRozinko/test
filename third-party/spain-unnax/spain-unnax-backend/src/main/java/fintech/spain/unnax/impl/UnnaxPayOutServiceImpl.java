package fintech.spain.unnax.impl;

import fintech.JsonUtils;
import fintech.PredicateBuilder;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.db.config.RequiresNew;
import fintech.spain.unnax.UnnaxPayOutService;
import fintech.spain.unnax.db.DisbursementQueueEntity;
import fintech.spain.unnax.db.DisbursementQueueRepository;
import fintech.spain.unnax.db.DisbursementQueueStatus;
import fintech.spain.unnax.db.TransferAutoEntity;
import fintech.spain.unnax.db.TransferAutoRepository;
import fintech.spain.unnax.db.TransferAutoStatus;
import fintech.spain.unnax.event.TransferAutoCreatedEvent;
import fintech.spain.unnax.event.TransferAutoProcessedEvent;
import fintech.spain.unnax.model.TransferAutoQuery;
import fintech.spain.unnax.model.TransferAutoUpdateResponse;
import fintech.spain.unnax.model.UnnaxErrorResponse;
import fintech.spain.unnax.model.UnnaxResponse;
import fintech.spain.unnax.transfer.TransferAutoUnnaxClient;
import fintech.spain.unnax.transfer.model.TransferAutoDetails;
import fintech.spain.unnax.transfer.model.TransferAutoRequest;
import fintech.spain.unnax.transfer.model.TransferAutoResponse;
import fintech.spain.unnax.transfer.model.TransferAutoState;
import fintech.spain.unnax.transfer.model.TransferAutoUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static fintech.spain.unnax.db.Entities.disbursementQueue;
import static fintech.spain.unnax.db.Entities.transferOut;

@Slf4j
@Service
@Transactional
public class UnnaxPayOutServiceImpl implements UnnaxPayOutService {

    private final TransferAutoUnnaxClient transferAutoUnnaxClient;
    private final TransferAutoRepository transferAutoRepository;
    private final DisbursementQueueRepository disbursementQueueRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TransactionTemplate tx;


    public UnnaxPayOutServiceImpl(TransferAutoUnnaxClient transferAutoUnnaxClient,
                                  TransferAutoRepository transferAutoRepository,
                                  DisbursementQueueRepository disbursementQueueRepository,
                                  ApplicationEventPublisher eventPublisher,
                                  @RequiresNew TransactionTemplate tx) {
        this.transferAutoUnnaxClient = transferAutoUnnaxClient;
        this.transferAutoRepository = transferAutoRepository;
        this.disbursementQueueRepository = disbursementQueueRepository;
        this.eventPublisher = eventPublisher;
        this.tx = tx;
    }

    @Override
    public Optional<TransferAutoResponse> transferOut(@Valid TransferAutoRequest request) {
        log.info("Executing Unnax transfer out request {}", request);
        Validate.isTrue(request.getOrderCode() != null,
            "Transfer out request does not contain order code {%s}. Request: {%s}", request.getOrderCode(), request);
        Optional<TransferAutoEntity> transferAutoEntity = findTransferOut(TransferAutoQuery.byOrderCode(request.getOrderCode()));
        TransferAutoEntity entity;
        if (transferAutoEntity.isPresent()) {
            entity = transferAutoEntity.get();
            Validate.isTrue(entity.getStatus() != TransferAutoStatus.PROCESSED,
                "Transfer out already processed. Entity: {%s}", entity);
        } else {
            entity = tx.execute(s -> transferAutoRepository.save(new TransferAutoEntity(request)));
        }
        UnnaxResponse<TransferAutoResponse> response = transferAutoUnnaxClient.transferAuto(request);
        if (response.isError()) {
            entity.setStatus(TransferAutoStatus.ERROR);
            entity.setErrorDetails(getErrorDetails(response.getErrorResponse()));
            transferAutoRepository.save(entity);
            log.error("Error while sending transfer through Unnax: {}", response.getErrorResponse());
            return Optional.empty();
        }

        return Optional.ofNullable(response.getResponse());
    }

    @Override
    public void retryTransferOut(String orderCode) {
        TransferAutoEntity transferOut = findRequiredTransferOut(TransferAutoQuery.byOrderCode(orderCode));
        Validate.isTrue(transferOut.getStatus() == TransferAutoStatus.ERROR || transferOut.getStatus() == TransferAutoStatus.PENDING,
            "Can't retry transfer in status {%s}, [%s]", transferOut.getStatus(), transferOut);
        UnnaxResponse<TransferAutoUpdateResponse> update = transferAutoUnnaxClient.update(orderCode, TransferAutoUpdateRequest.retry());
        Validate.isTrue(!update.isError(), "Can't retry transfer with order_code {%s}, reason: {%s}", orderCode, update.getErrorResponse());
    }

    @Override
    public List<DisbursementQueueEntity> getTransferOutQueue(LocalDateTime when) {
        return disbursementQueueRepository.findAll(disbursementQueue.status.eq(DisbursementQueueStatus.NEW));
    }

    @Override
    public List<TransferAutoEntity> findTransferOuts(TransferAutoQuery query) {
        return transferAutoRepository.findAll(toPredicate(query).allOf());
    }

    @Override
    @Transactional
    public void enqueueTransferOut(long disbursementId) {
        DisbursementQueueEntity entity = new DisbursementQueueEntity();
        entity.setDisbursementId(disbursementId);
        entity.setStatus(DisbursementQueueStatus.NEW);
        disbursementQueueRepository.save(entity);
    }

    @Override
    public void addAttempt(Long disbursementQueueElementId, DisbursementQueueStatus status) {
        DisbursementQueueEntity element = disbursementQueueRepository.findOne(disbursementQueueElementId);
        element.setAttempts(element.getAttempts() + 1);
        element.setStatus(status);
        disbursementQueueRepository.save(element);
    }

    @Override
    public Optional<TransferAutoEntity> findTransferOut(TransferAutoQuery query) {
        return Optional.ofNullable(transferAutoRepository.findOne(toPredicate(query).allOf()));
    }

    @Override
    public TransferAutoEntity findRequiredTransferOut(TransferAutoQuery query) {
        return findTransferOut(query)
            .orElseThrow(() -> new EntityNotFoundException(String.format("TransferOut not found query: %s", query)));
    }

    @Override
    public void syncTransfer(String orderCode) {
        UnnaxResponse<TransferAutoDetails> response = transferAutoUnnaxClient.getDetails(orderCode);
        Validate.isTrue(!response.isError(), "Error while syncing Unnax Transfer, {%s}", response.getErrorResponse());
        TransferAutoDetails details = response.getResponse();

        TransferAutoEntity transferOut = findRequiredTransferOut(TransferAutoQuery.byOrderCode(orderCode));

        if (transferOut.getStatus() != TransferAutoStatus.CREATED && transferOut.getStatus() != TransferAutoStatus.PENDING)
            return;

        switch (details.getState()) {
            case TransferAutoState.COMPLETED:
                eventPublisher.publishEvent(TransferAutoProcessedEvent.success(details));
                break;
            case TransferAutoState.CANCELED:
                eventPublisher.publishEvent(TransferAutoProcessedEvent.canceled(details));
                break;
        }
    }

    @Override
    public void cancelTransfer(String orderCode) {
        TransferAutoEntity transferOut = findRequiredTransferOut(TransferAutoQuery.byOrderCode(orderCode));
        Validate.isTrue(transferOut.getStatus() == TransferAutoStatus.PENDING
                || transferOut.getStatus() == TransferAutoStatus.CREATED,
            "Can't cancel transfer in status {%s}, [%s]", transferOut.getStatus(), transferOut);

        UnnaxResponse update = transferAutoUnnaxClient.update(orderCode, TransferAutoUpdateRequest.cancel());
        Validate.isTrue(!update.isError(), "Can't cancel transfer with order_code {%s}, reason: {%s}", orderCode, update.getErrorResponse());
    }

    @Override
    @EventListener
    public void handleTransferAutoCreated(TransferAutoCreatedEvent event) {
        TransferAutoEntity transferOut = findRequiredTransferOut(TransferAutoQuery.byOrderCode(event.getOrderId()));
        transferOut.setStatus(TransferAutoStatus.CREATED);
        transferOut.setSourceAccount(event.getSourceAccount());
        transferOut.setOrderCreatedAt(TimeMachine.now());
        transferAutoRepository.save(transferOut);
    }

    @Override
    @EventListener
    public void handleTransferAutoProcessed(TransferAutoProcessedEvent event) {
        TransferAutoEntity transferOut = findRequiredTransferOut(TransferAutoQuery.byOrderCode(event.getOrderId()));
        TransferAutoStatus status;

        if (event.isCancelled())
            status = TransferAutoStatus.CANCELED;
        else if (!event.isSuccess())
            status = TransferAutoStatus.ERROR;
        else
            status = TransferAutoStatus.PROCESSED;

        transferOut.setStatus(status);
        transferOut.setSourceAccount(event.getSourceAccount());
        transferOut.setErrorDetails(event.errorDetails());
        transferOut.setOrderProcessedAt(TimeMachine.now());
    }

    private PredicateBuilder toPredicate(TransferAutoQuery query) {
        return new PredicateBuilder()
            .addIfPresent(query.getOrderCode(), transferOut.orderCode::eq)
            .addIfPresent(query.getProcessedFromDate(), transferOut.orderProcessedAt::goe)
            .addIfPresent(query.getProcessedToDate(), transferOut.orderProcessedAt::loe)
            .addIfPresent(query.getStatus(), transferOut.status::eq);
    }

    private String getErrorDetails(UnnaxErrorResponse errorResponse) {
        return JsonUtils.writeValueAsString(errorResponse);
    }
}
