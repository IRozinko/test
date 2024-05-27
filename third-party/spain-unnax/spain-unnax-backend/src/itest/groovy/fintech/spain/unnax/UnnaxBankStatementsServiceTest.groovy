package fintech.spain.unnax

import fintech.JsonUtils
import fintech.TimeMachine
import fintech.spain.unnax.callback.model.BankStatementsUploadedData
import fintech.spain.unnax.callback.model.CallbackRequest
import fintech.spain.unnax.db.BankStatementsRequestRepository
import fintech.spain.unnax.db.BankStatementsRequestStatus
import fintech.spain.unnax.event.BankStatementsUploadedEvent
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Subject

class UnnaxBankStatementsServiceTest extends AbstractBaseSpecification {

    static final SOURCE_IBAN = "ES1801821797340203757723"

    @Subject
    @Autowired
    UnnaxBankStatementsService bankStatementsService

    @Autowired
    BankStatementsRequestRepository bankStatementsRequestRepository

    @Autowired
    ApplicationEventPublisher applicationEventPublisher

    def "request bank statements"() {
        when:
        bankStatementsService.requestStatementsUpload(TimeMachine.today(), TimeMachine.today(), SOURCE_IBAN)

        then:
        bankStatementsRequestRepository.count() == 1
        bankStatementsService.lastSuccessRequestedDateByIban().isEmpty()
        def statement = bankStatementsRequestRepository.findAll()[0]
        with(statement) {
            status == BankStatementsRequestStatus.NEW
            fromDate == TimeMachine.today()
            toDate == TimeMachine.today()
            iban == SOURCE_IBAN
            requestCode
            !processedAt
        }
        def data = new BankStatementsUploadedData().setLink("fake_url").setRequestCode(statement.requestCode)

        when:
        def callback = new CallbackRequest()
            .setResponseId("b9f174fc-7763-4853-96f1-ab8bfdbc66a4")
            .setSignature("e8f88f223f7a6269966a74f404fa24ae039302e8")
            .setTriggeredEvent("event_pdfs_uploaded_link")
            .setService("pdfs_uploaded_link")
            .setEnvironment("unnax_integration_aws")
            .setTraceIdentifier("b9f174fc-7763-4853-96f1-ab8bfdbc66a4")
            .setDate(TimeMachine.now())
            .setData(JsonUtils.readTree(data))
        applicationEventPublisher.publishEvent(new BankStatementsUploadedEvent(callback))

        then:
        with(bankStatementsRequestRepository.findByRequestCode(statement.requestCode).get()) {
            status == BankStatementsRequestStatus.ERROR
            error
            processedAt
        }
    }
}
