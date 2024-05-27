package fintech.lending.core.invoice

import fintech.lending.BaseSpecification
import fintech.lending.core.CreditLineLoanHelper
import fintech.lending.core.LoanHolder
import fintech.lending.core.invoice.commands.CloseInvoiceCommand
import fintech.lending.core.invoice.commands.GeneratedInvoice
import fintech.lending.core.invoice.db.InvoiceEntity
import fintech.lending.core.invoice.db.InvoiceRepository
import fintech.lending.core.loan.commands.ApplyFeeCommand
import fintech.lending.core.loan.commands.ApplyInterestCommand
import fintech.lending.core.loan.commands.ApplyPenaltyCommand
import fintech.payments.PaymentService
import fintech.payments.commands.AddPaymentCommand
import fintech.payments.model.PaymentType
import fintech.transactions.AddTransactionCommand
import fintech.transactions.TransactionService
import fintech.transactions.TransactionType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Subject

import java.time.LocalDateTime

import static fintech.BigDecimalUtils.amount
import static fintech.DateUtils.date
import static fintech.RandomUtils.randomId
import static fintech.lending.core.CreditLineLoanHelper.PRODUCT_ID
import static fintech.lending.core.invoice.InvoiceQuery.byLoanOpen
import static fintech.lending.core.invoice.InvoiceStatusDetail.CANCELLED
import static fintech.lending.core.invoice.InvoiceStatusDetail.PAID
import static fintech.lending.core.invoice.InvoiceStatusDetail.PARTIALLY_PAID
import static fintech.lending.core.invoice.InvoiceStatusDetail.PENDING
import static fintech.lending.core.invoice.InvoiceStatusDetail.VOIDED
import static fintech.lending.core.invoice.db.InvoiceItemType.FEE
import static fintech.lending.core.invoice.db.InvoiceItemType.INTEREST
import static fintech.lending.core.invoice.db.InvoiceItemType.PENALTY
import static fintech.lending.core.invoice.db.InvoiceItemType.PRINCIPAL
import static fintech.transactions.TransactionQuery.byInvoice
import static fintech.transactions.TransactionQuery.byLoan
import static java.time.LocalDate.now
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric

class InvoiceServiceTest extends BaseSpecification {

    @Subject
    @Autowired
    InvoiceService invoiceService

    @Autowired
    InvoiceRepository invoiceRepository

    @Autowired
    TransactionTemplate txTemplate

    @Autowired
    CreditLineLoanHelper loanHelper

    @Autowired
    PaymentService paymentService

    @Autowired
    TransactionService transactionService

    LoanHolder loanHolder

    LoanHolder loanHolder2

    def setup() {
        loanHelper.init()

        loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanHolder2 = loanHelper.applyAndDisburse(new LoanHolder())
    }

    def "find"() {
        given:
        def loanId = loanHolder.loanId
        invoiceWithStatus(loanId, PENDING)
        invoiceWithStatus(loanId, VOIDED)
        invoiceWithStatus(loanId, PAID)
        invoiceWithStatus(loanHolder2.loanId, PENDING)
        invoiceWithStatus(loanId, PARTIALLY_PAID)

        expect:
        invoiceService.find(InvoiceQuery.byLoan(loanId, PENDING, PARTIALLY_PAID)).size() == 2
        invoiceService.find(InvoiceQuery.byLoan(loanId, PENDING)).size() == 1
        invoiceService.find(InvoiceQuery.byLoan(loanId, VOIDED)).size() == 1

        and:
        invoiceService.find(new InvoiceQuery().builder().statusDetailArrayList([PENDING]).build()).size() == 2
        invoiceService.find(new InvoiceQuery().builder().statusDetailArrayList([PENDING, PARTIALLY_PAID]).build()).size() == 3
    }

    def "invoice is created"() {
        def invoiceCommand = createInvoiceCommand([
            new GeneratedInvoice.GeneratedInvoiceItem(type: PRINCIPAL, amount: loanHolder.offeredPrincipal)
        ])
        when:
        def invoiceId = invoiceService.createInvoice(invoiceCommand)

        then:
        with(invoiceService.get(invoiceId)) {
            id
            productId == invoiceCommand.productId
            clientId == invoiceCommand.clientId
            loanId == invoiceCommand.loanId
            number == invoiceCommand.number
            statusDetail == PENDING
            periodFrom == invoiceCommand.periodFrom
            periodTo == invoiceCommand.periodTo
            invoiceDate == invoiceCommand.invoiceDate
            dueDate == invoiceCommand.dueDate
            total == loanHolder.offeredPrincipal
            totalPaid == 0.00
            items.size() == 1
        }

        when:
        invoiceCommand.periodFrom = date("2017-01-31")
        invoiceService.createInvoice(invoiceCommand)

        then:
        thrown(IllegalArgumentException)
    }

    def "cancel single invoice"() {
        given:
        loanHelper.loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, 67.00, loanHolder.issueDate))
        loanHelper.loanService.applyPenalty(new ApplyPenaltyCommand(loanId: loanHolder.loanId, amount: 15.00, valueDate: loanHolder.issueDate, comments: "any comment"))
        loanHelper.loanService.applyFee(new ApplyFeeCommand(loanId: loanHolder.loanId, amount: 15.00, valueDate: loanHolder.issueDate))

        def invoiceId = invoiceService.createInvoice(createInvoiceCommand([
            new GeneratedInvoice.GeneratedInvoiceItem(type: PRINCIPAL, amount: loanHolder.offeredPrincipal),
            new GeneratedInvoice.GeneratedInvoiceItem(type: INTEREST, amount: 23.00),
            new GeneratedInvoice.GeneratedInvoiceItem(type: PENALTY, amount: 10.00),
            new GeneratedInvoice.GeneratedInvoiceItem(type: FEE, amount: 15.00)
        ]))
        partlyRepayInvoice(invoiceId)
        def openInvoice = invoiceService.get(invoiceId)

        when:
        invoiceService.closeInvoice(new CloseInvoiceCommand(invoiceId: invoiceId, date: loanHolder.issueDate.plusDays(20), reason: "broken agreement", statusDetail: CANCELLED))

        then:
        with(invoiceService.get(invoiceId)) {
            status == InvoiceStatus.CLOSED
            statusDetail == CANCELLED
            closeDate == loanHolder.issueDate.plusDays(20)
            closeReason == "broken agreement"
            total == openInvoice.total
            totalPaid == openInvoice.totalPaid
        }
    }

    def "cancel multiple invoices"() {
        given:
        loanHelper.loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, 21.00, loanHolder.issueDate))
        loanHelper.loanService.applyPenalty(new ApplyPenaltyCommand(loanId: loanHolder.loanId, amount: 15.00, valueDate: loanHolder.issueDate, comments: "any comment"))
        loanHelper.loanService.applyFee(new ApplyFeeCommand(loanId: loanHolder.loanId, amount: 15.00, valueDate: loanHolder.issueDate))

        and:
        def invoice1Command = createInvoiceCommand([
            new GeneratedInvoice.GeneratedInvoiceItem(type: PRINCIPAL, amount: 100.05),
            new GeneratedInvoice.GeneratedInvoiceItem(type: INTEREST, amount: 20.99),
            new GeneratedInvoice.GeneratedInvoiceItem(type: PENALTY, amount: 10.00),
            new GeneratedInvoice.GeneratedInvoiceItem(type: FEE, amount: 15.00)
        ])
        invoice1Command.periodTo = loanHolder.issueDate.plusDays(2)
        def invoice1Id = invoiceService.createInvoice(invoice1Command)

        and:
        def invoice2Command = createInvoiceCommand([
            new GeneratedInvoice.GeneratedInvoiceItem(type: INTEREST, amount: 0.01),
            new GeneratedInvoice.GeneratedInvoiceItem(type: PENALTY, amount: 2.50),
        ])
        invoice2Command.periodFrom = invoice1Command.periodTo.plusDays(1)
        invoice2Command.periodTo = invoice1Command.periodTo.plusDays(3)
        def invoice2Id = invoiceService.createInvoice(invoice2Command)

        and:
        partlyRepayInvoice(invoice1Id)
        invoiceService.find(byLoanOpen(loanHolder.loanId)).size() == 2

        when:
        invoiceService.closeInvoice(new CloseInvoiceCommand(invoiceId: invoice1Id, date: loanHolder.issueDate.plusDays(20), statusDetail: CANCELLED, reason: "broken"))

        then: "can cancel only latest invoice"
        thrown(IllegalArgumentException)
        invoiceService.find(byLoanOpen(loanHolder.loanId)).size() == 2

        when:
        invoiceService.closeInvoice(new CloseInvoiceCommand(invoiceId: invoice2Id, date: loanHolder.issueDate.plusDays(20), statusDetail: CANCELLED, reason: "broken promises"))

        then:
        invoiceService.find(byLoanOpen(loanHolder.loanId)).size() == 1
        with(invoiceService.get(invoice2Id)) {
            status == InvoiceStatus.CLOSED
            statusDetail == CANCELLED
            closeDate == loanHolder.issueDate.plusDays(20)
            closeReason == "broken promises"
        }

        when:
        invoiceService.closeInvoice(new CloseInvoiceCommand(invoiceId: invoice1Id, date: loanHolder.issueDate.plusDays(21), statusDetail: CANCELLED, reason: "more broken promises"))

        then:
        invoiceService.find(byLoanOpen(loanHolder.loanId)).size() == 0
        with(invoiceService.get(invoice1Id)) {
            status == InvoiceStatus.CLOSED
            statusDetail == CANCELLED
            closeDate == loanHolder.issueDate.plusDays(21)
            closeReason == "more broken promises"
        }
    }

    def "invoice transaction is created"() {
        given:
        loanHelper.loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, 21.00, loanHolder.issueDate))
        loanHelper.loanService.applyPenalty(new ApplyPenaltyCommand(loanId: loanHolder.loanId, amount: 15.00, valueDate: loanHolder.issueDate, comments: "any comment"))
        loanHelper.loanService.applyFee(new ApplyFeeCommand(loanId: loanHolder.loanId, amount: 15.00, valueDate: loanHolder.issueDate))

        when:
        def invoiceId = invoiceService.createInvoice(createInvoiceCommand([
            new GeneratedInvoice.GeneratedInvoiceItem(type: PRINCIPAL, amount: 100.05),
            new GeneratedInvoice.GeneratedInvoiceItem(type: INTEREST, amount: 20.99),
            new GeneratedInvoice.GeneratedInvoiceItem(type: PENALTY, amount: 10.00),
            new GeneratedInvoice.GeneratedInvoiceItem(type: FEE, amount: 15.00)
        ]))

        then:
        invoiceService.get(invoiceId)
        def invoiceTransactions = transactionService.findTransactions(byInvoice(invoiceId))
        invoiceTransactions.size() == 1
        with(invoiceTransactions[0]) {
            transactionType == TransactionType.INVOICE

            principalInvoiced == 100.05
            principalDisbursed == 00.0
            principalPaid == 00.0
            principalWrittenOff == 00.0

            interestInvoiced == 20.99
            interestApplied == 0.00
            interestPaid == 0.00
            interestWrittenOff == 0.00

            penaltyInvoiced == 10.00
            penaltyApplied == 0.00
            penaltyPaid == 0.00
            penaltyWrittenOff == 0.00

            feeInvoiced == 15.00
            feeApplied == 0.00
            feePaid == 0.00
            feeWrittenOff == 0.00
        }

        and:
        with(transactionService.getBalance(byLoan(loanHolder.loanId))) {
            principalInvoiced == 100.05
            principalDisbursed == 1000.0
            principalPaid == 0.00
            principalWrittenOff == 00.0
            principalOutstanding == 1000.00

            interestInvoiced == 20.99
            interestApplied == 21.00
            interestPaid == 0.00
            interestWrittenOff == 0.00
            interestOutstanding == 21.00

            penaltyInvoiced == 10.00
            penaltyApplied == 15.00
            penaltyPaid == 0.00
            penaltyWrittenOff == 0.00
            penaltyOutstanding == 15.00

            feeInvoiced == 15.00
            feeApplied == 15.00
            feePaid == 0.00
            feeWrittenOff == 0.00
            feeOutstanding == 15.00
        }
    }

    def "3rd and 4th decimal place amount is written off"() {
        given:
        loanHelper.loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, 21.00, loanHolder.issueDate))
        loanHelper.loanService.applyPenalty(new ApplyPenaltyCommand(loanId: loanHolder.loanId, amount: 15.00, valueDate: loanHolder.issueDate, comments: "any comment"))
        loanHelper.loanService.applyFee(new ApplyFeeCommand(loanId: loanHolder.loanId, amount: 15.00, valueDate: loanHolder.issueDate))

        when:
        invoiceService.createInvoice(createInvoiceCommand([
            new GeneratedInvoice.GeneratedInvoiceItem(type: PRINCIPAL, amount: 100.0099),
            new GeneratedInvoice.GeneratedInvoiceItem(type: INTEREST, amount: 20.9911),
            new GeneratedInvoice.GeneratedInvoiceItem(type: PENALTY, amount: 10.999999999),
            new GeneratedInvoice.GeneratedInvoiceItem(type: FEE, amount: 14.9911)
        ]))

        then:
        with(transactionService.getBalance(byLoan(loanHolder.loanId))) {
            principalInvoiced == 100.00
            principalDisbursed == 1000.0
            principalPaid == 0.00
            principalWrittenOff == 0.00 // the principal is not written off
            principalOutstanding == 1000.00

            interestInvoiced == 20.99
            interestApplied == 21.00
            interestPaid == 0.00
            interestWrittenOff == 0.0011
            interestOutstanding == 21.00 - interestWrittenOff

            penaltyInvoiced == 10.99
            penaltyApplied == 15.00
            penaltyPaid == 0.00
            penaltyWrittenOff == 0.0099
            penaltyOutstanding == 15.00 - penaltyWrittenOff

            feeInvoiced == 14.99
            feeApplied == 15.00
            feePaid == 0.00
            feeWrittenOff == 0.0011
            feeOutstanding == 15.00 - feeWrittenOff
        }
    }

    def "invoice file generated"() {
        when:
        def invoiceCommand = createInvoiceCommand([
            new GeneratedInvoice.GeneratedInvoiceItem(type: PRINCIPAL, amount: loanHolder.offeredPrincipal)
        ])
        def invoiceId = invoiceService.createInvoice(invoiceCommand)
        invoiceService.invoiceFileGenerated(invoiceId, 1L, "invoice.pdf")

        then:
        with(invoiceService.get(invoiceId)) {
            fileId == 1l
            fileName == "invoice.pdf"
        }

    }

    def "invoices for membership level"() {
        given:
        def invoiceCommand = createInvoiceCommand([
            new GeneratedInvoice.GeneratedInvoiceItem(type: PRINCIPAL, amount: loanHolder.offeredPrincipal)
        ])
        invoiceCommand.membershipLevelChecked = false
        def invoiceId = invoiceService.createInvoice(invoiceCommand)

        when:
        def invoices = invoiceService.findForMembershipLevel(invoiceCommand.clientId)

        then:
        invoices.size() == 1
        !invoices[0].membershipLevelChanged

        when:
        invoiceService.markMembershipLevelChanged(invoiceId)

        and:
        invoices = invoiceService.findForMembershipLevel(invoiceCommand.clientId)

        then:
        invoices.isEmpty()
    }

    GeneratedInvoice createInvoiceCommand(items) {
        new GeneratedInvoice(invoiceDate: date("2017-02-01"), periodFrom: loanHolder.issueDate, periodTo: loanHolder.issueDate.plusDays(30),
            number: randomAlphanumeric(8), dueDate: loanHolder.issueDate.plusDays(40), loanId: loanHolder.loanId, productId: PRODUCT_ID, clientId: randomId(),
            items: items)
    }

    InvoiceEntity invoiceWithStatus(long loanId, status) {
        println loanHelper.loanService.getLoan(loanId)
        txTemplate.execute {
            invoiceRepository.saveAndFlush(new InvoiceEntity(productId: PRODUCT_ID, clientId: randomId(), loanId: loanId,
                number: randomId(), statusDetail: status, periodFrom: now().minusDays(30), periodTo: now(),
                invoiceDate: now(), dueDate: now(), total: 100.00, totalPaid: 0.00))
        }
    }

    def partlyRepayInvoice(invoiceId) {
        txTemplate.execute {
            def paymentId = paymentService.addPayment(new AddPaymentCommand(accountId: loanHelper.institutionAccountId,
                paymentType: PaymentType.INCOMING, valueDate: now(), postedAt: LocalDateTime.now(),
                amount: amount(100.00), details: "", reference: randomAlphabetic(8), key: randomAlphabetic(8)))

            transactionService.addTransaction(new AddTransactionCommand(valueDate: now(), bookingDate: now(), postDate: now(),
                transactionType: TransactionType.REPAYMENT, invoiceId: invoiceId, paymentId: paymentId, loanId: loanHolder.loanId,
                principalPaid: amount(30.00), interestPaid: amount(10.00), cashIn: amount(40.00),
                entries: []
            ))

            transactionService.addTransaction(new AddTransactionCommand(valueDate: now(), bookingDate: now(), postDate: now(),
                transactionType: TransactionType.REPAYMENT, invoiceId: invoiceId, paymentId: paymentId, loanId: loanHolder.loanId,
                principalPaid: amount(20.00), interestPaid: amount(5.00), penaltyPaid: 2.00, cashIn: amount(27.00)))
        }
    }

}
