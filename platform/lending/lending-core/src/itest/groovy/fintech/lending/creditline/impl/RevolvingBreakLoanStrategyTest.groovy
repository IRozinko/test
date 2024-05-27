package fintech.lending.creditline.impl

import fintech.lending.BaseSpecification
import fintech.lending.core.CreditLineLoanHelper
import fintech.lending.core.LoanHolder
import fintech.lending.core.invoice.InvoiceService
import fintech.lending.core.invoice.InvoiceStatus
import fintech.lending.core.invoice.InvoiceStatusDetail
import fintech.lending.core.invoice.commands.GeneratedInvoice
import fintech.lending.core.invoice.db.InvoiceItemType
import fintech.lending.core.loan.InstallmentStatus
import fintech.lending.core.loan.InstallmentStatusDetail
import fintech.lending.core.loan.LoanService
import fintech.lending.core.loan.LoanStatus
import fintech.lending.core.loan.LoanStatusDetail
import fintech.lending.core.loan.ScheduleService
import fintech.lending.core.loan.commands.BreakLoanCommand
import fintech.lending.core.loan.commands.UnBreakLoanCommand
import org.apache.commons.lang3.NotImplementedException
import org.springframework.beans.factory.annotation.Autowired

import static fintech.TimeMachine.today

class RevolvingBreakLoanStrategyTest extends BaseSpecification {

    @Autowired
    CreditLineLoanHelper loanHelper

    @Autowired
    ScheduleService scheduleService

    @Autowired
    LoanService loanService

    @Autowired
    InvoiceService invoiceService

    LoanHolder loanHolder

    def setup() {
        loanHelper.init()
        loanHolder = new LoanHolder()
    }

    def "break loan"() {
        given:
        loanHelper.applyAndDisburse(loanHolder)

        when:
        def breakLoanCommand = new BreakLoanCommand(loanId: loanHolder.loanId, when: today())
        loanService.breakLoan(breakLoanCommand)
        def installment = scheduleService.getFirstActiveInstallment(loanHolder.loanId)
        def loan = loanService.getLoan(loanHolder.loanId)

        then:
        with(loan) {
            status == LoanStatus.OPEN
            statusDetail == LoanStatusDetail.BROKEN
            totalDue == totalOutstanding
            maturityDate == breakLoanCommand.when
        }
        with(installment) {
            status == InstallmentStatus.OPEN
            statusDetail == InstallmentStatusDetail.PENDING
            principalInvoiced == loan.principalOutstanding
        }
    }

    def "break loan with invoices"() {
        given:
        loanHelper.applyAndDisburse(loanHolder)
        loanHelper.applyInterest(loanHolder, today(), 10.00)
        def invoiceId = loanHelper.createFirstInvoice(loanHolder,
            [new GeneratedInvoice.GeneratedInvoiceItem(type: InvoiceItemType.PRINCIPAL, amount: 50.00),
             new GeneratedInvoice.GeneratedInvoiceItem(type: InvoiceItemType.INTEREST, amount: 5.00)]
        )
        loanHelper.repayLoan(loanHolder, today(), 45.00)

        when:
        def breakLoanCommand = new BreakLoanCommand(loanId: loanHolder.loanId, when: today())
        loanService.breakLoan(breakLoanCommand)
        def invoice = invoiceService.get(invoiceId)
        def installment = scheduleService.getFirstActiveInstallment(loanHolder.loanId)
        def loan = loanService.getLoan(loanHolder.loanId)

        then:
        with(loan) {
            status == LoanStatus.OPEN
            statusDetail == LoanStatusDetail.BROKEN
            totalDue == totalOutstanding
            maturityDate == breakLoanCommand.when
        }
        with(invoice) {
            status == InvoiceStatus.CLOSED
            statusDetail == InvoiceStatusDetail.BROKEN
        }
        with(installment) {
            status == InstallmentStatus.OPEN
            statusDetail == InstallmentStatusDetail.PENDING
            principalInvoiced == loan.principalOutstanding
            interestInvoiced == loan.interestOutstanding
        }
    }

    def "un-break loan"() {
        given:
        loanHelper.applyAndDisburse(loanHolder)
        def breakLoanCommand = new BreakLoanCommand(loanId: loanHolder.loanId, when: today())
        loanService.breakLoan(breakLoanCommand)

        when:
        loanService.unBreakLoan(new UnBreakLoanCommand(loanHolder.loanId, today()))

        then:
        thrown(NotImplementedException.class)
    }

}
