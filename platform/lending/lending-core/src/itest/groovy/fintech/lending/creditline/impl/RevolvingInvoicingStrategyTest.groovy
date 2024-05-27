package fintech.lending.creditline.impl

import fintech.lending.BaseSpecification
import fintech.lending.core.CreditLineLoanHelper
import fintech.lending.core.LoanHolder
import fintech.lending.core.invoice.InvoiceService
import fintech.lending.core.invoice.commands.GenerateInvoiceCommand
import fintech.lending.core.invoice.db.InvoiceItemType
import fintech.lending.core.loan.LoanService
import fintech.lending.core.loan.commands.ApplyFeeCommand
import fintech.lending.core.loan.commands.ApplyInterestCommand
import fintech.lending.core.loan.commands.ApplyPenaltyCommand
import fintech.lending.core.loan.commands.WriteOffAmountCommand
import fintech.lending.creditline.TransactionConstants
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

class RevolvingInvoicingStrategyTest extends BaseSpecification {

    @Subject
    @Autowired
    CreditLineInvoicingStrategy creditLineInvoicingStrategy

    @Autowired
    CreditLineLoanHelper loanHelper

    @Autowired
    LoanService loanService

    @Autowired
    InvoiceService invoiceService

    def setup() {
        loanHelper.init()
    }

    def "Invoice items"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, 21.00, loanHolder.issueDate))
        loanService.applyPenalty(new ApplyPenaltyCommand(loanId: loanHolder.loanId, amount: 15.00, valueDate: loanHolder.issueDate, comments: "any comment"))
        loanService.applyFee(new ApplyFeeCommand(loanId: loanHolder.loanId, amount: 25.00, valueDate: loanHolder.issueDate, subType: TransactionConstants.TRANSACTION_SUB_TYPE_FIRST_DISBURSEMENT_FEE))
        loanService.applyFee(new ApplyFeeCommand(loanId: loanHolder.loanId, amount: 20.00, valueDate: loanHolder.issueDate, subType: TransactionConstants.TRANSACTION_SUB_TYPE_REPEATED_DISBURSEMENT_FEE))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 5
        with(items[0]) {
            type == InvoiceItemType.INTEREST
            !subType
            amount == 21.00
            !correction
        }
        with(items[1]) {
            type == InvoiceItemType.PENALTY
            !subType
            amount == 15.00
            !correction
        }
        with(items[2]) {
            type == InvoiceItemType.FEE
            subType == TransactionConstants.TRANSACTION_SUB_TYPE_FIRST_DISBURSEMENT_FEE
            amount == 25.00
            !correction
        }
        with(items[3]) {
            type == InvoiceItemType.FEE
            subType == TransactionConstants.TRANSACTION_SUB_TYPE_REPEATED_DISBURSEMENT_FEE
            amount == 20.00
            !correction
        }
        with(items[4]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 150.00 // principalOutstanding * product.invoiceSettings.principalAmountPercentage %
            !correction
        }
    }

    def "Invoice items, interest out of period"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, 21.00, loanHolder.issueDate.plusDays(4)))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 1
        with(items[0]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 150.00
            !correction
        }
    }

    def "Invoice items, interest NOT repaid"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanHelper.repayLoan(loanHolder, loanHolder.issueDate.plusDays(1), 1020.00)
        loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, 21.00, loanHolder.issueDate))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 1
        with(items[0]) {
            type == InvoiceItemType.INTEREST
            !subType
            amount == 21.00
            !correction
        }
    }

    def "Invoice items, interest written off"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, 21.00, loanHolder.issueDate))
        loanService.writeOffAmount(new WriteOffAmountCommand(loanId: loanHolder.loanId, when: loanHolder.issueDate.plusDays(1), interest: 15.00, comments: "any comment"))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 2
        with(items[0]) {
            type == InvoiceItemType.INTEREST
            !subType
            amount == 6.00
            !correction
        }
        with(items[1]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 150.00
            !correction
        }
    }

    def "Invoice items, interest already invoiced"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, 21.00, loanHolder.issueDate))
        creditLineInvoicingStrategy.generateInvoice(new GenerateInvoiceCommand(loanId: loanHolder.loanId, dateTo: loanHolder.issueDate.plusDays(1), invoiceDate: loanHolder.issueDate.plusDays(1), dueDate: loanHolder.issueDate.plusDays(10)))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 1
        with(items[0]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 150.00
            !correction
        }
    }

    def "Invoice items, penalty out of period"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanService.applyPenalty(new ApplyPenaltyCommand(loanId: loanHolder.loanId, amount: 15.00, valueDate: loanHolder.issueDate.plusDays(4), comments: "any comment"))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 1
        with(items[0]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 150.00
            !correction
        }
    }

    def "Invoice items, penalty NOT repaid"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanHelper.repayLoan(loanHolder, loanHolder.issueDate.plusDays(1), 1020.00)
        loanService.applyPenalty(new ApplyPenaltyCommand(loanId: loanHolder.loanId, amount: 15.00, valueDate: loanHolder.issueDate, comments: "any comment"))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 1
        with(items[0]) {
            type == InvoiceItemType.PENALTY
            !subType
            amount == 15.00
            !correction
        }
    }

    def "Invoice items, penalty written off"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanService.applyPenalty(new ApplyPenaltyCommand(loanId: loanHolder.loanId, amount: 15.00, valueDate: loanHolder.issueDate, comments: "any comment"))
        loanService.writeOffAmount(new WriteOffAmountCommand(loanId: loanHolder.loanId, when: loanHolder.issueDate.plusDays(1), penalty: 13.00, comments: "any comment"))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 2
        with(items[0]) {
            type == InvoiceItemType.PENALTY
            !subType
            amount == 2.00
            !correction
        }
        with(items[1]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 150.00
            !correction
        }
    }

    def "Invoice items, penalty already invoiced"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanService.applyPenalty(new ApplyPenaltyCommand(loanId: loanHolder.loanId, amount: 15.00, valueDate: loanHolder.issueDate, comments: "any comment"))
        creditLineInvoicingStrategy.generateInvoice(new GenerateInvoiceCommand(loanId: loanHolder.loanId, dateTo: loanHolder.issueDate.plusDays(1), invoiceDate: loanHolder.issueDate.plusDays(1), dueDate: loanHolder.issueDate.plusDays(10)))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 1
        with(items[0]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 150.00
            !correction
        }
    }

    def "Invoice items, fee not recognized"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanService.applyFee(new ApplyFeeCommand(loanId: loanHolder.loanId, amount: 20.00, valueDate: loanHolder.issueDate))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 1
        with(items[0]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 150.00
            !correction
        }
    }

    def "Invoice items, fee out of period"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanService.applyFee(new ApplyFeeCommand(loanId: loanHolder.loanId, amount: 20.00, valueDate: loanHolder.issueDate.plusDays(4), subType: TransactionConstants.TRANSACTION_SUB_TYPE_FIRST_DISBURSEMENT_FEE))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 1
        with(items[0]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 150.00
            !correction
        }
    }

    def "Invoice items, fee repaid"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanService.applyFee(new ApplyFeeCommand(loanId: loanHolder.loanId, amount: 25.00, valueDate: loanHolder.issueDate, subType: TransactionConstants.TRANSACTION_SUB_TYPE_FIRST_DISBURSEMENT_FEE))
        loanHelper.repayLoan(loanHolder, loanHolder.issueDate.plusDays(1), 20.00)
        loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, 21.00, loanHolder.issueDate))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 3
        with(items[0]) {
            type == InvoiceItemType.INTEREST
            !subType
            amount == 21.00
            !correction
        }
        with(items[1]) {
            type == InvoiceItemType.FEE
            subType == TransactionConstants.TRANSACTION_SUB_TYPE_FIRST_DISBURSEMENT_FEE
            amount == 5.00
            !correction
        }
        with(items[2]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 150.00
            !correction
        }
    }

    def "Invoice items, fee written off"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanService.applyFee(new ApplyFeeCommand(loanId: loanHolder.loanId, amount: 20.00, valueDate: loanHolder.issueDate, subType: TransactionConstants.TRANSACTION_SUB_TYPE_FIRST_DISBURSEMENT_FEE))
        loanService.writeOffAmount(new WriteOffAmountCommand(loanId: loanHolder.loanId, when: loanHolder.issueDate.plusDays(1), fee: 15.00, comments: "any comment"))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 2
        with(items[0]) {
            type == InvoiceItemType.FEE
            subType == TransactionConstants.TRANSACTION_SUB_TYPE_FIRST_DISBURSEMENT_FEE
            amount == 5.00
            !correction
        }
        with(items[1]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 150.00
            !correction
        }
    }

    def "Invoice items, fee already invoiced"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanService.applyFee(new ApplyFeeCommand(loanId: loanHolder.loanId, amount: 20.00, valueDate: loanHolder.issueDate, subType: TransactionConstants.TRANSACTION_SUB_TYPE_FIRST_DISBURSEMENT_FEE))
        creditLineInvoicingStrategy.generateInvoice(new GenerateInvoiceCommand(loanId: loanHolder.loanId, dateTo: loanHolder.issueDate.plusDays(1), invoiceDate: loanHolder.issueDate.plusDays(1), dueDate: loanHolder.issueDate.plusDays(10)))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 1
        with(items[0]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 150.00
            !correction
        }
    }

    def "Invoice items, principal repaid"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanHelper.repayLoan(loanHolder, loanHolder.issueDate.plusDays(1), 500.00)

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 1
        with(items[0]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 75.00
            !correction
        }
    }

    def "Invoice items, principal repaid minimal amount"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanHelper.repayLoan(loanHolder, loanHolder.issueDate.plusDays(1), 990.00)

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 1
        with(items[0]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 10.00
            !correction
        }
    }

    def "Invoice items, principal written off"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanService.writeOffAmount(new WriteOffAmountCommand(loanId: loanHolder.loanId, when: loanHolder.issueDate.plusDays(1), principal: 15.00, comments: "any comment"))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 1
        with(items[0]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 147.75
            !correction
        }
    }

    def "Invoice items, principal written off minimal amount"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanService.writeOffAmount(new WriteOffAmountCommand(loanId: loanHolder.loanId, when: loanHolder.issueDate.plusDays(1), principal: 990.00, comments: "any comment"))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 1
        with(items[0]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 10.00
            !correction
        }
    }

    def "Invoice items, principal already invoiced"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        creditLineInvoicingStrategy.generateInvoice(new GenerateInvoiceCommand(loanId: loanHolder.loanId, dateTo: loanHolder.issueDate.plusDays(1), invoiceDate: loanHolder.issueDate.plusDays(1), dueDate: loanHolder.issueDate.plusDays(10)))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 1
        with(items[0]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 150.00
            !correction
        }
    }

    def "Invoice items, principal already invoiced minimal amount"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        creditLineInvoicingStrategy.generateInvoice(new GenerateInvoiceCommand(loanId: loanHolder.loanId, dateTo: loanHolder.issueDate.plusDays(1), invoiceDate: loanHolder.issueDate.plusDays(1), dueDate: loanHolder.issueDate.plusDays(10)))
        loanHelper.repayLoan(loanHolder, loanHolder.issueDate.plusDays(1), 990.00)

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate.plusDays(1), loanHolder.issueDate.plusDays(2))

        then:
        items
        items.size() == 1
        with(items[0]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 10.00
            !correction
        }
    }

    def "Invoice items, principal repaid and interest minimal amount"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        loanHelper.repayLoan(loanHolder, loanHolder.issueDate.plusDays(1), 990.00)
        loanService.applyInterest(new ApplyInterestCommand(loanHolder.loanId, 21.00, loanHolder.issueDate))

        when:
        def items = creditLineInvoicingStrategy.calculate(loanHolder.loanId, loanHolder.issueDate, loanHolder.issueDate.plusDays(1))

        then:
        items
        items.size() == 2
        with(items[0]) {
            type == InvoiceItemType.INTEREST
            !subType
            amount == 21.00
            !correction
        }
        with(items[1]) {
            type == InvoiceItemType.PRINCIPAL
            !subType
            amount == 10.00
            !correction
        }
    }

    def "Generate first invoice"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        def loan = loanService.getLoan(loanHolder.loanId)

        when:
        def invoiceId = creditLineInvoicingStrategy.generateInvoice(new GenerateInvoiceCommand(loanId: loanHolder.loanId, dateTo: loanHolder.issueDate.plusDays(1), invoiceDate: loanHolder.issueDate.plusDays(1), dueDate: loanHolder.issueDate.plusDays(2)))
        def invoice = invoiceService.get(invoiceId)

        then:
        with(invoice){
            invoiceDate == loanHolder.issueDate.plusDays(1)
            periodFrom == loan.getFirstDisbursementDate()
            periodTo == loanHolder.issueDate.plusDays(1)
            number
            dueDate == loanHolder.issueDate.plusDays(2)
            loanId == loanHolder.loanId
            productId == loan.getProductId()
            clientId == loanHolder.clientId
            !generateFile
            !sendFile
        }
    }

    def "Generate second invoice"() {
        given:
        def loanHolder = loanHelper.applyAndDisburse(new LoanHolder())
        def loan = loanService.getLoan(loanHolder.loanId)
        creditLineInvoicingStrategy.generateInvoice(new GenerateInvoiceCommand(loanId: loanHolder.loanId, dateTo: loanHolder.issueDate.plusDays(1), invoiceDate: loanHolder.issueDate.plusDays(1), dueDate: loanHolder.issueDate.plusDays(2)))

        when:
        def invoiceId = creditLineInvoicingStrategy.generateInvoice(new GenerateInvoiceCommand(loanId: loanHolder.loanId, dateTo: loanHolder.issueDate.plusDays(4), invoiceDate: loanHolder.issueDate.plusDays(2), dueDate: loanHolder.issueDate.plusDays(5)))
        def invoice = invoiceService.get(invoiceId)

        then:
        with(invoice){
            invoiceDate == loanHolder.issueDate.plusDays(2)
            periodFrom == loanHolder.issueDate.plusDays(2)
            periodTo == loanHolder.issueDate.plusDays(4)
            number
            dueDate == loanHolder.issueDate.plusDays(5)
            loanId == loanHolder.loanId
            productId == loan.getProductId()
            clientId == loanHolder.clientId
            !generateFile
            !sendFile
        }
    }
}
