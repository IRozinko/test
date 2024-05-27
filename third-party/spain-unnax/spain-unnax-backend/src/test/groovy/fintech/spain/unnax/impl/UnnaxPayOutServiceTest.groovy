package fintech.spain.unnax.impl

import com.querydsl.core.types.Predicate
import fintech.spain.unnax.callback.model.CallbackRequest
import fintech.spain.unnax.callback.model.TransferAutoCreatedCallbackData
import fintech.spain.unnax.callback.model.TransferAutoProcessedCallbackData
import fintech.spain.unnax.db.DisbursementQueueRepository
import fintech.spain.unnax.db.TransferAutoEntity
import fintech.spain.unnax.db.TransferAutoRepository
import fintech.spain.unnax.db.TransferAutoStatus
import fintech.spain.unnax.event.TransferAutoCreatedEvent
import fintech.spain.unnax.event.TransferAutoProcessedEvent
import fintech.spain.unnax.model.UnnaxErrorResponse
import fintech.spain.unnax.model.UnnaxResponse
import fintech.spain.unnax.transfer.TransferAutoUnnaxClient
import fintech.spain.unnax.transfer.model.TransferAutoDetails
import fintech.spain.unnax.transfer.model.TransferAutoRequest
import fintech.spain.unnax.transfer.model.TransferAutoResponse
import fintech.spain.unnax.transfer.model.TransferAutoState
import fintech.spain.unnax.transfer.model.TransferAutoUpdateRequest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.SimpleTransactionStatus
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate
import java.time.LocalTime

class UnnaxPayOutServiceTest extends Specification {

    @Subject
    UnnaxPayOutServiceImpl unnaxService

    TransferAutoUnnaxClient transferAutoUnnaxClient
    TransferAutoRepository transferOutRepository
    ApplicationEventPublisher eventPublisher
    DisbursementQueueRepository disbursementQueueRepository
    TransactionTemplate tx

    void setup() {
        transferAutoUnnaxClient = Mock(TransferAutoUnnaxClient.class)
        transferOutRepository = Mock(TransferAutoRepository.class)
        eventPublisher = Mock(ApplicationEventPublisher.class)
        disbursementQueueRepository = Mock(DisbursementQueueRepository.class)
        PlatformTransactionManager txManager = Stub(PlatformTransactionManager)
        tx = new TransactionTemplate(txManager)

        txManager.getTransaction(_ as TransactionDefinition) >> new SimpleTransactionStatus()
        unnaxService = new UnnaxPayOutServiceImpl(transferAutoUnnaxClient, transferOutRepository,
            disbursementQueueRepository, eventPublisher, tx)
    }

    def "TransferOut"() {
        given:
        def req = new TransferAutoRequest()
        req.orderCode = ""

        when:
        unnaxService.transferOut(req)

        then:
        1 * transferOutRepository.save(_ as TransferAutoEntity)
        1 * transferAutoUnnaxClient.transferAuto(_ as TransferAutoRequest) >> new UnnaxResponse<>(new TransferAutoResponse())
    }



    def "transferAutoCreatedHandler"() {
        given:
        TransferAutoCreatedEvent event = new TransferAutoCreatedEvent(
            new CallbackRequest()
                .setData(new TransferAutoCreatedCallbackData()
                .setDate(LocalDate.of(2018, 5, 5))
                .setTime(LocalTime.of(5, 5, 5))
                .setAmount(100)
                .setOrderId("1")
                .setSourceAccount("ES1521047047332490142917")
            )
        )
        TransferAutoEntity entity = new TransferAutoEntity()

        when:
        unnaxService.handleTransferAutoCreated(event)

        then:
        1 * transferOutRepository.findOne(_ as Predicate) >> entity
        entity.sourceAccount == "ES1521047047332490142917"
        entity.status == TransferAutoStatus.CREATED
        entity.orderCreatedAt
    }

    def "transferAutoProcessedHandler"() {
        given:
        TransferAutoProcessedEvent event = new TransferAutoProcessedEvent(
            new CallbackRequest()
                .setData(new TransferAutoProcessedCallbackData()
                .setSuccess(true)
                .setAmount(100)
                .setDate(LocalDate.of(2018, 5, 5))
                .setTime(LocalTime.of(5, 5, 5))
                .setSrcAccountBalance(100)
                .setOrderId("1")
                .setSourceAccount("ES1521047047332490142917")
            )
        )
        TransferAutoEntity entity = new TransferAutoEntity()

        when:
        unnaxService.handleTransferAutoProcessed(event)

        then:
        1 * transferOutRepository.findOne(_ as Predicate) >> entity
        entity.sourceAccount == "ES1521047047332490142917"
        entity.status == TransferAutoStatus.PROCESSED
        entity.orderProcessedAt
    }

    def "sync transfer"() {
        given:
        TransferAutoEntity entity = new TransferAutoEntity()
        entity.setStatus(TransferAutoStatus.CREATED)

        def details = TransferAutoDetails.builder().state(TransferAutoState.COMPLETED).amount(1000).build();

        when:
        unnaxService.syncTransfer("12345")

        then:
        1 * transferOutRepository.findOne(_ as Predicate) >> entity
        1 * transferAutoUnnaxClient.getDetails(_ as String) >> new UnnaxResponse<>(details)
        1 * eventPublisher.publishEvent(_ as TransferAutoProcessedEvent)

        when:
        details = TransferAutoDetails.builder().state(TransferAutoState.CANCELED).amount(1000).build()
        unnaxService.syncTransfer("12345")

        then:
        1 * transferOutRepository.findOne(_ as Predicate) >> entity
        1 * transferAutoUnnaxClient.getDetails(_ as String) >> new UnnaxResponse<>(details)
        1 * eventPublisher.publishEvent(_ as TransferAutoProcessedEvent)

        when:
        entity.setStatus(TransferAutoStatus.CANCELED)
        details = TransferAutoDetails.builder().state(TransferAutoState.CANCELED).amount(1000).build()
        unnaxService.syncTransfer("12345")

        then:
        1 * transferOutRepository.findOne(_ as Predicate) >> entity
        1 * transferAutoUnnaxClient.getDetails("12345") >> new UnnaxResponse<>(details)
        0 * eventPublisher.publishEvent(_ as TransferAutoProcessedEvent)
    }

    def "Cancel Transfer"() {
        given:
        TransferAutoEntity entity = new TransferAutoEntity()
        entity.setStatus(TransferAutoStatus.CREATED)

        when:
        unnaxService.cancelTransfer("12345")

        then:
        1 * transferOutRepository.findOne(_ as Predicate) >> entity
        1 * transferAutoUnnaxClient.update("12345", _ as TransferAutoUpdateRequest) >> new UnnaxResponse<>("Cancelled")

        when:
        entity.setStatus(TransferAutoStatus.PROCESSED)
        unnaxService.cancelTransfer("12345")

        then:
        1 * transferOutRepository.findOne(_ as Predicate) >> entity
        thrown IllegalArgumentException
    }

    def "TransferOutProcessErrorResp"() {
        given:
        def transferAutoRequest = new TransferAutoRequest()
        transferAutoRequest.orderCode = ""
        TransferAutoEntity transferAutoEntity = new TransferAutoEntity()
        transferAutoEntity.status = TransferAutoStatus.CREATED
        UnnaxResponse<TransferAutoResponse> unnaxResponse = new UnnaxResponse(new UnnaxErrorResponse("test error response"));

        when:
        Optional<TransferAutoResponse> result = unnaxService.transferOut(transferAutoRequest)

        then:
        transferAutoUnnaxClient.transferAuto(_ as TransferAutoRequest) >> unnaxResponse
        transferOutRepository.save(_ as TransferAutoEntity) >> transferAutoEntity

        assert !result.isPresent()

    }

}
