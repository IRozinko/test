package fintech.dc

import com.querydsl.jpa.impl.JPAQueryFactory
import fintech.TimeMachine
import fintech.dc.commands.PostLoanCommand
import fintech.dc.model.DcSettings
import fintech.dc.model.Debt
import fintech.dc.spi.DcDefaults
import fintech.dc.spi.DebtBatchJobs
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    DcService dcService

    @Autowired
    DcSettingsService dcSettingsService

    @Autowired
    DcDefaults dcDefaults

    @Autowired
    DebtBatchJobs debtExecutor

    @Autowired
    JPAQueryFactory queryFactory

    DcSettings settings

    def setup() {
        testDatabase.cleanDb()

        dcDefaults.init()
    }

    Debt debt(Long debtId) {
        return dcService.get(debtId)
    }

    PostLoanCommand postCommand() {
        new PostLoanCommand(
            loanId: 1L,
            loanNumber: "1001",
            clientId: 2L,
            dpd: -2,
            maxDpd: 10,
            totalDue: 100.00g,
            interestDue: 20.00g,
            principalDue: 75.00g,
            penaltyDue: 10.00g,
            feeDue: 5.00g,
            totalOutstanding: 500.00g,
            interestOutstanding: 100.00g,
            principalOutstanding: 300.00g,
            penaltyOutstanding: 70.00g,
            feeOutstanding: 30.00g,
            totalPaid: 50.00g,
            interestPaid: 30.00g,
            principalPaid: 15.00g,
            penaltyPaid: 5.00g,
            feePaid: 0.00g,
            triggerActionsImmediately: true,
            maturityDate: TimeMachine.today().plusDays(365),
            paymentDueDate: TimeMachine.today().plusDays(2),
            loanStatus: "OPEN",
            loanStatusDetail: "ACTIVE"
        )
    }
}
