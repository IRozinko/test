package fintech.lending.creditline.impl

import fintech.lending.BaseSpecification
import fintech.lending.core.CreditLineLoanHelper
import fintech.lending.core.LoanHolder
import fintech.lending.core.invoice.InvoiceService
import fintech.lending.core.invoice.db.InvoiceItemType
import fintech.lending.core.loan.LoanService
import fintech.lending.core.loan.commands.ApplyFeeCommand
import fintech.lending.core.loan.commands.ApplyInterestCommand
import fintech.lending.core.loan.commands.ApplyPenaltyCommand
import fintech.lending.core.loan.commands.RepayLoanCommand
import fintech.lending.core.overpayment.ApplyOverpaymentCommand
import fintech.lending.core.overpayment.OverpaymentService
import fintech.lending.core.repayments.LoanRepaymentStrategy
import fintech.lending.creditline.settings.CreditLineRepaymentSettings
import fintech.transactions.Balance
import fintech.transactions.TransactionQuery
import fintech.transactions.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject
import spock.lang.Unroll

import static fintech.lending.core.invoice.commands.GeneratedInvoice.GeneratedInvoiceItem
import static fintech.lending.creditline.settings.CreditLineRepaymentSettings.InvoiceItemTypeSubTypePair.type

class LoanRepaymentStrategyTest extends BaseSpecification {

    @Subject
    @Autowired
    LoanRepaymentStrategy repaymentStrategy

    @Autowired
    CreditLineLoanHelper loanHelper

    @Autowired
    TransactionService transactionService

    @Autowired
    LoanService loanService

    @Autowired
    InvoiceService invoiceService

    @Autowired
    OverpaymentService overpaymentService

    LoanHolder loanHolder

    def setup() {
        loanHelper.init()
        loanHolder = new LoanHolder()
        loanHelper.applyAndDisburse(loanHolder)

        loanHelper.closePeriodSilently(loanHolder.issueDate.minusDays(1))
    }

    @Unroll
    def "repaying outstanding when no active invoices"() {
        given:
        def paymentDate = loanHolder.issueDate.plusDays(60)
        loanService.applyPenalty(new ApplyPenaltyCommand(loanId: loanHolder.loanId, amount: penaltyAmount, valueDate: loanHolder.issueDate, comments: "any comment"))
        loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, interestAmount, loanHolder.issueDate.plusDays(15)))
        loanHelper.applyFee(loanHolder, loanHolder.issueDate.plusDays(15), feeAmount)

        when:
        def paymentId = loanHelper.addPayment(paymentDate, paymentAmount)
        repaymentStrategy.repay(new RepayLoanCommand(loanId: loanHolder.loanId, paymentId: paymentId, paymentAmount: paymentAmount))

        then:
        with(loanBalance()) {
            penaltyPaid == penaltyAmountPaid
            interestPaid == interestAmountPaid
            feePaid == feeAmountPaid
            principalPaid == principalAmountPaid
            totalPaid == paymentAmount
        }

        where:
        paymentAmount | penaltyAmount | penaltyAmountPaid | interestAmount | interestAmountPaid | feeAmount | feeAmountPaid | principalAmountPaid
        1.0           | 0.0           | 0.0               | 0.0            | 0.0                | 0.0       | 0.0           | 1.0
        5.0           | 10.0          | 5.0               | 10.0           | 0.0                | 10.0      | 0.0           | 0.0
        10.0          | 10.0          | 10.0              | 10.0           | 0.0                | 10.0      | 0.0           | 0.0
        15.0          | 10.0          | 10.0              | 10.0           | 5.0                | 10.0      | 0.0           | 0.0
        20.0          | 10.0          | 10.0              | 10.0           | 10.0               | 10.0      | 0.0           | 0.0
        25.0          | 10.0          | 10.0              | 10.0           | 10.0               | 10.0      | 5.0           | 0.0
        30.0          | 10.0          | 10.0              | 10.0           | 10.0               | 10.0      | 10.0          | 0.0
        35.0          | 10.0          | 10.0              | 10.0           | 10.0               | 10.0      | 10.0          | 5.0
        40.0          | 10.0          | 10.0              | 10.0           | 10.0               | 10.0      | 10.0          | 10.0
    }

    def "amounts less than 1 cent are written off, except for principal"() {
        given:
        def paymentDate = loanHolder.issueDate.plusDays(60)
        loanService.applyPenalty(new ApplyPenaltyCommand(loanId: loanHolder.loanId, amount: 10.001, valueDate: loanHolder.issueDate, comments: "any comment"))
        loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, 10.002, loanHolder.issueDate.plusDays(15)))
        loanHelper.applyFee(loanHolder, loanHolder.issueDate.plusDays(15), 10.003)

        when:
        def paymentId = loanHelper.addPayment(paymentDate, 40.001)
        repaymentStrategy.repay(new RepayLoanCommand(loanId: loanHolder.loanId, paymentId: paymentId, paymentAmount: 40.001))

        then:
        with(loanBalance()) {
            penaltyPaid == 10.00
            penaltyWrittenOff == 0.001
            interestPaid == 10.00
            interestWrittenOff == 0.002
            feePaid == 10.00
            feeWrittenOff == 0.003
            principalPaid == 10.001
            principalWrittenOff == 0.00
            totalPaid == 40.001
        }
    }

    def "invoice has only principal"() {
        given:
        BigDecimal paymentAmount = 100.00
        def invoiceId = loanHelper.createFirstInvoice(loanHolder, [new GeneratedInvoiceItem(type: InvoiceItemType.PRINCIPAL, amount: 50.00)])
        def paymentId = loanHelper.addPayment(loanHolder.issueDate.plusDays(60), paymentAmount)
        assert loanBalance().totalPaid == 0.00

        when:
        repaymentStrategy.repay(new RepayLoanCommand(loanId: loanHolder.loanId, paymentId: paymentId, paymentAmount: 100.00))

        then:
        assert loanBalance().principalPaid == paymentAmount
        assert loanBalance().totalPaid == paymentAmount

        and:
        assert invoiceBalance(invoiceId).totalPaid == 50.00
    }

    def "invoice has only interest"() {
        given:
        loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, 10.00, loanHolder.issueDate))
        def invoiceId = loanHelper.createFirstInvoice(loanHolder, [new GeneratedInvoiceItem(type: InvoiceItemType.INTEREST, amount: 10.00)])
        def paymentId = loanHelper.addPayment(loanHolder.issueDate.plusDays(60), 100.00)

        when:
        repaymentStrategy.repay(new RepayLoanCommand(loanId: loanHolder.loanId, paymentId: paymentId, paymentAmount: 100.00))

        then:
        with(loanBalance()) {
            principalPaid == 90.00
            interestPaid == 10.00
            totalPaid == 100.00
        }

        and:
        invoiceBalance(invoiceId).totalPaid == 10.00
    }

    def "invoice has only penalty"() {
        given:
        loanService.applyPenalty(new ApplyPenaltyCommand(loanId: loanHolder.loanId, amount: 10.00, valueDate: loanHolder.issueDate, comments: "any comment"))
        def invoiceId = loanHelper.createFirstInvoice(loanHolder, [new GeneratedInvoiceItem(type: InvoiceItemType.PENALTY, amount: 10.00)])
        def paymentId = loanHelper.addPayment(loanHolder.issueDate.plusDays(60), 100.00)

        when:
        repaymentStrategy.repay(new RepayLoanCommand(loanId: loanHolder.loanId, paymentId: paymentId, paymentAmount: 100.00))

        then:
        with(loanBalance()) {
            principalPaid == 90.00
            penaltyPaid == 10.00
            totalPaid == 100.00
        }

        and:
        invoiceBalance(invoiceId).totalPaid == 10.00
    }

    def "invoice has only fees"() {
        given:
        loanService.applyFee(new ApplyFeeCommand(loanId: loanHolder.loanId, amount: 10.00, valueDate: loanHolder.issueDate))
        def invoiceId = loanHelper.createFirstInvoice(loanHolder, [new GeneratedInvoiceItem(type: InvoiceItemType.FEE, amount: 10.00)])
        def paymentId = loanHelper.addPayment(loanHolder.issueDate.plusDays(60), 100.00)

        when:
        repaymentStrategy.repay(new RepayLoanCommand(loanId: loanHolder.loanId, paymentId: paymentId, paymentAmount: 100.00))

        then:
        with(loanBalance()) {
            principalPaid == 90.00
            feePaid == 10.00
            totalPaid == 100.00
        }

        and:
        invoiceBalance(invoiceId).totalPaid == 10.00
    }

    def "principal, interest, penalties, fees"() {
        when:
        loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, 11.00, loanHolder.issueDate))
        loanService.applyPenalty(new ApplyPenaltyCommand(loanId: loanHolder.loanId, amount: 22.00, valueDate: loanHolder.issueDate, comments: "any comment"))
        loanService.applyFee(new ApplyFeeCommand(loanId: loanHolder.loanId, amount: 15.00, valueDate: loanHolder.issueDate))
        def invoiceId = loanHelper.createFirstInvoice(loanHolder, [
            new GeneratedInvoiceItem(type: InvoiceItemType.PRINCIPAL, amount: 50.00),
            new GeneratedInvoiceItem(type: InvoiceItemType.INTEREST, amount: 11.00),
            new GeneratedInvoiceItem(type: InvoiceItemType.PENALTY, amount: 22.00),
            new GeneratedInvoiceItem(type: InvoiceItemType.FEE, amount: 15.00)
        ])
        def paymentId = loanHelper.addPayment(loanHolder.issueDate.plusDays(60), 100.00)

        then:
        with(loanBalance()) {
            totalPaid == 0.00
            interestOutstanding == 11.00
            penaltyOutstanding == 22.00
            feeOutstanding == 15.00
        }

        when:
        repaymentStrategy.repay(new RepayLoanCommand(loanId: loanHolder.loanId, paymentId: paymentId, paymentAmount: 95.00))

        then:
        with(loanBalance()) {
            interestPaid == 11.00
            penaltyPaid == 22.00
            principalPaid == 50.00
            feePaid == 12.00
            totalPaid == 95.00
        }

        and:
        with(invoiceBalance(invoiceId)) {
            interestPaid == 11.00
            penaltyPaid == 22.00
            principalPaid == 50.00
            feePaid == 12.00
            totalPaid == 95.00
        }
    }

    // TODO invoice distribution order is not implemented
    def "repayment distribution order"() {
        given:
        def settings = loanHelper.getProductSettings()
        settings.repaymentSettings = new CreditLineRepaymentSettings(
            invoiceDistributionOrder: [
                type(InvoiceItemType.PENALTY),
                type(InvoiceItemType.INTEREST),
                type(InvoiceItemType.PRINCIPAL),
                type(InvoiceItemType.FEE)]
        )
        loanHelper.updateProductSettings(settings)

        when:
        loanHelper.loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, 11.00, loanHolder.issueDate))
        loanHelper.loanService.applyPenalty(new ApplyPenaltyCommand(loanId: loanHolder.loanId, amount: 22.00, valueDate: loanHolder.issueDate, comments: "any comment"))
        loanHelper.loanService.applyFee(new ApplyFeeCommand(loanId: loanHolder.loanId, amount: 10.00, valueDate: loanHolder.issueDate))
        loanHelper.createFirstInvoice(loanHolder, [
            new GeneratedInvoiceItem(type: InvoiceItemType.PRINCIPAL, amount: 150.00),
            new GeneratedInvoiceItem(type: InvoiceItemType.INTEREST, amount: 11.00),
            new GeneratedInvoiceItem(type: InvoiceItemType.PENALTY, amount: 22.00),
            new GeneratedInvoiceItem(type: InvoiceItemType.FEE, amount: 10.00)
        ])
        def paymentId = loanHelper.addPayment(loanHolder.issueDate.plusDays(60), paymentAmount)

        then:
        with(loanBalance()) {
            totalPaid == 0.00
            interestOutstanding == 11.00
            penaltyOutstanding == 22.00
            feeOutstanding == 10.00
        }

        when:
        repaymentStrategy.repay(new RepayLoanCommand(loanId: loanHolder.loanId, paymentId: paymentId, paymentAmount: paymentAmount))

        then:
        with(loanBalance()) {
            interestPaid == interestPaidAmount
            penaltyPaid == penaltyPaidAmount
            principalPaid == principalPaidAmount
            feePaid == feePaidAmount
            totalPaid == paymentAmount
        }

        where:
        paymentAmount | interestPaidAmount | penaltyPaidAmount | principalPaidAmount | feePaidAmount
        0.01          | 0.00               | 0.01              | 0.00                | 0.00
        22.01         | 0.01               | 22.00             | 0.00                | 0.00
        33.01         | 11.00              | 22.00             | 0.01                | 0.00
        183.00        | 11.00              | 22.00             | 150.00              | 0.00
        185.00        | 11.00              | 22.00             | 150.00              | 2.00
        193.00        | 11.00              | 22.00             | 150.00              | 10.00
    }

    @Unroll
    def "distributed to multiple invoices"() {
        given:
        loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, 11.00, loanHolder.issueDate))
        loanService.applyPenalty(new ApplyPenaltyCommand(loanId: loanHolder.loanId, amount: 22.00, valueDate: loanHolder.issueDate, comments: "any comment"))
        loanService.applyFee(new ApplyFeeCommand(loanId: loanHolder.loanId, amount: 10.00, valueDate: loanHolder.issueDate))
        def invoice1Id = loanHelper.createFirstInvoice(loanHolder, [
            new GeneratedInvoiceItem(type: InvoiceItemType.INTEREST, amount: 5.00),
            new GeneratedInvoiceItem(type: InvoiceItemType.PENALTY, amount: 12.00),
            new GeneratedInvoiceItem(type: InvoiceItemType.FEE, amount: 10.00)
        ])
        def invoice2Id = loanHelper.createSecondInvoice(loanHolder, [
            new GeneratedInvoiceItem(type: InvoiceItemType.INTEREST, amount: 3.00),
            new GeneratedInvoiceItem(type: InvoiceItemType.PENALTY, amount: 8.00),
            new GeneratedInvoiceItem(type: InvoiceItemType.PRINCIPAL, amount: 150.00),
        ])
        def paymentId = loanHelper.addPayment(loanHolder.issueDate.plusDays(70), paymentAmount)

        when:
        repaymentStrategy.repay(new RepayLoanCommand(loanId: loanHolder.loanId, paymentId: paymentId, paymentAmount: paymentAmount))

        then:
        with(invoiceBalance(invoice1Id)) {
            interestPaid == interestPaidAmount1
            penaltyPaid == penaltyPaidAmount1
            principalPaid == principalPaidAmount1
            feePaid == feePaidAmount1
            totalPaid == totalPaid1
        }
        with(invoiceBalance(invoice2Id)) {
            interestPaid == interestPaidAmount2
            penaltyPaid == penaltyPaidAmount2
            principalPaid == principalPaidAmount2
            feePaid == feePaidAmount2
            totalPaid == totalPaid2
        }

        where:
        paymentAmount | interestPaidAmount1 | penaltyPaidAmount1 | feePaidAmount1 | principalPaidAmount1 | totalPaid1 | interestPaidAmount2 | penaltyPaidAmount2 | feePaidAmount2 | principalPaidAmount2 | totalPaid2
        1.00          | 0.00                | 1.00               | 0.00           | 0.00                 | 1.0        | 0.00                | 0.00               | 0.00           | 0.00                 | 0.0
        500.00        | 5.00                | 12.00              | 10.00          | 0.00                 | 27.0       | 3.00                | 8.00               | 0.00           | 150.00               | 161.0
        510.00        | 5.00                | 12.00              | 10.00          | 0.00                 | 27.0       | 3.00                | 8.00               | 0.00           | 150.00               | 161.0
    }

    def "overpayment applied"() {
        given:
        loanHolder = new LoanHolder(offeredPrincipal: 500.00)
        loanHelper.applyAndDisburse(loanHolder)
        def paymentDate = loanHolder.issueDate.plusDays(45)
        loanHelper.applyInterest(loanHolder, loanHolder.issueDate, 20.00)
        loanHelper.applyFee(loanHolder, loanHolder.issueDate, 5.00)

        and:
        loanHelper.createFirstInvoice(loanHolder,
            [new GeneratedInvoiceItem(type: InvoiceItemType.PRINCIPAL, amount: 50.00),
             new GeneratedInvoiceItem(type: InvoiceItemType.INTEREST, amount: 5.00),
             new GeneratedInvoiceItem(type: InvoiceItemType.FEE, amount: 5.00)]
        )

        when:
        def paymentId = loanHelper.addPayment(paymentDate, 535.00)

        then:
        with(loanBalance()) {
            totalPaid == 0.00
            totalOutstanding == 525.00
        }

        when:
        repaymentStrategy.repay(new RepayLoanCommand(loanId: loanHolder.loanId, paymentId: paymentId, paymentAmount: 535.00))

        then:
        with(loanBalance()) {
            totalPaid == 525.00
            interestPaid == 20.00
            principalPaid == 500.00
            feePaid == 5.00
            overpaymentAvailable == 10.00
        }
    }

    def "overpayment used"() {
        given:
        loanHolder = new LoanHolder(offeredPrincipal: 500.00)
        loanHelper.applyAndDisburse(loanHolder)
        loanHelper.applyInterest(loanHolder, loanHolder.issueDate.plusDays(15), 25.00)
        loanHelper.applyFee(loanHolder, loanHolder.issueDate.plusDays(15), 5.00)

        and:
        def invoice1Id = loanHelper.createFirstInvoice(loanHolder,
            [new GeneratedInvoiceItem(type: InvoiceItemType.PRINCIPAL, amount: 50.00),
             new GeneratedInvoiceItem(type: InvoiceItemType.INTEREST, amount: 5.00),
             new GeneratedInvoiceItem(type: InvoiceItemType.FEE, amount: 3.00)]
        )
        def invoice1 = invoiceService.get(invoice1Id)

        when:
        def paymentId = loanHelper.addPayment(invoice1.dueDate, 550.00)
        repaymentStrategy.repay(new RepayLoanCommand(loanId: loanHolder.loanId, paymentId: paymentId, paymentAmount: 550.00, valueDate: invoice1.dueDate))

        then:
        with(loanBalance()) {
            overpaymentAvailable == 20.00
            overpaymentReceived == 20.00
            overpaymentUsed == 0.00
            totalPaid == 530.00
            totalOutstanding == 0.00
            cashIn == 550.00
        }

        when:
        loanHelper.disburse(loanHolder)
        def invoice2Id = loanHelper.createSecondInvoice(loanHolder,
            [new GeneratedInvoiceItem(type: InvoiceItemType.PRINCIPAL, amount: 20.00)]
        )
        def invoice2 = invoiceService.get(invoice2Id)
        repaymentStrategy.repay(new RepayLoanCommand(loanId: loanHolder.loanId, overpaymentAmount: 20.00, valueDate: invoice2.dueDate))

        then:
        with(loanBalance()) {
            overpaymentAvailable == 0.00
            overpaymentReceived == 20.00
            overpaymentUsed == 20.00
            totalPaid == 550.00
            totalOutstanding == 480.00
            cashIn == 550.00
        }

        when:
        repaymentStrategy.repay(new RepayLoanCommand(loanId: loanHolder.loanId, overpaymentAmount: 10.00, valueDate: invoice2.dueDate))

        then:
        thrown(IllegalArgumentException)
    }

    def "amount validation"() {
        given:
        loanHolder = new LoanHolder(offeredPrincipal: 500.00)
        loanHelper.applyAndDisburse(loanHolder)
        loanHelper.applyInterest(loanHolder, loanHolder.issueDate.plusDays(15), 25.00)

        and:
        def invoice1Id = loanHelper.createFirstInvoice(loanHolder,
            [new GeneratedInvoiceItem(type: InvoiceItemType.PRINCIPAL, amount: 50.00),
             new GeneratedInvoiceItem(type: InvoiceItemType.INTEREST, amount: 5.00)]
        )
        def invoice1 = loanHelper.invoiceService.get(invoice1Id)
        def paymentId = loanHelper.addPayment(invoice1.dueDate, 400.00)

        when:
        repaymentStrategy.repay(new RepayLoanCommand(loanId: loanHolder.loanId, paymentId: paymentId, paymentAmount: 600.00, valueDate: invoice1.dueDate))

        then:
        thrown(IllegalArgumentException)
        with(loanBalance()) {
            overpaymentReceived == 0.00
            totalPaid == 0.00
            totalOutstanding == 525.00
        }

        when:
        repaymentStrategy.repay(new RepayLoanCommand(loanId: loanHolder.loanId, overpaymentAmount: 10.00, valueDate: invoice1.dueDate))

        then:
        thrown(IllegalArgumentException)
        with(loanBalance()) {
            overpaymentReceived == 0.00
            totalPaid == 0.00
            totalOutstanding == 525.00
        }
    }

    def "apply overpayment < 1 cent to invoice"() {
        given:
        loanHolder = new LoanHolder(offeredPrincipal: 500.00)
        loanHelper.applyAndDisburse(loanHolder)
        loanHelper.applyInterest(loanHolder, loanHolder.issueDate.plusDays(15), 25.55)

        and:
        def overpaymentId = loanHelper.addPayment(loanHolder.issueDate.plusDays(15), 0.0001)
        overpaymentService.applyOverpayment(new ApplyOverpaymentCommand(clientId: loanHolder.clientId, loanId: loanHolder.loanId, paymentId: overpaymentId, amount: 0.0001, comments: "test"))

        and:
        def invoiceId = loanHelper.createFirstInvoice(loanHolder,
            [new GeneratedInvoiceItem(type: InvoiceItemType.PRINCIPAL, amount: 49.45),
             new GeneratedInvoiceItem(type: InvoiceItemType.INTEREST, amount: 5.55)]
        )
        def invoice = loanHelper.invoiceService.get(invoiceId)

        when:
        loanService.repayLoan(new RepayLoanCommand(loanId: loanHolder.loanId, paymentAmount: 0.00, overpaymentAmount: 0.0001, valueDate: invoice.invoiceDate))
        invoice = invoiceService.get(invoiceId)

        then:
        with(invoiceBalance(invoiceId)) {
            interestPaid == 0.0001
            principalPaid == 0.00
            totalPaid == 0.0001
        }
        with(invoice) {
            total == 55.00
            totalPaid == 0.0001
            with(items[0]) {
                amount == 49.45
                amountPaid == 0.00
                amountOutstanding == 49.45
            }
            with(items[1]) {
                amount == 5.55
                amountPaid == 0.0001
                amountOutstanding == 5.5499
            }
        }
        with(loanBalance()) {
            totalPaid == 0.0001
            totalOutstanding == 525.5499
            overpaymentAvailable == 0.00
        }
    }

    Balance loanBalance() {
        transactionService.getBalance(TransactionQuery.byLoan(loanHolder.loanId))
    }

    Balance invoiceBalance(invoiceId) {
        transactionService.getBalance(TransactionQuery.byInvoice(invoiceId))
    }
}
