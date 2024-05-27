package fintech.payments.impl

import fintech.payments.BaseSpecification
import fintech.payments.InstitutionService
import fintech.payments.UnnaxCallbackRequestFactory
import fintech.payments.commands.AddInstitutionCommand
import fintech.payments.db.PaymentRepository
import fintech.payments.model.PaymentStatus
import fintech.payments.model.PaymentStatusDetail
import fintech.payments.model.PaymentType
import fintech.spain.unnax.event.TransferAutoProcessedEvent
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

class AccountingUnnaxPaymentListenerITest extends BaseSpecification {

    final String source = "ES6821000844240200657804 "
    final String customer = "ES1846190210811618055380"

    @Subject
    @Autowired
    AccountingUnnaxPaymentListener paymentListener

    @Autowired
    PaymentRepository paymentRepository

    @Autowired
    InstitutionService institutionService

    Long institutionId
    Long sourceId
    Long customerId

    def setup() {
        institutionId = institutionService.addInstitution(addInstitutionCommand())
        sourceId = institutionService.findAccountByNumber(source).get().getId()
        customerId = institutionService.findAccountByNumber(customer).get().getId()
    }


    def "HandlePaymentProcessed"() {
        given:
        def event = new TransferAutoProcessedEvent(UnnaxCallbackRequestFactory.transferAutoProcessed())
        event.setSourceAccount(source)
        event.setCustomerAccount(customer)

        when:
        paymentListener.handlePaymentProcessed(event)
        def outgoing = paymentRepository.findByAccountId(sourceId)
        def incoming = paymentRepository.findByAccountId(customerId)

        then:
        outgoing.size() == 1
        outgoing[0].paymentType == PaymentType.OUTGOING
        outgoing[0].status == PaymentStatus.OPEN
        outgoing[0].statusDetail == PaymentStatusDetail.MANUAL

        incoming.size() == 1
        incoming[0].paymentType == PaymentType.INCOMING
        incoming[0].status == PaymentStatus.OPEN
        incoming[0].statusDetail == PaymentStatusDetail.MANUAL
    }

    def addInstitutionCommand() {
        return AddInstitutionCommand.builder()
            .name("test")
            .institutionType("Bank")
            .paymentMethods([])
            .accounts([AddInstitutionCommand.Account.builder()
                           .accountingAccountCode("ES4500810200210003223833")
                           .accountNumber(source)
                           .primary(true).build(),
                       AddInstitutionCommand.Account.builder()
                           .accountingAccountCode("ES4500810200210003223833")
                           .accountNumber(customer).build()])
            .build()
    }
}
