package fintech.spain.unnax;

import fintech.spain.unnax.db.DisbursementQueueEntity;
import fintech.spain.unnax.db.DisbursementQueueStatus;
import fintech.spain.unnax.db.TransferAutoEntity;
import fintech.spain.unnax.event.TransferAutoCreatedEvent;
import fintech.spain.unnax.event.TransferAutoProcessedEvent;
import fintech.spain.unnax.model.TransferAutoQuery;
import fintech.spain.unnax.transfer.model.TransferAutoRequest;
import fintech.spain.unnax.transfer.model.TransferAutoResponse;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Validated
public interface UnnaxPayOutService {

    Optional<TransferAutoResponse> transferOut(@Valid TransferAutoRequest request);

    void retryTransferOut(String orderCode);

    void syncTransfer(String orderCode);

    void cancelTransfer(String orderCode);

    List<DisbursementQueueEntity> getTransferOutQueue(LocalDateTime when);

    void enqueueTransferOut(long disbursementId);

    void addAttempt(Long disbursementQueueElementId, DisbursementQueueStatus status);

    Optional<TransferAutoEntity> findTransferOut(TransferAutoQuery query);

    TransferAutoEntity findRequiredTransferOut(TransferAutoQuery query);

    List<TransferAutoEntity> findTransferOuts(TransferAutoQuery query);

    void handleTransferAutoCreated(TransferAutoCreatedEvent event);

    void handleTransferAutoProcessed(TransferAutoProcessedEvent event);

}
