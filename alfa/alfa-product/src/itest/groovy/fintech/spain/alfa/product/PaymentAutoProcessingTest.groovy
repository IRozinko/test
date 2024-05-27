package fintech.spain.alfa.product

import fintech.lending.creditline.TransactionConstants
import fintech.payments.commands.AddPaymentCommand
import fintech.payments.model.PaymentType
import fintech.transactions.TransactionType

import static fintech.DateUtils.date
import static fintech.spain.alfa.product.testing.TestLoan.expectedExtensionPrice

class PaymentAutoProcessingTest extends AbstractAlfaTest {

    def setup() {
        // add random client/loan to make sure auto processing is matching right loans
        fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().issueActiveLoan(300.00, 30, date("2018-01-01"))
    }

    def "loan repayment - payment details with DNI"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        def loan = client.registerDirectly().issueActiveLoan(300.00, 30, date("2018-01-01"))

        expect:
        loan.balance.totalDue > 0.0

        when:
        def payment = fintech.spain.alfa.product.testing.TestFactory.payments().newIncomingPayment(loan.balance.totalDue, date("2018-02-01"), client.dni)

        then:
        loan.balance.totalDue > 0.0
        payment.isPending()

        when:
        fintech.spain.alfa.product.testing.TestFactory.payments().autoProcessPendingPayments()

        then:
        loan.isPaid()
        payment.isProcessed()
    }

    def "loan repayment - payment details with client number"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        def loan = client.registerDirectly().issueActiveLoan(300.00, 30, date("2018-01-01"))

        when:
        fintech.spain.alfa.product.testing.TestFactory.payments().newIncomingPayment(loan.balance.totalDue, date("2018-02-01"), client.getClient().getNumber())
        fintech.spain.alfa.product.testing.TestFactory.payments().autoProcessPendingPayments()

        then:
        loan.isPaid()
    }

    def "loan repayment - payment details with loan number"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        def loan = client.registerDirectly().issueActiveLoan(300.00, 30, date("2018-01-01"))

        when:
        fintech.spain.alfa.product.testing.TestFactory.payments().newIncomingPayment(loan.balance.totalDue, date("2018-02-01"), loan.getLoan().getNumber())
        fintech.spain.alfa.product.testing.TestFactory.payments().autoProcessPendingPayments()

        then:
        loan.isPaid()
    }

    def "loan repayment - not processed if contains extension keyword"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().issueActiveLoan(300.00, 30, date("2018-01-01"))

        when:
        def payment = fintech.spain.alfa.product.testing.TestFactory.payments().newIncomingPayment(loan.balance.totalDue, date("2018-02-01"), fintech.spain.alfa.product.payments.processors.LoanRepaymentProcessor.EXTENSION_KEYWORD + " " + loan.getLoan().getNumber())
        fintech.spain.alfa.product.testing.TestFactory.payments().autoProcessPendingPayments()

        then:
        payment.isManual()
        payment.getTransactions().isEmpty()
    }

    def "loan extension"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        def loan = client.registerDirectly().issueActiveLoan(300.00, 30, date("2018-01-01"))

        expect:
        loan.getLoan().getMaturityDate() == date("2018-01-31")

        when:
        def payment = fintech.spain.alfa.product.testing.TestFactory.payments().newIncomingPayment(expectedExtensionPrice(300.00, 30), date("2018-01-31").plusDays(30), client.dni)
        fintech.spain.alfa.product.testing.TestFactory.payments().autoProcessPendingPayments(date("2018-01-31").plusDays(30))

        then:
        payment.isProcessed()
        payment.getTransactions()[0].getTransactionType() == TransactionType.LOAN_EXTENSION
        loan.getLoan().getMaturityDate() == date("2018-01-31").plusDays(30)
    }

    def "loan extension ignored if loan will stay overdue, process as partial payment"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        def loan = client.registerDirectly().issueActiveLoan(300.00, 30, date("2018-01-01"))
        def extensionPrice = expectedExtensionPrice(300.00, 30)

        expect:
        loan.getLoan().getMaturityDate() == date("2018-01-31")

        when:
        def payment = fintech.spain.alfa.product.testing.TestFactory.payments().newIncomingPayment(extensionPrice, date("2018-01-31").plusDays(31), client.dni)
        fintech.spain.alfa.product.testing.TestFactory.payments().autoProcessPendingPayments(date("2018-01-31").plusDays(31))

        then:
        assert loan.getLoan().getMaturityDate() == date("2018-01-31")
        assert payment.getTransactions().isEmpty()
        assert payment.isManual()
    }

    def "payment not auto-processed if does not match exactly with total due"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        def loan = client.registerDirectly().issueActiveLoan(300.00, 30, date("2018-01-01"))

        when:
        def payment = fintech.spain.alfa.product.testing.TestFactory.payments().newIncomingPayment(loan.getLoan().getTotalDue() - 1g, date("2018-01-01").plusDays(30), client.dni)
        fintech.spain.alfa.product.testing.TestFactory.payments().autoProcessPendingPayments(date("2018-01-01").plusDays(30))

        then:
        assert payment.getTransactions().isEmpty()
        assert payment.isManual()
    }

    def "unknown payment details - manual payment"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        def loan = client.registerDirectly().issueActiveLoan(300.00, 30, date("2018-01-01"))

        when:
        def payment = fintech.spain.alfa.product.testing.TestFactory.payments().newIncomingPayment(loan.balance.totalDue, date("2018-02-01"), "unkown")
        fintech.spain.alfa.product.testing.TestFactory.payments().autoProcessPendingPayments()

        then:
        !loan.isPaid()
        payment.isManual()
    }

    def "disbursement settlement"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        def loan = client.signUp().toLoanWorkflow().runAll().exportDisbursement().toLoan()

        expect:
        loan.balance.cashOut == 0.0

        when:
        def payment = fintech.spain.alfa.product.testing.TestFactory.payments().newOutgoingPayment(loan.balance.principalDisbursed, date("2018-02-01"), loan.getLoan().getNumber())
        fintech.spain.alfa.product.testing.TestFactory.payments().autoProcessPendingPayments()

        then:
        loan.balance.cashOut == 100.00
        payment.isProcessed()
    }

    def "inter company payment"() {
        given:
        def payments = fintech.spain.alfa.product.testing.TestFactory.payments()
        def payment = payments.newPayment(new AddPaymentCommand()
            .setAccountId(payments.getAccountId(fintech.spain.alfa.product.payments.PaymentsSetup.BANK_ACCOUNT_BBVA))
            .setCounterpartyAccount(fintech.spain.alfa.product.payments.PaymentsSetup.BANK_ACCOUNT_BANKIA)
            .setValueDate(date("2018-01-01"))
            .setPaymentType(PaymentType.OUTGOING)
            .setAmount(10000.00)
            .setDetails("")
            .setKey(UUID.randomUUID().toString())
        )

        when:
        fintech.spain.alfa.product.testing.TestFactory.payments().autoProcessPendingPayments()

        then:
        payment.isProcessed()
        with(payment.getTransactions()[0]) {
            transactionType == TransactionType.PAYMENT
            transactionSubType == TransactionConstants.TRANSACTION_SUB_TYPE_INTER_COMPANY_TRANSFER
        }
    }
}
