package fintech.payments.impl

import fintech.payments.InstitutionService
import fintech.payments.PaymentService
import fintech.payments.UnnaxCallbackRequestFactory
import fintech.payments.commands.AddPaymentCommand
import fintech.payments.model.InstitutionAccount
import fintech.payments.model.PaymentType
import fintech.spain.unnax.event.TransferAutoProcessedEvent
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static fintech.payments.impl.AccountingUnnaxPaymentListener.INCOMING_PAYMENT_KEY_SUFFIX
import static fintech.payments.impl.AccountingUnnaxPaymentListener.OUTGOING_PAYMENT_KEY_SUFFIX

class AccountingUnnaxPaymentListenerTest extends Specification {

    @Subject
    AccountingUnnaxPaymentListener paymentListener
    InstitutionService institutionService
    PaymentService paymentService

    void setup() {
        institutionService = Mock(InstitutionService)
        paymentService = Mock(PaymentService)
        paymentListener = new AccountingUnnaxPaymentListener(institutionService, paymentService)
    }

    def "HandlePaymentProcessed - Event is not success"() {
        given:
        def event = new TransferAutoProcessedEvent(UnnaxCallbackRequestFactory.transferAutoProcessed(false))

        when:
        paymentListener.handlePaymentProcessed(event)

        then:
        0 * institutionService.findAccountByNumber(event.getSourceAccount())
        0 * institutionService.findAccountByNumber(event.getCustomerAccount())
        0 * paymentService.addPayment(_ as AddPaymentCommand)
    }

    @Unroll
    def "HandlePaymentProcessed - Source: #source.isPresent() Dest: #destination.isPresent()"() {
        given:
        def event = new TransferAutoProcessedEvent(UnnaxCallbackRequestFactory.transferAutoProcessed())

        when:
        paymentListener.handlePaymentProcessed(event)

        then:
        1 * institutionService.findAccountByNumber(event.getSourceAccount()) >> source
        1 * institutionService.findAccountByNumber(event.getCustomerAccount()) >> destination

        number_of_payments * paymentService.addPayment(_ as AddPaymentCommand)

        where:
        source                                     | destination                                | number_of_payments
        Optional.empty()                           | Optional.empty()                           | 0
        Optional.empty()                           | Optional.of(new InstitutionAccount(id: 1)) | 0
        Optional.of(new InstitutionAccount(id: 1)) | Optional.empty()                           | 0
        Optional.of(new InstitutionAccount(id: 1)) | Optional.of(new InstitutionAccount(id: 2)) | 2
    }

    def "HandlePaymentProcessed - Two payments created"() {
        given:
        def event = new TransferAutoProcessedEvent(UnnaxCallbackRequestFactory.transferAutoProcessed(true))
        def outgoingCommand = AddPaymentCommand.fromUnnaxEvent(1, PaymentType.OUTGOING, event, OUTGOING_PAYMENT_KEY_SUFFIX)
        outgoingCommand.setRequireManualStatus(true)
        def incomingCommand = AddPaymentCommand.fromUnnaxEvent(2, PaymentType.INCOMING, event, INCOMING_PAYMENT_KEY_SUFFIX)
        incomingCommand.setRequireManualStatus(true)

        when:
        paymentListener.handlePaymentProcessed(event)

        then:
        1 * institutionService.findAccountByNumber(event.getSourceAccount()) >> Optional.of(new InstitutionAccount(id: 1))
        1 * institutionService.findAccountByNumber(event.getCustomerAccount()) >> Optional.of(new InstitutionAccount(id: 2))
        1 * paymentService.addPayment(outgoingCommand)
        1 * paymentService.addPayment(incomingCommand)
    }

}
