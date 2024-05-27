package fintech.spain.alfa.product.accounting

import fintech.accounting.AccountingReports
import fintech.accounting.AccountingService
import fintech.accounting.ReportQuery
import fintech.accounting.db.EntryRepository
import fintech.lending.creditline.TransactionConstants
import fintech.payments.InstitutionService
import fintech.spain.dc.command.RescheduleCommand
import fintech.spain.dc.command.ReschedulingPreviewCommand
import fintech.spain.dc.model.ReschedulingPreview
import fintech.spain.alfa.product.AbstractAlfaTest
import org.springframework.beans.factory.annotation.Autowired

import static fintech.DateUtils.date
import static fintech.TimeMachine.today
import static fintech.lending.creditline.TransactionConstants.TRANSACTION_SUB_TYPE_BANK_COMMISSION
import static fintech.lending.creditline.TransactionConstants.TRANSACTION_SUB_TYPE_UNIDENTIFIED_LIABILITIES_TO_CUSTOMERS

class AlfaAccountingTest extends AbstractAlfaTest {

    @Autowired
    AccountingReports accountingReports

    @Autowired
    AccountingService accountingService

    @Autowired
    InstitutionService institutionService

    @Autowired
    EntryRepository entryRepository

    def "issued loan"() {
        given:
        def term = 10
        def issueDate = today()
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(700.00, term, issueDate)

        when:
        def turnover = accountingReports.getTurnover(
            new ReportQuery(loanId: loan.getLoanId(), bookingDateFrom: issueDate, bookingDateTo: issueDate))
        loan = loan.loan

        then:
        turnover[Accounts.LOANS_PRINCIPAL].debit == loan.principalDisbursed
        turnover[Accounts.LOANS_PRINCIPAL].credit == 0.00
        turnover[Accounts.FUNDS_IN_TRANSFER].debit == loan.principalDisbursed
        turnover[Accounts.FUNDS_IN_TRANSFER].credit == loan.principalDisbursed
        turnover[Accounts.PRIMARY_BANK_ACCOUNT].debit == 0.00
        turnover[Accounts.PRIMARY_BANK_ACCOUNT].credit == loan.principalDisbursed

        turnover[Accounts.LOANS_CHARGED].debit == loan.interestApplied
        turnover[Accounts.LOANS_CHARGED].credit == 0.0

        turnover[Accounts.INITIAL_COMMISSION].debit == 0.0
        turnover[Accounts.INITIAL_COMMISSION].credit == loan.interestApplied
    }

    def "received customer's payment"() {
        given:
        def issueDate = today().minusDays(20)
        def penaltyDate = issueDate.plusDays(20)
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(700.00, 10L, issueDate)
            .extend(70.0, issueDate)
            .applyPenalty(penaltyDate)

        when:
        loan.repay(loan.loan.totalOutstanding, penaltyDate)
        def turnover = accountingReports.getTurnover(new ReportQuery())
        loan = loan.loan


        then:
        turnover[Accounts.LOANS_PRINCIPAL].debit == loan.principalDisbursed
        turnover[Accounts.LOANS_PRINCIPAL].credit == loan.principalDisbursed
        turnover[Accounts.FUNDS_IN_TRANSFER].debit == loan.principalDisbursed
        turnover[Accounts.FUNDS_IN_TRANSFER].credit == loan.principalDisbursed
        turnover[Accounts.LOANS_CHARGED].debit == loan.interestPaid + loan.penaltyPaid + 70.00
        turnover[Accounts.LOANS_CHARGED].credit == loan.interestPaid + loan.penaltyPaid + 70.00

        turnover[Accounts.INITIAL_COMMISSION].credit == loan.interestPaid
        turnover[Accounts.INITIAL_COMMISSION].debit == 0.0

        turnover[Accounts.PENALTIES].credit == loan.penaltyPaid
        turnover[Accounts.PENALTIES].debit == 0.0

        turnover[Accounts.PROLONGS].credit == 70.00
        turnover[Accounts.PROLONGS].debit == 0.0

        turnover[Accounts.PRIMARY_BANK_ACCOUNT].debit == loan.totalPaid
        turnover[Accounts.PRIMARY_BANK_ACCOUNT].credit == loan.principalDisbursed

    }

    def "received customer's prepayment"() {
        given:
        def issueDate = today().minusDays(30)

        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(300.00, 30L, issueDate)

        when:
        def prepaymentDate = issueDate.plusDays(5)
        def prePayment = loan.calculatePrepayment(prepaymentDate)
        loan.repay(prePayment.totalToPay, prepaymentDate)

        and:
        def turnover = accountingReports.getTurnover(new ReportQuery("loanId": loan.getLoanId()))

        then:
        turnover[Accounts.PRIMARY_BANK_ACCOUNT].debit == prePayment.totalToPay
        turnover[Accounts.PRIMARY_BANK_ACCOUNT].credit == prePayment.principalToPay
        turnover[Accounts.LOANS_PRINCIPAL].debit == prePayment.principalToPay
        turnover[Accounts.LOANS_PRINCIPAL].credit == prePayment.principalToPay
        turnover[Accounts.LOANS_CHARGED].debit == loan.loan.interestApplied + prePayment.prepaymentFeeToPay
        turnover[Accounts.LOANS_CHARGED].credit == prePayment.interestToPay + prePayment.prepaymentFeeToPay + prePayment.interestToWriteOff
        turnover[Accounts.INITIAL_COMMISSION].debit == 0.00
        turnover[Accounts.INITIAL_COMMISSION].credit == loan.loan.interestApplied
        turnover[Accounts.PREPAYMENT_COMMISSION].debit == 0.00
        turnover[Accounts.PREPAYMENT_COMMISSION].credit == prePayment.prepaymentFeeToPay

        turnover[Accounts.WRITE_OFF_COMMISSIONS_EARLY_PAYMENT].debit == prePayment.interestToWriteOff
        turnover[Accounts.WRITE_OFF_COMMISSIONS_EARLY_PAYMENT].credit == 0.00
    }

    def "received customer's payment (reschedule)"() {
        given:
        def issueDate = today().minusDays(90)
        def penaltyDate = issueDate.plusDays(90)
        def rescheduleDate = penaltyDate
        def paymentDate = penaltyDate

        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(300.00, 30L, issueDate)
            .applyPenalty(penaltyDate)

        ReschedulingPreview schedule = loan.generateReschedulePreview(new ReschedulingPreviewCommand()
            .setNumberOfPayments(2).setWhen(rescheduleDate)
        )

        when:
        loan.reschedule(new RescheduleCommand().setPreview(schedule).setWhen(rescheduleDate))
        def installment = loan.firstInstallment().get()
        loan.repay(installment.totalDue, paymentDate)

        and:
        def turnover = accountingReports.getTurnover(new ReportQuery())

        then:
        turnover[Accounts.PRIMARY_BANK_ACCOUNT].debit == installment.totalDue
        turnover[Accounts.PRIMARY_BANK_ACCOUNT].credit == loan.loan.principalDisbursed
        turnover[Accounts.LOANS_PRINCIPAL].debit == loan.loan.principalDisbursed
        turnover[Accounts.LOANS_PRINCIPAL].credit == installment.principalScheduled

        turnover[Accounts.LOANS_CHARGED].debit == loan.loan.interestApplied + installment.penaltyScheduled + installment.feeScheduled
        turnover[Accounts.LOANS_CHARGED].credit == installment.interestScheduled + installment.penaltyScheduled + installment.feeScheduled

        turnover[Accounts.INITIAL_COMMISSION].debit == 0.00
        turnover[Accounts.INITIAL_COMMISSION].credit == loan.loan.interestApplied

        turnover[Accounts.PENALTIES].debit == 0.00
        turnover[Accounts.PENALTIES].credit == installment.penaltyScheduled

    }

    def "Received unidentified payment"() {
        given:
        def issueDate = today()
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(300.00, 30L, issueDate)


        when:
        def payment = fintech.spain.alfa.product.testing.TestFactory.payments().newIncomingPayment(200.0, issueDate, "Foo")
        fintech.spain.alfa.product.testing.TestFactory.payments().addTransactionToPayment(payment.getPaymentId(), 200.0,
            TRANSACTION_SUB_TYPE_UNIDENTIFIED_LIABILITIES_TO_CUSTOMERS, loan.getTestClient().getClientId())

        and:
        def turnover = accountingReports.getTurnover(new ReportQuery())

        then:
        turnover[Accounts.PRIMARY_BANK_ACCOUNT].debit == payment.payment.amount
        turnover[Accounts.PRIMARY_BANK_ACCOUNT].credit == loan.loan.principalDisbursed
        turnover[Accounts.UNIDENTIFIED_LIABILITIES_TO_CUSTOMERS].debit == 0.00
        turnover[Accounts.UNIDENTIFIED_LIABILITIES_TO_CUSTOMERS].credit == payment.payment.amount
    }

    def "Received and refund overpayment"() {
        given:
        def issueDate = today()
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(300.00, 30L, issueDate)

        when:
        def paymentIn = fintech.spain.alfa.product.testing.TestFactory.payments().newIncomingPayment(500.0, issueDate, "Foo")
        def paymentOut = fintech.spain.alfa.product.testing.TestFactory.payments().newOutgoingPayment(95.0, issueDate, "Foo")
        loan.repay(500.0, issueDate)
        fintech.spain.alfa.product.testing.TestFactory.payments().refundOverpayment(paymentOut.getPaymentId(), loan.getTestClient().getClientId(), loan.getLoanId(), 95.0)

        and:
        def turnover = accountingReports.getTurnover(new ReportQuery())

        then:
        turnover[Accounts.PRIMARY_BANK_ACCOUNT].debit == paymentIn.payment.amount
        turnover[Accounts.PRIMARY_BANK_ACCOUNT].credit == loan.loan.principalDisbursed + paymentOut.payment.amount
        turnover[Accounts.OVERPAYMENT].debit == loan.loan.overpaymentRefunded
        turnover[Accounts.OVERPAYMENT].credit == loan.loan.overpaymentReceived
    }

    def "Overpayment splitted"() {
        given:
        def issueDate = today().minusDays(30)
        def loan1 = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(100.00, 10L, issueDate)
        loan1.repay(240.0, issueDate)

        def loan2 = loan1.getTestClient()
            .issueActiveLoan(100.00, 10L, issueDate)
            .extendUsingOverpayment(10.0, issueDate)
            .applyPenalty(issueDate.plusDays(20))

        loan2.repayUsingOverpayment(122.0, issueDate.plusDays(20))

        when:
        def turnover = accountingReports.getTurnover(new ReportQuery())
        loan1 = loan1.loan
        loan2 = loan2.loan

        then:
        turnover[Accounts.LOANS_PRINCIPAL].debit == loan1.principalDisbursed + loan2.principalDisbursed
        turnover[Accounts.LOANS_PRINCIPAL].credit == loan1.principalDisbursed + loan2.principalDisbursed

        turnover[Accounts.OVERPAYMENT].debit == loan2.overpaymentUsed
        turnover[Accounts.OVERPAYMENT].credit == loan1.overpaymentReceived

        turnover[Accounts.PRIMARY_BANK_ACCOUNT].debit == loan1.overpaymentReceived + loan1.totalPaid
        turnover[Accounts.PRIMARY_BANK_ACCOUNT].credit == loan1.principalDisbursed + loan2.principalDisbursed

        turnover[Accounts.LOANS_CHARGED].debit == loan1.interestPaid + loan2.interestPaid + loan2.penaltyPaid + 10
        turnover[Accounts.LOANS_CHARGED].credit == loan1.interestPaid + loan2.interestPaid + loan2.penaltyPaid  + 10

        turnover[Accounts.INITIAL_COMMISSION].debit == 0.00
        turnover[Accounts.INITIAL_COMMISSION].credit == loan1.interestPaid + loan2.interestPaid

        turnover[Accounts.PENALTIES].debit == 0.0
        turnover[Accounts.PENALTIES].credit == loan2.penaltyPaid

        turnover[Accounts.PROLONGS].debit == 0.00
        turnover[Accounts.PROLONGS].credit == 10.00

    }

    def "Overpayment splitted as prepayment"() {
        given:
        def issueDate = today().minusDays(10)
        def prepaymentDate = issueDate.plusDays(3)
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(100.00, 10L, issueDate)

        when:
        loan.repay(240.0, issueDate)
        loan = loan.getTestClient().
            issueActiveLoan(100.00, 10L, issueDate)

        def prePayment = loan.calculatePrepayment(prepaymentDate)
        loan.repayUsingOverpayment(prePayment.totalToPay, prepaymentDate)
        def turnover = accountingReports.getTurnover(new ReportQuery())

        then:
        turnover[Accounts.LOANS_PRINCIPAL].debit == 200.00
        turnover[Accounts.LOANS_PRINCIPAL].credit == 200.00

        turnover[Accounts.OVERPAYMENT].debit == prePayment.totalToPay
        turnover[Accounts.OVERPAYMENT].credit == 128.0

        turnover[Accounts.PRIMARY_BANK_ACCOUNT].debit == 240.0
        turnover[Accounts.PRIMARY_BANK_ACCOUNT].credit == 200.0

        turnover[Accounts.LOANS_CHARGED].debit == 12.0 + loan.loan.interestApplied + prePayment.prepaymentFeeToPay
        turnover[Accounts.LOANS_CHARGED].credit == 12.0 + prePayment.interestToPay + prePayment.prepaymentFeeToPay + prePayment.interestToWriteOff

        turnover[Accounts.INITIAL_COMMISSION].debit == 0.00
        turnover[Accounts.INITIAL_COMMISSION].credit == 12.0 + loan.loan.interestApplied

        turnover[Accounts.PREPAYMENT_COMMISSION].debit == 0.0
        turnover[Accounts.PREPAYMENT_COMMISSION].credit == prePayment.prepaymentFeeToPay

        turnover[Accounts.WRITE_OFF_COMMISSIONS_EARLY_PAYMENT].debit == prePayment.interestToWriteOff
        turnover[Accounts.WRITE_OFF_COMMISSIONS_EARLY_PAYMENT].credit == 0.0

    }

    def "overpayment splitted as reschedule"() {
        given:
        def issueDate = today().minusDays(90)
        def penaltyDate = issueDate.plusDays(90)
        def loan1 = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueActiveLoan(100.00, 10L, issueDate)

        loan1.repay(500.0, issueDate)
        def loan2 = loan1.getTestClient()
            .issueActiveLoan(300.00, 30L, issueDate)
            .applyPenalty(penaltyDate)

        ReschedulingPreview schedule = loan2.generateReschedulePreview(new ReschedulingPreviewCommand()
            .setNumberOfPayments(2).setWhen(penaltyDate)
        )
        loan2.reschedule(new RescheduleCommand().setPreview(schedule).setWhen(penaltyDate))

        def installment = loan2.firstInstallment().get()
        loan2.repayUsingOverpayment(installment.totalDue, penaltyDate)

        when:
        def turnover = accountingReports.getTurnover(new ReportQuery())

        then:
        turnover[Accounts.PRIMARY_BANK_ACCOUNT].debit == 500.0
        turnover[Accounts.PRIMARY_BANK_ACCOUNT].credit == loan1.loan.principalDisbursed + loan2.loan.principalDisbursed
        turnover[Accounts.LOANS_PRINCIPAL].debit == loan1.loan.principalDisbursed + loan2.loan.principalDisbursed
        turnover[Accounts.LOANS_PRINCIPAL].credit == loan1.loan.principalPaid + installment.principalScheduled

        turnover[Accounts.LOANS_CHARGED].debit == loan1.loan.interestPaid + loan2.loan.interestApplied + installment.penaltyScheduled + installment.feeScheduled
        turnover[Accounts.LOANS_CHARGED].credit == loan1.loan.interestPaid + installment.interestScheduled + installment.penaltyScheduled + installment.feeScheduled + installment.interestWrittenOff + installment.feeWrittenOff + installment.interestWrittenOff

        turnover[Accounts.INITIAL_COMMISSION].debit == 0.00
        turnover[Accounts.INITIAL_COMMISSION].credit == loan1.loan.interestPaid + loan2.loan.interestApplied

        turnover[Accounts.PENALTIES].debit == 0.00
        turnover[Accounts.PENALTIES].credit == installment.penaltyScheduled

    }

    def "bank commission booking"() {
        def payment
        when:
        def issueDate = date("2017-04-10")
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueLoan(BigDecimal.TEN, 1L, issueDate)

        payment = fintech.spain.alfa.product.testing.TestFactory.payments().newIncomingPayment(0.01, issueDate, "Foo")
        fintech.spain.alfa.product.testing.TestFactory.payments().addTransactionToPayment(payment.getPayment().getId(), 0.01,
            TRANSACTION_SUB_TYPE_BANK_COMMISSION, loan.getTestClient().getClientId())

        and:
        def turnover = accountingReports.getTurnover(new ReportQuery())
        def accountingAccountCode = institutionService.getAccount(payment.getPayment().getAccountId()).accountingAccountCode

        then:
        turnover[accountingAccountCode].debit == 0.01
        turnover[accountingAccountCode].credit == 0.00
        turnover[Accounts.ALL_TYPE_BANK_FEES].debit == 0.00
        turnover[Accounts.ALL_TYPE_BANK_FEES].credit == 0.01

        when:
        payment = fintech.spain.alfa.product.testing.TestFactory.payments().newOutgoingPayment(0.01, issueDate, "Foo")
        fintech.spain.alfa.product.testing.TestFactory.payments().autoProcessPendingPayments(issueDate)
        fintech.spain.alfa.product.testing.TestFactory.payments().addTransactionToPayment(payment.getPayment().getId(), 0.01,
            TRANSACTION_SUB_TYPE_BANK_COMMISSION, loan.getTestClient().getClientId())

        and:
        turnover = accountingReports.getTurnover(new ReportQuery())

        then:
        turnover[Accounts.ALL_TYPE_BANK_FEES].debit == 0.01
        turnover[Accounts.ALL_TYPE_BANK_FEES].credit == 0.01
        turnover[accountingAccountCode].debit == 0.01
        turnover[accountingAccountCode].credit == 0.01
    }

    def "write off"() {
        given:
        def issueDate = date("2017-03-10")
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueLoan(100.00, 10L, issueDate)
            .exportDisbursements(issueDate)
            .settleDisbursements(issueDate)
            .writeOff(issueDate, 50.0, 6.0)

        when:
        def turnover = accountingReports.getTurnover(new ReportQuery())

        then:
        turnover[Accounts.LOANS_PRINCIPAL].debit == 100.00
        turnover[Accounts.LOANS_PRINCIPAL].credit == 50.00

        turnover[Accounts.LOANS_CHARGED].debit == loan.loan.interestApplied
        turnover[Accounts.LOANS_CHARGED].credit == 6.00

        turnover[Accounts.FUNDS_IN_TRANSFER].debit == 100.00
        turnover[Accounts.FUNDS_IN_TRANSFER].credit == 100.00

        turnover[Accounts.WRITE_OFF_PRINCIPAL].debit == 50.0
        turnover[Accounts.WRITE_OFF_PRINCIPAL].credit == 0.0

        turnover[Accounts.WRITE_OFF_COMMISSIONS_DC_DISCOUNT].debit == 6.0
        turnover[Accounts.WRITE_OFF_COMMISSIONS_DC_DISCOUNT].credit == 0.0

    }

    def "write off sales portfolio"() {
        given:
        def issueDate = date("2017-03-10")
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueLoan(100.00, 10L, issueDate)
            .exportDisbursements(issueDate)
            .settleDisbursements(issueDate)
            .postToDc()
            .sellDebt()

        when:
        def turnover = accountingReports.getTurnover(new ReportQuery())

        then:
        turnover[Accounts.LOANS_PRINCIPAL].debit == 100.00
        turnover[Accounts.LOANS_PRINCIPAL].credit == 100.00

        turnover[Accounts.PRIMARY_BANK_ACCOUNT].debit == 0.0
        turnover[Accounts.PRIMARY_BANK_ACCOUNT].credit == 100.0

        turnover[Accounts.LOANS_CHARGED].debit == loan.loan.interestApplied
        turnover[Accounts.LOANS_CHARGED].credit == 12

        turnover[Accounts.FUNDS_IN_TRANSFER].debit == 100.00
        turnover[Accounts.FUNDS_IN_TRANSFER].credit == 100.00

        turnover[Accounts.WRITE_OFF_SALES_PORTFOLIO].debit == 112.0
        turnover[Accounts.WRITE_OFF_SALES_PORTFOLIO].credit == 0.0

    }

    def "principal viventor"() {
        given:
        def issueDate = date("2017-03-10")
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly()
            .issueLoan(100.00, 10L, issueDate)
            .exportDisbursements(issueDate)
            .settleDisbursements(issueDate)


        when:
        def payment = fintech.spain.alfa.product.testing.TestFactory.payments().newIncomingPayment(500.0, issueDate, "T" + loan.getTestClient().getClient().getNumber())
        fintech.spain.alfa.product.testing.TestFactory.payments().addTransactionToPayment(payment.paymentId, 112.0,
            TransactionConstants.TRANSACTION_SUB_TYPE_PRINCIPAL_VIVENTOR, loan.getTestClient().getClientId())
        def turnover = accountingReports.getTurnover(new ReportQuery())

        then:
        turnover[Accounts.LOANS_PRINCIPAL].debit == 100.00
        turnover[Accounts.LOANS_PRINCIPAL].credit == 0.00

        turnover[Accounts.VIVENTOR_PRINCIPAL].debit == 0.00
        turnover[Accounts.VIVENTOR_PRINCIPAL].credit == 112.00

    }
}
