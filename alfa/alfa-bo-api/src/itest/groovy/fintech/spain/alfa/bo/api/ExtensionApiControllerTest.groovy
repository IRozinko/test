package fintech.spain.alfa.bo.api

import fintech.DateUtils
import fintech.bo.api.model.payments.AddExtensionTransactionRequest
import fintech.strategy.model.ExtensionOffer
import spock.lang.Specification
import spock.lang.Subject

import java.time.temporal.ChronoUnit

class ExtensionApiControllerTest extends Specification {

    @Subject
    ExtensionApiController controller = new ExtensionApiController()

    def "AddExtensionTransactionRequest correctly mapped to ApplyAndRepayExtensionFeeCommand"() {
        given:
        def req = new AddExtensionTransactionRequest()
            .setPaymentId(1)
            .setPaymentAmount(100.00)
            .setLoanId(2)
            .setOverpaymentAmount(50.00)
            .setValueDate(DateUtils.date("2019-08-07"))
            .setComments("comment")

        def offer = new ExtensionOffer()
            .setPeriodCount(14)
            .setPeriodUnit(ChronoUnit.DAYS)
            .setPrice(100.00)

        when:
        def cmd = controller.toExtensionCommand(req, offer)

        then:
        with(cmd) {
            loanId == req.loanId
            paymentId == req.paymentId
            extensionOffer == offer
            paymentAmount == req.paymentAmount
            overpaymentAmount == req.overpaymentAmount
            valueDate == req.valueDate
            comments == req.comments
        }
    }

}
