package fintech.payments

import fintech.payments.commands.AddPaymentCommand
import fintech.payments.model.PaymentStatus
import fintech.payments.model.PaymentStatusDetail
import fintech.payments.model.PaymentType
import org.apache.commons.lang3.RandomStringUtils

import static fintech.DateUtils.date

class PaymentTest extends BaseSpecification {

    def "Add payment"() {
        when:
        def id = paymentService.addPayment(addPaymentCommand())

        then:
        def payment = paymentService.getPayment(id)
        payment.accountId == institution.accounts[0].id
        payment.paymentType == PaymentType.INCOMING
        payment.statusDetail == PaymentStatusDetail.PENDING
        payment.reference == "REF"
        payment.details == "Repayment"
        payment.amount == 10.00g
        payment.valueDate == date("2016-01-01")
    }

    def "Void payment"() {
        given:
        def id = paymentService.addPayment(addPaymentCommand())

        when:
        paymentService.voidPayment(id)

        then:
        paymentService.getPayment(id).statusDetail == PaymentStatusDetail.VOIDED

        when:
        paymentService.unvoidPayment(id)

        then:
        paymentService.getPayment(id).statusDetail == PaymentStatusDetail.MANUAL
        paymentService.getPayment(id).status == PaymentStatus.OPEN
    }

    private AddPaymentCommand addPaymentCommand() {
        return new AddPaymentCommand(
            accountId: institution.accounts[0].id,
            paymentType: PaymentType.INCOMING,
            details: "Repayment",
            reference: "REF",
            amount: 10.00g,
            key: RandomStringUtils.randomAlphanumeric(10),
            valueDate: date("2016-01-01")
        )
    }
}
