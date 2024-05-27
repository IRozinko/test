package fintech.lending.core.snapshot

import fintech.TimeMachine
import fintech.lending.BaseSpecification
import fintech.lending.core.CreditLineLoanHelper
import fintech.lending.core.LoanHolder
import fintech.lending.core.loan.LoanService
import fintech.lending.core.loan.LoanStatus
import fintech.lending.core.loan.LoanStatusDetail
import fintech.lending.core.snapshot.db.LoanDailySnapshotRepository
import fintech.transactions.TransactionService
import org.springframework.beans.factory.annotation.Autowired

import static fintech.DateUtils.date

class LoanSnapshotServiceTest extends BaseSpecification {

    @Autowired
    CreditLineLoanHelper helper

    @Autowired
    TransactionService transactionService

    @Autowired
    LoanService loanService

    @Autowired
    LoanDailySnapshotRepository loanDailySnapshotRepository

    @Autowired
    LoanDailySnapshotService loanDailySnapshotService

    def setup() {
        helper.init()
    }

    def "Make snapshots"() {
        given:
        def holder = new LoanHolder()
        helper.applyAndDisburse(holder)

        when:
        loanDailySnapshotService.makeSnapshotOfAllLoans(date("2001-01-01"), false)

        then:
        loanDailySnapshotRepository.findAll().size() == 1
        with(loanDailySnapshotRepository.findAll()[0]) {
            principalDisbursed == holder.offeredPrincipal
            principalPaid == 0.00g
            issueDate == holder.issueDate
            loanId == holder.loanId
            status == LoanStatus.OPEN
            effectiveFrom == date("2001-01-01")
            effectiveTo == date("2100-01-01")
        }

        when:
        helper.repayLoan(holder, holder.issueDate.plusDays(31), holder.offeredInterest + holder.offeredPrincipal)
        loanDailySnapshotService.makeSnapshotOfAllLoans(date("2001-01-01"), false)

        then: "No new snapshot created for same date"
        loanDailySnapshotRepository.findAll().size() == 1
        with(loanDailySnapshotRepository.findAll()[0]) {
            principalPaid == 0.00g
            status == LoanStatus.OPEN
            effectiveFrom == date("2001-01-01")
            effectiveTo == date("2100-01-01")
        }

        when:
        loanDailySnapshotService.makeSnapshotOfAllLoans(date("2001-01-02"), false)

        then: "New snapshot added for new date"
        loanDailySnapshotRepository.findAll().size() == 2
        with(loanDailySnapshotRepository.findAll()[0]) {
            principalPaid == 0.00g
            status == LoanStatus.OPEN
            effectiveFrom == date("2001-01-01")
            effectiveTo == date("2001-01-01")
        }
        with(loanDailySnapshotRepository.findAll()[1]) {
            principalPaid == holder.offeredPrincipal
            effectiveFrom == date("2001-01-02")
            effectiveTo == date("2100-01-01")
        }
    }

    def "Loan snapshoting stops at some point for closed loans"() {
        given:
        def holder = new LoanHolder()
        helper.applyAndDisburse(holder)
        helper.updateLoanStatus(holder, LoanStatus.CLOSED, LoanStatusDetail.PAID, date("2001-01-01"))

        expect:
        loanService.getLoan(holder.loanId).closeDate == date("2001-01-01")

        when:
        loanDailySnapshotService.makeSnapshotOfAllLoans(TimeMachine.today().plusDays(10), false)

        then:
        loanDailySnapshotRepository.findAll().empty

        when: "Force to create snapshot"
        loanDailySnapshotService.makeSnapshotOfAllLoans(TimeMachine.today().plusDays(10), true)

        then:
        loanDailySnapshotRepository.findAll().size() == 1
    }

    def "can't snapshot before issue date"() {
        given:
        def holder = new LoanHolder(issueDate: date("2017-09-02"))
        helper.applyAndDisburse(holder)
        assert loanDailySnapshotRepository.findAll().isEmpty()

        when:
        loanDailySnapshotService.makeSnapshotOfAllLoans(date("2017-09-01"), false)

        then:
        assert loanDailySnapshotRepository.findAll().isEmpty()

        when:
        loanDailySnapshotService.makeSnapshotOfAllLoans(date("2017-09-02"), false)

        then:
        assert loanDailySnapshotRepository.findAll().size() == 1

    }

}
