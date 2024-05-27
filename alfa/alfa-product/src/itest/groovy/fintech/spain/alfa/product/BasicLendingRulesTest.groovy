package fintech.spain.alfa.product

import fintech.TimeMachine
import fintech.lending.core.PeriodUnit
import fintech.lending.core.application.LoanApplicationService
import fintech.lending.core.application.LoanApplicationType
import fintech.lending.core.application.commands.SubmitLoanApplicationCommand
import fintech.risk.checklist.CheckListConstants
import fintech.risk.checklist.CheckListService
import fintech.risk.checklist.commands.AddCheckListEntryCommand
import fintech.spain.equifax.mock.MockedEquifaxResponse
import org.springframework.beans.factory.annotation.Autowired

import static fintech.DateUtils.date

class BasicLendingRulesTest extends AbstractAlfaTest {

    @Autowired
    CheckListService checkListService

    @Autowired
    LoanApplicationService loanApplicationService

    def "client already has open loan"() {
        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))
            .toClient()
            .submitApplicationAndStartFirstLoanWorkflow(300.00, 30, date("2018-03-01"))
            .toLoanWorkflow()
            .runAll()

        then:
        workflow.isTerminated()
        workflow.toApplication().isRejected()
        workflow.toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_CLIENT_HAS_OPEN_LOAN
    }

    def "province blacklist"() {
        given:
        checkListService.addEntry(new AddCheckListEntryCommand(CheckListConstants.CHECKLIST_TYPE_PROVINCE_CODE, "35", ""))
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        client.applicationForm.address.postalCode = "3588"

        expect:
        client.signUp()
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_PROVINCE_NOT_ALLOWED
    }

    def "ApplicationCountWithin30Days"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()

        8.times {
            loanApplicationService.cancel(
                loanApplicationService.submit(new SubmitLoanApplicationCommand(type: LoanApplicationType.NEW_LOAN, clientId: client.clientId, applicationNumber: UUID.randomUUID().toString(), productId: AlfaConstants.PRODUCT_ID, principal: 1000.00, periodCount: 30, periodUnit: PeriodUnit.DAY, submittedAt: TimeMachine.now(), loansPaid: 0)),
                "test"
            )
        }

        expect:
        client
            .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == "ApplicationCountWithin30Days"
    }

    def "RejectionCountIn30Days"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()

        5.times {
            loanApplicationService.reject(
                loanApplicationService.submit(new SubmitLoanApplicationCommand(type: LoanApplicationType.NEW_LOAN, clientId: client.clientId, applicationNumber: UUID.randomUUID().toString(), productId: AlfaConstants.PRODUCT_ID, principal: 1000.00, periodCount: 30, periodUnit: PeriodUnit.DAY, submittedAt: TimeMachine.now(), loansPaid: 0)),
                "test"
            )
        }

        expect:
        client
            .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == "RejectionCountIn30Days"
    }

    def "RejectionCountIn7Days"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()

        4.times {
            loanApplicationService.reject(
                loanApplicationService.submit(new SubmitLoanApplicationCommand(type: LoanApplicationType.NEW_LOAN, clientId: client.clientId, applicationNumber: UUID.randomUUID().toString(), productId: AlfaConstants.PRODUCT_ID, principal: 1000.00, periodCount: 30, periodUnit: PeriodUnit.DAY, submittedAt: TimeMachine.now(), loansPaid: 0)),
                "test"
            )
        }

        expect:
        client
            .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == "RejectionCountIn7Days"
    }

    def "FeePenaltyPaid"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()

        client
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .applyPenalty(TimeMachine.today().plusDays(80))
            .repayAll(TimeMachine.today().plusDays(80))

        expect:
        client
            .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == "FeePenaltyPaid"
    }

    def "PrincipalDisbursedLessThanCashIn"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()

        def loan = client
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 20, TimeMachine.today().minusDays(20))
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()

        loan
            .writeOff(TimeMachine.today(), loan.getBalance().principalDue, loan.getBalance().interestDue)

        expect:
        client
            .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == "PrincipalDisbursedLessThanCashIn"
    }

    def "PrincipalSoldRule"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()

        client
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repay(1000.00, TimeMachine.today())

        client
            .submitApplicationAndStartFirstLoanWorkflow(500.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .sellLoan()

        expect:
        client
            .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == "PrincipalSoldRule"
    }

    def "DaysSinceLastApplicationRejectAndRejectionReason"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()

        mockEquifaxProvider.responseSupplier = MockedEquifaxResponse.DEBT_AMOUNT

        def workflow = client
            .toLoanWorkflow()
            .runAll()

        expect:
        workflow.toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_EQUIFAX_DEBT_AMOUNT

        client
            .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == "DaysSinceLastApplicationRejectAndRejectionReason"
    }

    def "TotalOverdueDays"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()

        client
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today().plusDays(30).plusDays(20))

        client
            .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today().plusDays(30).plusDays(81))

        expect:
        client
            .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == "TotalOverdueDays"
    }

    def "MaxOverdueDays"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()

        client
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today().plusDays(30).plusDays(20))

        client
            .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today().plusDays(30).plusDays(46))

        expect:
        client
            .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == "MaxOverdueDays"
    }

    def "MaxOverdueDaysIn12Months"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()

        client
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today().plusDays(30).plusDays(20))

        client
            .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today().plusDays(30).plusDays(31))

        expect:
        client
            .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == "MaxOverdueDaysIn12Months"
    }

    def "LastLoanOverdueDays"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()

        client
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today().plusDays(30).plusDays(21))

        expect:
        client
            .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == "LastLoanOverdueDays"
    }

    def "Age - too young"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        client.setDateOfBirth(TimeMachine.today().minusYears(20))

        expect:
        client
            .signUp()
            .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_AGE_TOO_YOUNG
    }

    def "Age - too old"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        client.setDateOfBirth(TimeMachine.today().minusYears(70))

        expect:
        client
            .signUp()
            .submitApplicationAndStartFirstLoanWorkflow(1000.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_AGE_TOO_OLD
    }
}
