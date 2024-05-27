package fintech.spain.alfa.product.acceptance

import fintech.TimeMachine
import fintech.dc.DcService
import fintech.dc.commands.LogDebtActionCommand
import fintech.lending.core.loan.LoanService
import fintech.lending.core.loan.LoanStatus
import fintech.lending.core.loan.LoanStatusDetail
import fintech.notification.NotificationHelper
import fintech.spain.dc.command.BreakReschedulingCommand
import fintech.spain.dc.command.RescheduleCommand
import fintech.spain.dc.command.ReschedulingPreviewCommand
import fintech.spain.dc.model.ReschedulingPreview
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.cms.CmsSetup
import fintech.spain.alfa.product.db.LoanReschedulingRepository
import fintech.spain.alfa.product.dc.DcFacade
import fintech.spain.alfa.product.lending.LoanReschedulingQuery

import fintech.transactions.TransactionType
import org.springframework.beans.factory.annotation.Autowired

import java.math.RoundingMode

import static fintech.DateUtils.date
import static fintech.TimeMachine.today
import static fintech.spain.alfa.product.db.Entities.reschedulingLoan

class AcceptanceReschedulingTest extends AbstractAlfaTest {

    @Autowired
    DcTestCases dcTestCases

    @Autowired
    DcFacade dcFacade

    @Autowired
    LoanService loanService

    @Autowired
    DcService dcService

    @Autowired
    fintech.spain.alfa.product.lending.ReschedulingLoanConsumerBean consumer

    @Autowired
    fintech.spain.alfa.product.lending.LoanReschedulingService loanReschedulingService

    @Autowired
    LoanReschedulingRepository loanReschedulingRepository

    @Autowired
    NotificationHelper notificationHelper

    def "generate preview - 2 payments"() {
        given:
        def loan = dcTestCases.loanInDpd(15)
        def loanBalance = loan.getBalance()

        when:
        ReschedulingPreview schedule = loan.generateReschedulePreview(new ReschedulingPreviewCommand()
            .setNumberOfPayments(2)
            .setWhen(date("2018-03-01")))

        then:
        assert schedule.getItems().size() == 2

        and:
        with(schedule.getItems().get(0)) {
            installmentSequence == 1
            dueDate == date("2018-03-03")
            periodFrom == date("2018-03-01")
            periodTo == date("2018-03-03")
            !generateInvoiceOnDate
            gracePeriodInDays == 5
            applyPenalty
            principalScheduled == loanBalance.getPrincipalOutstanding() / 2
            interestScheduled == (loanBalance.getInterestOutstanding() / 2).setScale(2, RoundingMode.FLOOR)
            penaltyScheduled == (loanBalance.getPenaltyOutstanding() / 2).setScale(2, RoundingMode.FLOOR)
            !feeItems
            totalScheduled == principalScheduled + interestScheduled + penaltyScheduled
        }

        and:
        with(schedule.getItems().get(1)) {
            installmentSequence == 2
            dueDate == date("2018-04-03")
            periodFrom == date("2018-03-05")
            periodTo == date("2018-04-03")
            !generateInvoiceOnDate
            gracePeriodInDays == 5
            applyPenalty
            principalScheduled == loanBalance.getPrincipalOutstanding() / 2
            interestScheduled == (loanBalance.getInterestOutstanding() / 2).setScale(2, RoundingMode.FLOOR)
            penaltyScheduled == 17.63
            !feeItems
            totalScheduled == principalScheduled + interestScheduled + penaltyScheduled
        }
    }

    def "generate preview - 3 payments"() {
        given:
        def loan = dcTestCases.loanInDpd(15)
        def loanBalance = loan.getBalance()

        when:
        ReschedulingPreview schedule = loan.generateReschedulePreview(new ReschedulingPreviewCommand()
            .setNumberOfPayments(3)
            .setWhen(date("2018-03-01")))

        then:
        assert schedule.getItems().size() == 3

        and:
        with(schedule.getItems().get(0)) {
            installmentSequence == 1
            dueDate == date("2018-03-03")
            periodFrom == date("2018-03-01")
            periodTo == date("2018-03-03")
            !generateInvoiceOnDate
            gracePeriodInDays == 5
            applyPenalty
            principalScheduled == (loanBalance.getPrincipalOutstanding() / 3).setScale(2, RoundingMode.HALF_UP)
            interestScheduled == (loanBalance.getInterestOutstanding() / 3).setScale(2, RoundingMode.HALF_UP)
            penaltyScheduled == (loanBalance.getPenaltyOutstanding() / 3).setScale(2, RoundingMode.HALF_UP)
            !feeItems
            totalScheduled == principalScheduled + interestScheduled + penaltyScheduled
        }

        and:
        with(schedule.getItems().get(1)) {
            installmentSequence == 2
            dueDate == date("2018-04-03")
            periodFrom == date("2018-03-05")
            periodTo == date("2018-04-03")
            !generateInvoiceOnDate
            gracePeriodInDays == 5
            applyPenalty
            principalScheduled == (loanBalance.getPrincipalOutstanding() / 3).setScale(2, RoundingMode.HALF_UP)
            interestScheduled == (loanBalance.getInterestOutstanding() / 3).setScale(2, RoundingMode.HALF_UP)
            penaltyScheduled == (loanBalance.getPenaltyOutstanding() / 3).setScale(2, RoundingMode.HALF_UP)
            !feeItems
            totalScheduled == principalScheduled + interestScheduled + penaltyScheduled
        }

        and:
        with(schedule.getItems().get(2)) {
            installmentSequence == 3
            dueDate == date("2018-05-03")
            periodFrom == date("2018-04-05")
            periodTo == date("2018-05-03")
            !generateInvoiceOnDate
            gracePeriodInDays == 5
            applyPenalty
            principalScheduled == (loanBalance.getPrincipalOutstanding() / 3).setScale(2, RoundingMode.FLOOR)
            interestScheduled == (loanBalance.getInterestOutstanding() / 3).setScale(2, RoundingMode.FLOOR)
            penaltyScheduled == (loanBalance.getPenaltyOutstanding() / 3).setScale(2, RoundingMode.FLOOR)
            !feeItems
            totalScheduled == principalScheduled + interestScheduled + penaltyScheduled
        }
    }

    def "generate preview - 4 payments"() {
        given:
        def loan = dcTestCases.loanInDpd(15)
        def loanBalance = loan.getBalance()

        when:
        ReschedulingPreview schedule = loan.generateReschedulePreview(new ReschedulingPreviewCommand()
            .setNumberOfPayments(4)
            .setWhen(date("2018-03-01")))

        then:
        assert schedule.getItems().size() == 4

        and:
        with(schedule.getItems().get(0)) {
            installmentSequence == 1
            dueDate == date("2018-03-03")
            periodFrom == date("2018-03-01")
            periodTo == date("2018-03-03")
            !generateInvoiceOnDate
            gracePeriodInDays == 5
            applyPenalty
            principalScheduled == (loanBalance.getPrincipalOutstanding() / 4).setScale(2, RoundingMode.HALF_UP)
            interestScheduled == (loanBalance.getInterestOutstanding() / 4).setScale(2, RoundingMode.HALF_UP)
            penaltyScheduled == (loanBalance.getPenaltyOutstanding() / 4).setScale(2, RoundingMode.HALF_UP)
            !feeItems
            totalScheduled == principalScheduled + interestScheduled + penaltyScheduled
        }

        and:
        with(schedule.getItems().get(1)) {
            installmentSequence == 2
            dueDate == date("2018-04-03")
            periodFrom == date("2018-03-05")
            periodTo == date("2018-04-03")
            !generateInvoiceOnDate
            gracePeriodInDays == 5
            applyPenalty
            principalScheduled == (loanBalance.getPrincipalOutstanding() / 4).setScale(2, RoundingMode.HALF_UP)
            interestScheduled == (loanBalance.getInterestOutstanding() / 4).setScale(2, RoundingMode.HALF_UP)
            penaltyScheduled == (loanBalance.getPenaltyOutstanding() / 4).setScale(2, RoundingMode.HALF_UP)
            !feeItems
            totalScheduled == principalScheduled + interestScheduled + penaltyScheduled
        }

        and:
        with(schedule.getItems().get(2)) {
            installmentSequence == 3
            dueDate == date("2018-05-03")
            periodFrom == date("2018-04-05")
            periodTo == date("2018-05-03")
            !generateInvoiceOnDate
            gracePeriodInDays == 5
            applyPenalty
            principalScheduled == (loanBalance.getPrincipalOutstanding() / 4).setScale(2, RoundingMode.HALF_UP)
            interestScheduled == (loanBalance.getInterestOutstanding() / 4).setScale(2, RoundingMode.HALF_UP)
            penaltyScheduled == (loanBalance.getPenaltyOutstanding() / 4).setScale(2, RoundingMode.HALF_UP)
            !feeItems
            totalScheduled == principalScheduled + interestScheduled + penaltyScheduled
        }

        and:
        with(schedule.getItems().get(3)) {
            installmentSequence == 4
            dueDate == date("2018-06-03")
            periodFrom == date("2018-05-05")
            periodTo == date("2018-06-03")
            !generateInvoiceOnDate
            gracePeriodInDays == 5
            applyPenalty
            principalScheduled == (loanBalance.getPrincipalOutstanding() / 4).setScale(2, RoundingMode.HALF_UP)
            interestScheduled == (loanBalance.getInterestOutstanding() / 4).setScale(2, RoundingMode.HALF_UP)
            penaltyScheduled == 8.82
            !feeItems
            totalScheduled == principalScheduled + interestScheduled + penaltyScheduled
        }
    }

    def "Check status Rescheduled -> Active -> Rescheduled"() {
        given:
        def loan = dcTestCases.loanInDpd(15)
        def loanTotalDue = loan.getLoan().totalDue

        when:
        ReschedulingPreview schedule = loan.generateReschedulePreview(
            new ReschedulingPreviewCommand()
                .setNumberOfPayments(2)
                .setWhen(today().plusDays(60))
        )

        then:
        assert schedule.getItems().size() == 2

        when:
        loan.reschedule(
            new RescheduleCommand().setPreview(schedule).setWhen(today())
        )
        loanService.resolveLoanDerivedValues(loan.loanId, today())


        then:
        with(loan.getLoan()) {
            status == LoanStatus.OPEN
            statusDetail == LoanStatusDetail.RESCHEDULED
            totalDue == 0
        }

        when:
        TimeMachine.useFixedClockAt(today().plusDays(35))
        consumer.consume()
        loanService.resolveLoanDerivedValues(loan.loanId, today())

        then:
        with(loan.getLoan()) {
            status == LoanStatus.OPEN
            statusDetail == LoanStatusDetail.ACTIVE
            totalDue == loanTotalDue
        }

        when:
        schedule = loan.generateReschedulePreview(
            new ReschedulingPreviewCommand()
                .setNumberOfPayments(2)
                .setWhen(today().plusDays(90))
        )

        and:
        TimeMachine.useFixedClockAt(today().plusDays(3))
        loan.reschedule(
            new RescheduleCommand()
                .setPreview(schedule)
                .setWhen(today())
        )
        loanService.resolveLoanDerivedValues(loan.loanId, today())

        then:
        with(loan.getLoan()) {
            status == LoanStatus.OPEN
            statusDetail == LoanStatusDetail.RESCHEDULED
            totalDue == 0 //
        }
    }

    def "Rescheduled -> BrokenRescheduled should always have the same totalDue"() {
        given:
        def loan = dcTestCases.loanInDpd(15)
        def loanTotalDue = loan.getLoan().totalDue

        when:
        ReschedulingPreview schedule = loan.generateReschedulePreview(
            new ReschedulingPreviewCommand()
                .setNumberOfPayments(2)
                .setWhen(today().plusDays(60))
        )

        then:
        assert schedule.getItems().size() == 2

        when:
        loan.reschedule(
            new RescheduleCommand().setPreview(schedule).setWhen(today())
        )

        and:
        TimeMachine.useFixedClockAt(today().plusDays(2))
        loan.breakRescheduling(today())
        loanService.resolveLoanDerivedValues(loan.loanId, today())

        then:
        with(loan.getLoan()) {
            status == LoanStatus.OPEN
            statusDetail == LoanStatusDetail.ACTIVE
            totalDue == loanTotalDue
        }

        when:
        TimeMachine.useFixedClockAt(today().plusDays(58))
        loanService.resolveLoanDerivedValues(loan.loanId, today())

        then:
        with(loan.getLoan()) {
            status == LoanStatus.OPEN
            statusDetail == LoanStatusDetail.ACTIVE
            totalDue == loanTotalDue
        }
    }

    def "break rescheduling"() {
        when:
        def loan = dcTestCases.rescheduleBroken()

        then:
        with(loan.getLoan()) {
            statusDetail == LoanStatusDetail.ACTIVE
            totalDue == totalOutstanding
        }

        and:
        loan.openInstallments().size() == 1
        loan.contracts().size() == 3
        loan.getTestClient().getClient().excludedFromASNEF
    }

    def "reschedule - debt status RescheduleOffered"() {
        when:
        def loan = dcTestCases.rescheduleOffered()

        then:
        assert loan.contract().getMaturityDate() == loan.openInstallments().last().getDueDate()
        assert loan.getLoan().getMaturityDate() == loan.openInstallments().last().getDueDate()

        and:
        assert loan.getDebt().getPortfolio() == "Rescheduled"
        assert loan.getDebt().getStatus() == "RescheduleOffered"
        loan.getTestClient().getClient().excludedFromASNEF
    }

    def "reschedule - debt status Rescheduled"() {
        when:
        def loan = dcTestCases.rescheduled(2)

        then:
        assert loan.getDebt().getPortfolio() == "Rescheduled"
        assert loan.getDebt().getStatus() == "RescheduleActivated"
        loan.getTestClient().getClient().excludedFromASNEF
    }

    def "reschedule - debt status RescheduleFailed"() {
        when:
        def loan = dcTestCases.rescheduleFailed()
        loan.triggerDcActions()

        then:
        assert loan.getDebt().getPortfolio() == "Collections"
        assert loan.getDebt().getStatus() == "RescheduleNonActivated"
        assert loan.getLoan().getStatusDetail() == LoanStatusDetail.ACTIVE
        assert loan.getLoan().getOverdueDays() > 0
        !loan.getTestClient().getClient().excludedFromASNEF

        when: "no cascading triggers"
        loan.triggerDcActions()

        then:
        assert loan.getDebt().getPortfolio() == "Collections"
        assert loan.getDebt().getStatus() == "RescheduleNonActivated"
        assert loan.getLoan().getStatusDetail() == LoanStatusDetail.ACTIVE
        assert loan.getLoan().getOverdueDays() > 0
        !loan.getTestClient().getClient().excludedFromASNEF
    }

    def "reschedule - RescheduleFailed happens even after status change to debt"() {
        given:
        def loan = dcTestCases.rescheduleOffered(today().minusDays(3))

        when: "log action and change status to delayed"
        dcService.logAction(new LogDebtActionCommand(
            debtId: loan.debt.id,
            actionName: "IncomingCall",
            comments: "Client calls",
            agent: "admin",
            status: "Delayed",
            bulkActions: [
                "LogActivity": new LogDebtActionCommand.BulkAction()
            ]
        ))

        then: "debt status is delayed"
        loan.getDebt().getStatus() == "Delayed"

        when:
        loan.triggerDcActions()
        loan.resolveDerivedValues()

        then: "reschedule is failed no matter if status is delayed"
        assert loan.getDebt().getPortfolio() == "Collections"
        assert loan.getDebt().getStatus() == "Delayed"
        assert loan.getLoan().getStatusDetail() == LoanStatusDetail.ACTIVE
        assert loan.getLoan().getOverdueDays() > 0
        !loan.getTestClient().getClient().excludedFromASNEF
    }

    def "reschedule - RescheduleActivated happens even after status change to debt"() {
        given:
        def loan = dcTestCases.rescheduleOffered(today().minusDays(1))

        when: "log action and change status to delayed"
        dcService.logAction(new LogDebtActionCommand(
            debtId: loan.debt.id,
            actionName: "IncomingCall",
            comments: "Client calls",
            agent: "admin",
            status: "Delayed",
            bulkActions: [
                "LogActivity": new LogDebtActionCommand.BulkAction()
            ]
        ))

        then: "debt status is delayed"
        loan.getDebt().getStatus() == "Delayed"

        when:
        loan.repay(loan.getLoan().getTotalDue(), today().minusDays(1))
        loan.triggerDcActions()
        loan.resolveDerivedValues()

        then: "reschedule is failed no matter if status is delayed"
        assert loan.getDebt().getPortfolio() == "Rescheduled"
        assert loan.getDebt().getStatus() == "Delayed"
    }

    def "reschedule - late payment"() {
        when:
        def loan = dcTestCases.rescheduled(3)

        then:
        assert loan.getDebt().getPortfolio() == "Rescheduled"
        assert loan.getDebt().getStatus() == "RescheduleActivated"
        loan.getTestClient().getClient().excludedFromASNEF
    }

    def "reschedule - due amounts"() {
        given:
        def loan = dcTestCases.loanInDpd(20)
        ReschedulingPreview schedule = loan.generateReschedulePreview(new ReschedulingPreviewCommand()
            .setNumberOfPayments(2).setWhen(today())
        )

        when:
        loan.reschedule(new RescheduleCommand().setPreview(schedule).setWhen(today()))
        def balance = loan.getBalance()
        def loanEntity = loan.getLoan()

        then:
        assert balance.getTotalOutstanding() == loan.openInstallments().sum { it.totalScheduled }
        assert balance.getPrincipalDue() == balance.getPrincipalOutstanding()
        assert balance.getInterestDue() == balance.getInterestOutstanding()
        assert balance.getPenaltyDue() == balance.getPenaltyOutstanding()
        assert balance.getFeeDue() == balance.getFeeOutstanding()
        assert balance.getTotalDue() == balance.getTotalOutstanding()

        and:
        assert loan.openInstallments().size() == 2
        assert loanEntity.getPrincipalDue() == loan.openInstallments().first().principalScheduled
        assert loanEntity.getInterestDue() == loan.openInstallments().first().interestScheduled
        assert loanEntity.getPenaltyDue() == loan.openInstallments().first().penaltyScheduled
        assert loanEntity.getFeeDue() == loan.openInstallments().first().feeScheduled
        assert loanEntity.getTotalDue() == loan.openInstallments().first().totalScheduled

        when:
        loan.repay(loanEntity.getTotalDue(), today().plusDays(2))
        loanEntity = loan.getLoan()

        then:
        assert loan.openInstallments().size() == 1
        assert loanEntity.getPrincipalDue() == 0.00
        assert loanEntity.getInterestDue() == 0.00
        assert loanEntity.getPenaltyDue() == 0.00
        assert loanEntity.getFeeDue() == 0.00
        assert loanEntity.getTotalDue() == 0.00

        when:
        loan.updateDerivedValues(today().plusDays(4))
        loanEntity = loan.getLoan()

        then:
        assert loan.openInstallments().size() == 1
        assert loanEntity.getPrincipalDue() == loan.openInstallments().last().principalScheduled
        assert loanEntity.getInterestDue() == loan.openInstallments().last().interestScheduled
        assert loanEntity.getPenaltyDue() == loan.openInstallments().last().penaltyScheduled
        assert loanEntity.getFeeDue() == loan.openInstallments().last().feeScheduled
        assert loanEntity.getTotalDue() == loan.openInstallments().last().totalScheduled

        when:
        loan.repay(loanEntity.getTotalOutstanding(), today().plusMonths(1).plusDays(2))
        loanEntity = loan.getLoan()

        then:
        assert loan.openInstallments().size() == 0
        assert loanEntity.totalOutstanding == 0.00
        assert loanEntity.getTotalDue() == 0.00
    }

    def "penalties on dpd 3"() {
        given:
        def expectedPenaltiesFor3days = 0
        def rescheduledDate = today()
        def loan = dcTestCases.rescheduleOffered(4, rescheduledDate)
        def prevBalance = loan.getLoan()

        when:
        // First installment due date is rescheduledDate + 2 days
        loan.applyPenalty(rescheduledDate.plusDays(1))
        loan.applyPenalty(rescheduledDate.plusDays(2))
        // Grace period is due date + 1 day + grace period in days
        loan.applyPenalty(rescheduledDate.plusDays(3))
        loan.applyPenalty(rescheduledDate.plusDays(4))

        then: "no penalties applied for 5 day grace period"
        with(loan.getLoan()) {
            totalOutstanding == prevBalance.totalOutstanding
        }

        when:
        loan.updateDerivedValues(rescheduledDate.plusDays(5))
        prevBalance = loan.getLoan()
        def prevInstallment = loan.openInstallments().first()
        loan.applyPenalty(rescheduledDate.plusDays(5))

        then:
        with(loan.getLoan()) {
            assert penaltyApplied == prevBalance.getPenaltyApplied() + expectedPenaltiesFor3days
            assert penaltyDue == prevBalance.getPenaltyDue() + expectedPenaltiesFor3days
            assert totalOutstanding == prevBalance.getTotalOutstanding() + expectedPenaltiesFor3days
            assert totalDue == prevBalance.getTotalDue() + expectedPenaltiesFor3days
        }

        and:
        assert loan.openInstallments().first().getTotalDue() == prevInstallment.getTotalDue() + expectedPenaltiesFor3days
    }

    def "reschedule repayment"() {
        given:
        def loan = dcTestCases.rescheduleOffered()

        when:
        loan.repay(loan.getLoan().getTotalDue(), today().plusDays(2))

        then:
        def repaymentTransaction = loan.transactions().sort { it.id }.last()
        assert repaymentTransaction.getTransactionType() == TransactionType.REPAYMENT
        assert repaymentTransaction.getTransactionSubType() == "RESCHEDULE_REPAYMENT"
        assert repaymentTransaction.getFeePaid() == 0
    }

    def "loan rescheduling - broken when there is no repayment after grace period"() {
        given:
        def loan = dcTestCases.rescheduleOffered()

        when:
        loan.repay(loan.getLoan().getTotalDue(), today().plusDays(2))
        consumer.consume()

        then:
        def repaymentTransaction = loan.transactions().sort { it.id }.last()
        assert repaymentTransaction.getTransactionType() == TransactionType.REPAYMENT
        assert repaymentTransaction.getTransactionSubType() == "RESCHEDULE_REPAYMENT"
        assert repaymentTransaction.getFeePaid() == 0

        when:
        TimeMachine.useFixedClockAt(today().plusMonths(1).plusDays(2))
        consumer.consume()

        then:
        loan.getLoan().getStatusDetail() == LoanStatusDetail.RESCHEDULED

        when:
        TimeMachine.useFixedClockAt(today().plusMonths(1).plusDays(3))
        consumer.consume()
        loanService.resolveLoanDerivedValues(loan.loanId, today())

        then:
        loan.getLoan().getStatusDetail() == LoanStatusDetail.ACTIVE
    }

    def "loan rescheduling - active"() {
        given:
        def loan = dcTestCases.rescheduled(4)

        when:
        consumer.consume()

        then:
        def loanRescheduling = loanReschedulingService.findLoanRescheduling(LoanReschedulingQuery.rescheduled(loan.getLoan().getId()))
        with(loanRescheduling.get()) {
            it.loanId == loan.getLoan().getId()
            it.status == fintech.spain.alfa.product.lending.LoanReschedulingStatus.RESCHEDULED
            it.numberOfPayments == 4
        }
        loan.getLoan().getStatus() == LoanStatus.OPEN
        loan.getLoan().getStatusDetail() == LoanStatusDetail.RESCHEDULED
    }

    def "loan rescheduling - cancelled when there is no repayment"() {
        given:
        def loan = dcTestCases.rescheduleOffered()

        when:
        consumer.consume()

        then:
        def loanRescheduling = loanReschedulingService.findLoanRescheduling(LoanReschedulingQuery.rescheduled(loan.getLoan().getId()))
        with(loanRescheduling.get()) {
            it.loanId == loan.getLoan().getId()
            it.status == fintech.spain.alfa.product.lending.LoanReschedulingStatus.RESCHEDULED
            it.numberOfPayments == 2
        }
        loan.getLoan().getStatus() == LoanStatus.OPEN
        loan.getLoan().getStatusDetail() == LoanStatusDetail.RESCHEDULED

        when:
        TimeMachine.useFixedClockAt(today().plusDays(6))
        consumer.consume()

        then:
        loanReschedulingRepository.count(reschedulingLoan.loan.id.eq(loan.loanId) & reschedulingLoan.status.eq(fintech.spain.alfa.product.lending.LoanReschedulingStatus.CANCELLED)) == 1
        loan.getLoan().getStatus() == LoanStatus.OPEN
        loan.getLoan().getStatusDetail() == LoanStatusDetail.ACTIVE
    }

    def "loan rescheduling - reactivate rescheduling after cancel"() {
        given:
        def loan = dcTestCases.rescheduleOffered()

        when:
        consumer.consume()
        loan.triggerDcActions()

        then:
        def loanRescheduling = loanReschedulingService.findLoanRescheduling(LoanReschedulingQuery.rescheduled(loan.getLoan().getId()))
        with(loanRescheduling.get()) {
            it.loanId == loan.getLoan().getId()
            it.status == fintech.spain.alfa.product.lending.LoanReschedulingStatus.RESCHEDULED
            it.numberOfPayments == 2
        }
        loan.getLoan().getStatus() == LoanStatus.OPEN
        loan.getLoan().getStatusDetail() == LoanStatusDetail.RESCHEDULED
        loan.getTestClient().getClient().excludedFromASNEF

        when:
        TimeMachine.useFixedClockAt(today().plusDays(6))
        consumer.consume()

        then:
        loanReschedulingRepository.count(reschedulingLoan.loan.id.eq(loan.loanId) & reschedulingLoan.status.eq(fintech.spain.alfa.product.lending.LoanReschedulingStatus.CANCELLED)) == 1
        loan.getLoan().getStatus() == LoanStatus.OPEN
        loan.getLoan().getStatusDetail() == LoanStatusDetail.ACTIVE

        when:
        def reactivateReschedulingDate = today().plusDays(10)
        TimeMachine.useFixedClockAt(reactivateReschedulingDate)
        loan = dcTestCases.rescheduleOffered(reactivateReschedulingDate)
        loanRescheduling = loanReschedulingService.findLoanRescheduling(LoanReschedulingQuery.rescheduled(loan.getLoan().getId()))

        then:
        with(loanRescheduling.get()) {
            it.loanId == loan.getLoan().getId()
            it.status == fintech.spain.alfa.product.lending.LoanReschedulingStatus.RESCHEDULED
            it.numberOfPayments == 2
        }
        loan.getLoan().getStatus() == LoanStatus.OPEN
        loan.getLoan().getStatusDetail() == LoanStatusDetail.RESCHEDULED
    }

    def "rescheduled loan - repay all installments during one day"() {
        given:

        def prepaymentDate = today().plusDays(2)
        when:
        def rescheduledDate = today()
        def numberOfPayments = 3
        def loan = dcTestCases.rescheduleOffered(numberOfPayments, rescheduledDate)

        then:
        loan.openInstallments().size() == 3

        when:
        loan.repay(loan.getLoan().getTotalDue(), prepaymentDate)

        then:
        def repaymentTransaction = loan.transactions().sort { it.id }.last()
        assert repaymentTransaction.getTransactionType() == TransactionType.REPAYMENT
        assert repaymentTransaction.getTransactionSubType() == "RESCHEDULE_REPAYMENT"
        assert repaymentTransaction.getFeePaid() == 0
        loan.openInstallments().size() == 2

        when: "client wants to prepay all pending installments"
        loan.openInstallments().forEach({ i ->
            loan.repay(i.getTotalDue(), prepaymentDate)
        })
        def loanRescheduling = loanReschedulingService.findLoanRescheduling(LoanReschedulingQuery.paid(loan.getLoan().getId()))

        then:
        with(loanRescheduling.get()) {
            it.loanId == loan.getLoan().getId()
            it.status == fintech.spain.alfa.product.lending.LoanReschedulingStatus.RESCHEDULED_PAID
            it.numberOfPayments == 3
        }
        loan.openInstallments().size() == 0
        loan.getStatus() == LoanStatus.CLOSED
        loan.getStatusDetail() == LoanStatusDetail.RESCHEDULED_PAID
    }


    def "loan rescheduling - cancel rescheduling"() {
        given:
        def loan = dcTestCases.rescheduled(4)

        when:
        consumer.consume()

        then:
        def loanRescheduling = loanReschedulingService.findLoanRescheduling(LoanReschedulingQuery.rescheduled(loan.getLoan().getId()))
        with(loanRescheduling.get()) {
            it.loanId == loan.getLoan().getId()
            it.status == fintech.spain.alfa.product.lending.LoanReschedulingStatus.RESCHEDULED
            it.numberOfPayments == 4
        }
        loan.getLoan().getStatus() == LoanStatus.OPEN
        loan.getLoan().getStatusDetail() == LoanStatusDetail.RESCHEDULED

        when:
        dcFacade.breakRescheduling(new BreakReschedulingCommand(loanId: loan.loanId, when: today()))

        then:
        loanReschedulingRepository.count(reschedulingLoan.loan.id.eq(loan.loanId) & reschedulingLoan.status.eq(fintech.spain.alfa.product.lending.LoanReschedulingStatus.CANCELLED)) == 1

        loan.getLoan().getStatus() == LoanStatus.OPEN
        loan.getLoan().getStatusDetail() == LoanStatusDetail.ACTIVE

        when:
        loan = dcTestCases.rescheduled(2)
        consumer.consume()
        then:
        def newLoanRescheduling = loanReschedulingService.findLoanRescheduling(LoanReschedulingQuery.rescheduled(loan.getLoan().getId()))
        with(newLoanRescheduling.get()) {
            it.loanId == loan.getLoan().getId()
            it.status == fintech.spain.alfa.product.lending.LoanReschedulingStatus.RESCHEDULED
            it.numberOfPayments == 2
        }

    }

    def "loan rescheduling - notifications"() {
        given:
        TimeMachine.useFixedClockAt(date("2017-01-01"))
        def loan = dcTestCases.rescheduleOffered(4, today())
        def client = loan.getTestClient().client

        when:
        consumer.consume()

        then:
        def loanRescheduling = loanReschedulingService.findLoanRescheduling(LoanReschedulingQuery.rescheduled(loan.getLoan().getId()))
        with(loanRescheduling.get()) {
            it.loanId == loan.getLoan().getId()
            it.status == fintech.spain.alfa.product.lending.LoanReschedulingStatus.RESCHEDULED
            it.numberOfPayments == 4
        }
        loan.getLoan().getStatus() == LoanStatus.OPEN
        loan.getLoan().getStatusDetail() == LoanStatusDetail.RESCHEDULED

        when:
        def amount = loan.firstInstallment().get().getTotalDue()
        loan.repay(amount, today())
        consumer.consume()
        TimeMachine.useFixedClockAt(date("2017-02-05"))

        then:
        notificationHelper.countSms(client.id, CmsSetup.RESCHEDULING_REMINDER_48_HOURS) == 1
        notificationHelper.countSms(client.id, CmsSetup.RESCHEDULING_EXPIRED_2_DAYS) == 0
        notificationHelper.countEmails(client.id, CmsSetup.RESCHEDULING_EXPIRED_3_DAYS) == 0
        notificationHelper.countSms(client.id, CmsSetup.RESCHEDULING_EXPIRED_4_DAYS) == 0

        when:
        consumer.consume()

        then:
        notificationHelper.countSms(client.id, CmsSetup.RESCHEDULING_REMINDER_48_HOURS) == 1
        notificationHelper.countSms(client.id, CmsSetup.RESCHEDULING_EXPIRED_2_DAYS) == 1
        notificationHelper.countEmails(client.id, CmsSetup.RESCHEDULING_EXPIRED_3_DAYS) == 0
        notificationHelper.countSms(client.id, CmsSetup.RESCHEDULING_EXPIRED_4_DAYS) == 0

        when:
        TimeMachine.useFixedClockAt(date("2017-02-06"))
        consumer.consume()

        then:
        notificationHelper.countSms(client.id, CmsSetup.RESCHEDULING_REMINDER_48_HOURS) == 1
        notificationHelper.countSms(client.id, CmsSetup.RESCHEDULING_EXPIRED_2_DAYS) == 1
        notificationHelper.countEmails(client.id, CmsSetup.RESCHEDULING_EXPIRED_3_DAYS) == 1
        notificationHelper.countSms(client.id, CmsSetup.RESCHEDULING_EXPIRED_4_DAYS) == 0

        when:
        TimeMachine.useFixedClockAt(date("2017-02-07"))
        consumer.consume()

        then:
        notificationHelper.countSms(client.id, CmsSetup.RESCHEDULING_REMINDER_48_HOURS) == 1
        notificationHelper.countSms(client.id, CmsSetup.RESCHEDULING_EXPIRED_2_DAYS) == 1
        notificationHelper.countEmails(client.id, CmsSetup.RESCHEDULING_EXPIRED_3_DAYS) == 1
        notificationHelper.countSms(client.id, CmsSetup.RESCHEDULING_EXPIRED_4_DAYS) == 1

    }

    def "loan rescheduling - full lifecycle"() {
        given:
        TimeMachine.useFixedClockAt(date("2017-01-01"))
        def loan = dcTestCases.rescheduleOffered(4, today())

        when:
        consumer.consume()

        then:
        def loanRescheduling = loanReschedulingService.findLoanRescheduling(LoanReschedulingQuery.rescheduled(loan.getLoan().getId()))
        with(loanRescheduling.get()) {
            it.loanId == loan.getLoan().getId()
            it.status == fintech.spain.alfa.product.lending.LoanReschedulingStatus.RESCHEDULED
            it.numberOfPayments == 4
        }
        loan.getLoan().getStatus() == LoanStatus.OPEN
        loan.getLoan().getStatusDetail() == LoanStatusDetail.RESCHEDULED

        when:
        TimeMachine.useFixedClockAt(date("2017-01-05"))
        consumer.consume()
        loanRescheduling = loanReschedulingService.findLoanRescheduling(LoanReschedulingQuery.rescheduled(loan.getLoan().getId()))

        then:
        with(loanRescheduling.get()) {
            it.loanId == loan.getLoan().getId()
            it.status == fintech.spain.alfa.product.lending.LoanReschedulingStatus.PENDING_TO_BREAK
            it.numberOfPayments == 4
        }

        when:
        TimeMachine.useFixedClockAt(date("2017-01-07"))
        consumer.consume()
        loanRescheduling = loanReschedulingService.findLoanRescheduling(LoanReschedulingQuery.cancelled(loan.getLoan().getId()))

        then:
        with(loanRescheduling.get()) {
            it.loanId == loan.getLoan().getId()
            it.status == fintech.spain.alfa.product.lending.LoanReschedulingStatus.CANCELLED
            it.numberOfPayments == 4
        }
        loan.getLoan().getStatus() == LoanStatus.OPEN
        loan.getLoan().getStatusDetail() == LoanStatusDetail.ACTIVE

        when: "client wants one more time reschedule loan"
        TimeMachine.useFixedClockAt(date("2017-02-01"))
        loan = dcTestCases.rescheduleOffered(2, today())
        consumer.consume()

        then:
        def newLoanRescheduling = loanReschedulingService.findLoanRescheduling(LoanReschedulingQuery.rescheduled(loan.getLoan().getId()))
        with(newLoanRescheduling.get()) {
            it.loanId == loan.getLoan().getId()
            it.status == fintech.spain.alfa.product.lending.LoanReschedulingStatus.RESCHEDULED
            it.numberOfPayments == 2
        }

        when:
        TimeMachine.useFixedClockAt(date("2017-02-05"))
        consumer.consume()
        newLoanRescheduling = loanReschedulingService.findLoanRescheduling(LoanReschedulingQuery.rescheduled(loan.getLoan().getId()))

        then:
        with(newLoanRescheduling.get()) {
            it.loanId == loan.getLoan().getId()
            it.status == fintech.spain.alfa.product.lending.LoanReschedulingStatus.PENDING_TO_BREAK
            it.numberOfPayments == 2
        }

        when: "full repayment"
        loan.openInstallments().forEach({ i ->
            loan.repay(i.getTotalDue(), today())
        })
         newLoanRescheduling = loanReschedulingService.findLoanRescheduling(LoanReschedulingQuery.paid(loan.getLoan().getId()))

        then:
        with(newLoanRescheduling.get()) {
            it.loanId == loan.getLoan().getId()
            it.status == fintech.spain.alfa.product.lending.LoanReschedulingStatus.RESCHEDULED_PAID
            it.numberOfPayments == 2
        }
        loan.openInstallments().size() == 0
        loan.getStatus() == LoanStatus.CLOSED
        loan.getStatusDetail() == LoanStatusDetail.RESCHEDULED_PAID
    }
}
