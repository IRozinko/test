package fintech.payments.impl

import fintech.TimeMachine
import fintech.payments.DisbursementService
import fintech.payments.InstitutionService
import fintech.payments.PaymentService
import fintech.payments.UnnaxCallbackRequestFactory
import fintech.payments.commands.AddPaymentCommand
import fintech.payments.model.Disbursement
import fintech.payments.model.InstitutionAccount
import fintech.payments.model.PaymentType
import fintech.spain.unnax.event.TransferAutoProcessedEvent
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

import static fintech.payments.DisbursementService.DisbursementQuery.byReference

class DisbursementUnnaxPaymentListenerTest extends Specification {

    @Subject
    DisbursementUnnaxPaymentListener paymentListener

    InstitutionService institutionService
    PaymentService paymentService
    DisbursementService disbursementService

    void setup() {
        institutionService = Mock(InstitutionService)
        paymentService = Mock(PaymentService)
        disbursementService = Mock(DisbursementService)
        paymentListener = new DisbursementUnnaxPaymentListener(institutionService, disbursementService, paymentService)
    }

    def "HandlePaymentProcessed - Event is not success"() {
        given:
        def event = new TransferAutoProcessedEvent(UnnaxCallbackRequestFactory.transferAutoProcessed(false))

        when:
        paymentListener.handlePaymentProcessed(event)

        then:
        1 * disbursementService.getOptional(byReference(event.getOrderId())) >> Optional.of(new Disbursement(id: 1L))
        0 * institutionService.findAccountByNumber(event.getSourceAccount())
        0 * paymentService.addPayment(_ as AddPaymentCommand)
        0 * paymentService.autoProcess(_ as Long, _ as LocalDate)
    }

    def "HandlePaymentProcessed - Disbursement not found"() {
        given:
        def event = new TransferAutoProcessedEvent(UnnaxCallbackRequestFactory.transferAutoProcessed())

        when:
        paymentListener.handlePaymentProcessed(event)

        then:
        1 * disbursementService.getOptional(byReference(event.getOrderId())) >> Optional.empty()
        0 * institutionService.findAccountByNumber(event.getSourceAccount())
        0 * paymentService.addPayment(_ as AddPaymentCommand)
        0 * paymentService.autoProcess(_ as Long, _ as LocalDate)
    }

    def "HandlePaymentProcessed - Source account not found"() {
        given:
        def event = new TransferAutoProcessedEvent(UnnaxCallbackRequestFactory.transferAutoProcessed())

        when:
        paymentListener.handlePaymentProcessed(event)

        then:
        1 * disbursementService.getOptional(byReference(event.getOrderId())) >> Optional.of(new Disbursement())
        1 * institutionService.findAccountByNumber(event.getSourceAccount()) >> Optional.empty()
        thrown IllegalArgumentException
        0 * paymentService.addPayment(_ as AddPaymentCommand)
        0 * paymentService.autoProcess(_ as Long, _ as LocalDate)
    }

    def "HandlePaymentProcessed - Payment created"() {
        given:
        def event = new TransferAutoProcessedEvent(UnnaxCallbackRequestFactory.transferAutoProcessed())

        when:
        paymentListener.handlePaymentProcessed(event)

        then:
        1 * disbursementService.getOptional(byReference(event.getOrderId())) >> Optional.of(new Disbursement())
        1 * institutionService.findAccountByNumber(event.getSourceAccount()) >> Optional.of(new InstitutionAccount(id: 1))
        1 * paymentService.addPayment(AddPaymentCommand.fromUnnaxEvent(1, PaymentType.OUTGOING, event)) >> 1
        1 * paymentService.autoProcess(1, TimeMachine.today())
    }

}
