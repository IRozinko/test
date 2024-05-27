package fintech.spain.alfa.product


import fintech.spain.alfa.product.cms.CmsSetup

import static fintech.BigDecimalUtils.amount
import static fintech.TimeMachine.today
import static fintech.lending.core.loan.LoanStatus.CLOSED
import static AlfaConstants.ATTACHMENT_TYPE_INVOICE

class RepaymentWithInvoiceTest extends AbstractAlfaTest {

    def "first loan issued and repaid with overpayment has one invoice"() {
        when:
        fintech.spain.alfa.product.testing.TestLoan loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUpWithApplication()
            .addPrimaryBankAccount()
            .issueActiveLoan(amount(2000), 15, today()).repay(amount(4000), today().plusDays(15))

        then:
        assert loan.status == CLOSED
        assert loan.overpaymentReceived == 1650.00
        assert loan.attachmentCount(ATTACHMENT_TYPE_INVOICE) == 1

        when:
        fintech.spain.alfa.product.testing.TestClient client = loan.toClient()

        then:
        assert client.emailCount(CmsSetup.CLIENT_PAYMENT_RECEIVED_NOTIFICATION) == 1
    }
}
