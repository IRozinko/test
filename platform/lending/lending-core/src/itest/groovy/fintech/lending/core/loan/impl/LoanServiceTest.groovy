package fintech.lending.core.loan.impl

import fintech.TimeMachine
import fintech.lending.BaseSpecification
import fintech.lending.core.CreditLineLoanHelper
import fintech.lending.core.LoanHolder
import fintech.lending.core.loan.LoanService
import fintech.lending.core.loan.LoanStatus
import fintech.lending.core.loan.LoanStatusDetail
import fintech.lending.core.loan.ScheduleService
import fintech.lending.core.loan.commands.ClosePaidLoanCommand
import fintech.lending.core.loan.commands.IssueLoanCommand
import fintech.transactions.TransactionService
import fintech.transactions.VoidTransactionCommand
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import java.time.temporal.ChronoUnit

class LoanServiceTest extends BaseSpecification {

    @Subject
    @Autowired
    LoanService loanService

    @Autowired
    CreditLineLoanHelper creditLineLoanHelper

    @Autowired
    ScheduleService scheduleService

    @Autowired
    TransactionService transactionService

    def "issue credit line loan"() {
        given:
        creditLineLoanHelper.init()
        def holder = new LoanHolder()
        creditLineLoanHelper.submitApplication(holder)
        creditLineLoanHelper.updateOffer(holder)
        def command = new IssueLoanCommand(loanApplicationId: holder.applicationId, loanNumber: holder.number, issueDate: holder.issueDate)

        when:
        def loanId = loanService.issueLoan(command)
        def loan = loanService.getLoan(loanId)

        then:
        with(loan) {
            id == loanId
            productId == CreditLineLoanHelper.PRODUCT_ID
            clientId == holder.clientId
            applicationId == holder.applicationId
            status == LoanStatus.OPEN
            statusDetail == LoanStatusDetail.ISSUED
            number == holder.number
            loansPaid == 0
            issueDate == holder.issueDate
            !closeDate
            maturityDate == holder.issueDate.plusMonths(holder.requestedMonths)
            paymentDueDate == holder.issueDate.plusMonths(holder.requestedMonths)
            !brokenDate
            !rescheduledDate
            !rescheduleBrokenDate
            !movedToLegalDate
            !firstDisbursementDate
            periodCount == 1
            invoicePaymentDay == holder.invoicePaymentDate
            overdueDays == ChronoUnit.DAYS.between(paymentDueDate, issueDate)
            maxOverdueDays == overdueDays
            extensions == 0
            extendedByDays == 0
            !penaltySuspended
            interestDiscountAmount == 0
            interestDiscountPercent == 0
            creditLimit == 1000.0
            creditLimitAvailable == 1000.0
            creditLimitAwarded == 2000.0
            principalDisbursed == 0
            principalPaid == 0
            principalWrittenOff == 0
            principalDue == 0
            principalOutstanding == 0
            interestApplied == 0
            interestPaid == 0
            interestWrittenOff == 0
            interestDue == 0
            interestOutstanding == 0
            penaltyApplied == 0
            penaltyPaid == 0
            penaltyWrittenOff == 0
            penaltyDue == 0
            penaltyOutstanding == 0
            feeApplied == 0
            feePaid == 0
            feeWrittenOff == 0
            feeDue == 0
            feeOutstanding == 0
            totalPaid == 0
            totalDue == 0
            totalOutstanding == 0
            overpaymentReceived == 0
            overpaymentUsed == 0
            overpaymentRefunded == 0
            overpaymentAvailable == 0
            cashIn == 0
            cashOut == 0
        }
    }

    def "disbursed credit line loan"() {
        given:
        creditLineLoanHelper.init()
        def holder = new LoanHolder()
        creditLineLoanHelper.submitApplication(holder)
        creditLineLoanHelper.updateOffer(holder)
        creditLineLoanHelper.issueLoan(holder)

        when:
        creditLineLoanHelper.disburse(holder)
        def loan = loanService.getLoan(holder.loanId)

        then:
        with(loan) {
            id == holder.loanId
            productId == CreditLineLoanHelper.PRODUCT_ID
            clientId == holder.clientId
            applicationId == holder.applicationId
            status == LoanStatus.OPEN
            statusDetail == LoanStatusDetail.ACTIVE
            number == holder.number
            loansPaid == 0
            issueDate == holder.issueDate
            !closeDate
            maturityDate == holder.issueDate.plusMonths(holder.requestedMonths)
            paymentDueDate == holder.issueDate.plusMonths(holder.requestedMonths)
            !brokenDate
            !rescheduledDate
            !rescheduleBrokenDate
            !movedToLegalDate
            firstDisbursementDate == holder.issueDate
            periodCount == 1
            invoicePaymentDay == holder.invoicePaymentDate
            overdueDays == ChronoUnit.DAYS.between(maturityDate, TimeMachine.today())
            maxOverdueDays == overdueDays
            extensions == 0
            extendedByDays == 0
            !penaltySuspended
            interestDiscountAmount == 0
            interestDiscountPercent == 0
            creditLimit == 1000.0
            creditLimitAvailable == 0.0
            creditLimitAwarded == 2000.0
            principalDisbursed == 1000.0
            principalPaid == 0
            principalWrittenOff == 0
            principalDue == 0
            principalOutstanding == 1000.0
            interestApplied == 0
            interestPaid == 0
            interestWrittenOff == 0
            interestDue == 0
            interestOutstanding == 0
            penaltyApplied == 0
            penaltyPaid == 0
            penaltyWrittenOff == 0
            penaltyDue == 0
            penaltyOutstanding == 0
            feeApplied == 0
            feePaid == 0
            feeWrittenOff == 0
            feeDue == 0
            feeOutstanding == 0
            totalPaid == 0
            totalDue == 0
            totalOutstanding == 1000.0
            overpaymentReceived == 0
            overpaymentUsed == 0
            overpaymentRefunded == 0
            overpaymentAvailable == 0
            cashIn == 0
            cashOut == 0
        }
    }

    def "void disbursement credit line loan"() {
        given:
        creditLineLoanHelper.init()
        def holder = new LoanHolder()
        creditLineLoanHelper.submitApplication(holder)
        creditLineLoanHelper.updateOffer(holder)
        creditLineLoanHelper.issueLoan(holder)
        def transactionId = creditLineLoanHelper.disburse(holder)

        when:
        transactionService.voidTransaction(new VoidTransactionCommand(transactionId, holder.issueDate, holder.issueDate))
        def loan = loanService.getLoan(holder.loanId)

        then:
        with(loan) {
            id == holder.loanId
            productId == CreditLineLoanHelper.PRODUCT_ID
            clientId == holder.clientId
            applicationId == holder.applicationId
            status == LoanStatus.OPEN
            statusDetail == LoanStatusDetail.ISSUED
            number == holder.number
            loansPaid == 0
            issueDate == holder.issueDate
            !closeDate
            maturityDate == holder.issueDate.plusMonths(holder.requestedMonths)
            paymentDueDate == holder.issueDate.plusMonths(holder.requestedMonths)
            !brokenDate
            !rescheduledDate
            !rescheduleBrokenDate
            !movedToLegalDate
            !firstDisbursementDate
            periodCount == 1
            invoicePaymentDay == holder.invoicePaymentDate
            overdueDays == ChronoUnit.DAYS.between(maturityDate, holder.issueDate)
            maxOverdueDays == overdueDays
            extensions == 0
            extendedByDays == 0
            !penaltySuspended
            interestDiscountAmount == 0
            interestDiscountPercent == 0
            creditLimit == 1000.0
            creditLimitAvailable == 1000.0
            creditLimitAwarded == 2000.0
            principalDisbursed == 0.0
            principalPaid == 0
            principalWrittenOff == 0
            principalDue == 0
            principalOutstanding == 0.0
            interestApplied == 0
            interestPaid == 0
            interestWrittenOff == 0
            interestDue == 0
            interestOutstanding == 0
            penaltyApplied == 0
            penaltyPaid == 0
            penaltyWrittenOff == 0
            penaltyDue == 0
            penaltyOutstanding == 0
            feeApplied == 0
            feePaid == 0
            feeWrittenOff == 0
            feeDue == 0
            feeOutstanding == 0
            totalPaid == 0
            totalDue == 0
            totalOutstanding == 0.0
            overpaymentReceived == 0
            overpaymentUsed == 0
            overpaymentRefunded == 0
            overpaymentAvailable == 0
            cashIn == 0
            cashOut == 0
        }
    }

    def "extend maturity date, 1 month loan"() {
        given:
        creditLineLoanHelper.init()
        def holder = new LoanHolder()
        creditLineLoanHelper.submitApplication(holder)
        creditLineLoanHelper.updateOffer(holder)
        creditLineLoanHelper.issueLoan(holder)

        when:
        loanService.extendMaturityDate(holder.loanId, TimeMachine.today())
        def loan = loanService.getLoan(holder.loanId)
        def contract = scheduleService.getCurrentContract(holder.loanId)
        def loanMaturityDate = holder.issueDate.plusMonths(holder.requestedMonths)

        then:
        with(loan) {
            id == holder.loanId
            maturityDate == loanMaturityDate.plusMonths(holder.requestedMonths)
        }
        with(contract) {
            loanId == holder.loanId
            maturityDate == loan.maturityDate
        }
    }

    def "extend maturity date, 1 year loan"() {
        given:
        creditLineLoanHelper.init()
        def holder = new LoanHolder()
        holder.requestedMonths = 12
        creditLineLoanHelper.submitApplication(holder)
        creditLineLoanHelper.updateOffer(holder)
        creditLineLoanHelper.issueLoan(holder)

        when:
        loanService.extendMaturityDate(holder.loanId, TimeMachine.today())
        def loan = loanService.getLoan(holder.loanId)
        def contract = scheduleService.getCurrentContract(holder.loanId)
        def loanMaturityDate = holder.issueDate.plusMonths(holder.requestedMonths)

        then:
        with(loan) {
            id == holder.loanId
            maturityDate == loanMaturityDate.plusMonths(holder.requestedMonths)
        }
        with(contract) {
            loanId == holder.loanId
            maturityDate == loan.maturityDate
        }
    }

    def "don't extend maturity date for loans not open"() {
        given:
        creditLineLoanHelper.init()
        def holder = new LoanHolder()
        creditLineLoanHelper.submitApplication(holder)
        creditLineLoanHelper.updateOffer(holder)
        creditLineLoanHelper.issueLoan(holder)
        creditLineLoanHelper.updateLoanStatus(holder, LoanStatus.CLOSED, LoanStatusDetail.PAID, TimeMachine.today())

        when:
        loanService.extendMaturityDate(holder.loanId, TimeMachine.today())

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "It's possible to extend maturity date only for OPEN loans"
    }

    def "don't extend maturity date for broken loans"() {
        given:
        creditLineLoanHelper.init()
        def holder = new LoanHolder()
        creditLineLoanHelper.submitApplication(holder)
        creditLineLoanHelper.updateOffer(holder)
        creditLineLoanHelper.issueLoan(holder)
        creditLineLoanHelper.updateLoanStatus(holder, LoanStatus.OPEN, LoanStatusDetail.BROKEN, TimeMachine.today())

        when:
        loanService.extendMaturityDate(holder.loanId, TimeMachine.today())

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "It's possible to extend maturity date only for ACTIVE or PAID or DISBURSING or ISSUED loans"
    }

    def "don't extend maturity date for loans in legal"() {
        given:
        creditLineLoanHelper.init()
        def holder = new LoanHolder()
        creditLineLoanHelper.submitApplication(holder)
        creditLineLoanHelper.updateOffer(holder)
        creditLineLoanHelper.issueLoan(holder)
        creditLineLoanHelper.updateLoanStatus(holder, LoanStatus.OPEN, LoanStatusDetail.LEGAL, TimeMachine.today())

        when:
        loanService.extendMaturityDate(holder.loanId, TimeMachine.today())

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "It's possible to extend maturity date only for ACTIVE or PAID or DISBURSING or ISSUED loans"
    }

    def "don't extend maturity date in advance"() {
        given:
        creditLineLoanHelper.init()
        def holder = new LoanHolder()
        creditLineLoanHelper.submitApplication(holder)
        creditLineLoanHelper.updateOffer(holder)
        creditLineLoanHelper.issueLoan(holder)
        def loan = loanService.getLoan(holder.loanId)

        when:
        TimeMachine.useFixedClockAt(loan.maturityDate.minusDays(3))
        loanService.extendMaturityDate(holder.loanId, TimeMachine.today())

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "It's possible to extend the maturity date max 2 days in advance"
    }

    def "close paid credit-line loan"() {
        given:
        creditLineLoanHelper.init()
        def holder = new LoanHolder()
        creditLineLoanHelper.submitApplication(holder)
        creditLineLoanHelper.updateOffer(holder)
        creditLineLoanHelper.issueLoan(holder)
        creditLineLoanHelper.disburse(holder)

        when:
        loanService.closePaidLoan(new ClosePaidLoanCommand(holder.loanId))

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Loan must be paid"

        when:
        creditLineLoanHelper.repayLoan(holder, TimeMachine.today(), creditLineLoanHelper.loanBalance(holder).getTotalOutstanding())
        loanService.closePaidLoan(new ClosePaidLoanCommand(holder.loanId))

        then:
        with(scheduleService.getCurrentContract(holder.loanId)) {
            closeLoanOnPaid == Boolean.TRUE
        }

        with(loanService.getLoan(holder.loanId)) {
            status == LoanStatus.CLOSED
            statusDetail == LoanStatusDetail.PAID
        }
    }
}
