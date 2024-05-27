package fintech.spain.alfa.product

import fintech.TimeMachine
import fintech.dc.DcService
import fintech.dc.commands.LogDebtActionCommand
import fintech.lending.core.loan.LoanStatus
import fintech.lending.core.loan.LoanStatusDetail
import fintech.notification.NotificationHelper
import fintech.risk.checklist.CheckListConstants
import fintech.risk.checklist.model.CheckListQuery
import fintech.spain.asnef.AsnefService
import fintech.spain.asnef.LogRowStatus
import fintech.spain.dc.command.RescheduleCommand
import fintech.spain.dc.command.ReschedulingPreviewCommand
import fintech.spain.dc.model.ReschedulingPreview
import fintech.spain.alfa.product.cms.CmsSetup
import org.springframework.beans.factory.annotation.Autowired

import static fintech.DateUtils.date

class DcFacadeTest extends AbstractAlfaTest {

    @Autowired
    NotificationHelper notificationHelper

    @Autowired
    fintech.spain.alfa.product.asnef.internal.AlfaFileAsnefServiceBean alfaAsnefService

    @Autowired
    AsnefService asnefService

    @Autowired
    DcService dcService

    @Autowired
    fintech.spain.alfa.product.lending.ReschedulingLoanConsumerBean consumer

    def "Client's dni and email blacklisted, LOAN_SOLD_NOTIFICATION sent on debt sold"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .toLoan()
            .postToDc()
            .sellDebt()
            .toClient()

        then:
        !checkListService.isAllowed(CheckListQuery.builder().type(CheckListConstants.CHECKLIST_TYPE_DNI).value1(client.dni).build())
        !checkListService.isAllowed(CheckListQuery.builder().type(CheckListConstants.CHECKLIST_TYPE_EMAIL).value1(client.email).build())

        and:
        notificationHelper.countEmails(client.clientId, CmsSetup.LOAN_SOLD_NOTIFICATION) == 1
        notificationHelper.countSms(client.clientId, CmsSetup.LOAN_SOLD_NOTIFICATION) == 0
    }

    def "Client is excluded from ASNEF on dc sold"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .toLoan()
            .postToDc()
            .sellDebt()
            .toClient()

        then:
        client.client.isExcludedFromASNEF()
    }

    def "ASNEF Notifica are marked as exhausted on dc sold"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))
        def logId = alfaAsnefService.generateRpFile([loan.loanId], date("2018-01-01"))

        when:
        loan.postToDc().sellDebt()

        then:
        asnefService.get(logId).logRows.forEach { row -> assert row.status == LogRowStatus.EXHAUSTED }
    }

    def "ASNEF Notifica status is not changing on dc repurchase"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))
            .postToDc()
            .sellDebt()
        def logId = alfaAsnefService.generateRpFile([loan.loanId], date("2018-01-01"))

        when:
        loan.repurchaseDebt("Collections")

        then:
        asnefService.get(logId).logRows.forEach { row -> assert row.status == LogRowStatus.PREPARED }
    }

    def "Client is communication blocked in case of dc sold"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .toLoan()
            .postToDc()
            .sellDebt()
            .toClient()

        then:
        client.client.isBlockCommunication()
    }

    def "ExtensionCollection portfolio"() {
        given:
        TimeMachine.useFixedClockAt(TimeMachine.today().minusDays(6))
        BigDecimal amount = 200
        def term = 15
        def extensionDays = 30
        def dpd = 20
        def rescheduleDate = TimeMachine.today()
        def paymentDate = TimeMachine.today().minusDays(30)
        fintech.spain.alfa.product.testing.TestLoan loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .randomEmailAndName("Rescheduled")
            .registerDirectly()
            .issueActiveLoan(amount, term, rescheduleDate.minusDays(term).minusDays(dpd))
            .extend(fintech.spain.alfa.product.testing.TestLoan.expectedExtensionPrice(amount, extensionDays), paymentDate)
            .extend(fintech.spain.alfa.product.testing.TestLoan.expectedExtensionPrice(amount, extensionDays), paymentDate)
            .extend(fintech.spain.alfa.product.testing.TestLoan.expectedExtensionPrice(amount, extensionDays), paymentDate)
            .extend(fintech.spain.alfa.product.testing.TestLoan.expectedExtensionPrice(amount, extensionDays), paymentDate)
            .extend(fintech.spain.alfa.product.testing.TestLoan.expectedExtensionPrice(amount, extensionDays), paymentDate)
            .extend(fintech.spain.alfa.product.testing.TestLoan.expectedExtensionPrice(amount, extensionDays), paymentDate)
            .applyPenalty(rescheduleDate).postToDc()

        ReschedulingPreview schedule = loan.generateReschedulePreview(new ReschedulingPreviewCommand()
            .setNumberOfPayments(2)
            .setWhen(rescheduleDate))

        loan.reschedule(new RescheduleCommand().setPreview(schedule).setWhen(rescheduleDate))
        loan.triggerDcActions()
        loan.resolveDerivedValues()

        when:
        TimeMachine.useDefaultClock()
        consumer.consume()
        loan.triggerDcActions()

        then:
        loan.loan.status == LoanStatus.OPEN
        loan.loan.statusDetail == LoanStatusDetail.ACTIVE
        loan.debt.portfolio == 'Collections'
        !loan.loan.penaltySuspended
        !loan.testClient.client.excludedFromASNEF

        when:
        dcService.logAction(new LogDebtActionCommand(
            debtId: loan.debt.id,
            actionName: "ChangePortfolio",
            comments: "loan is active, extension period is more than 170 days, client has repaid more than 100% of the principal",
            agent: "Agent A",
            status: "NoStatus",
            nextActionAt: TimeMachine.now().plusSeconds(5),
            nextAction: "OutgoingCall",
            bulkActions: [
                "ChangePortfolio": new LogDebtActionCommand.BulkAction(
                    params: ["portfolio": "ExtensionCollections"]
                )
            ]
        ))
        loan.triggerDcActions()

        then:
        loan.debt.portfolio == 'ExtensionCollections'
        loan.loan.penaltySuspended
        loan.testClient.client.excludedFromASNEF

        when:
        def newRescheduleDate = TimeMachine.today()
        schedule = loan.generateReschedulePreview(new ReschedulingPreviewCommand()
            .setNumberOfPayments(2)
            .setWhen(newRescheduleDate))

        loan.reschedule(new RescheduleCommand().setPreview(schedule).setWhen(newRescheduleDate))
        loan.triggerDcActions()
        loan.resolveDerivedValues()
        consumer.consume()
        loan.triggerDcActions()

        then:
        loan.loan.status == LoanStatus.OPEN
        loan.loan.statusDetail == LoanStatusDetail.RESCHEDULED
        loan.debt.portfolio == 'Rescheduled'
        loan.loan.penaltySuspended
        loan.testClient.client.excludedFromASNEF
    }

    def "ExtensionCollection portfolio - penalties don't apply"() {
        given:
        TimeMachine.useFixedClockAt(date("2020-01-01"))
        BigDecimal amount = 200
        def term = 15
        def extensionDays = 30
        def dpd = 20
        def today = TimeMachine.today()
        fintech.spain.alfa.product.testing.TestLoan loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .randomEmailAndName("Rescheduled")
            .registerDirectly()
            .issueActiveLoan(amount, term, today.minusDays(term).minusDays(dpd))
            .extend(fintech.spain.alfa.product.testing.TestLoan.expectedExtensionPrice(amount, extensionDays), today)
            .extend(fintech.spain.alfa.product.testing.TestLoan.expectedExtensionPrice(amount, extensionDays), today)
            .extend(fintech.spain.alfa.product.testing.TestLoan.expectedExtensionPrice(amount, extensionDays), today)
            .extend(fintech.spain.alfa.product.testing.TestLoan.expectedExtensionPrice(amount, extensionDays), today)
            .extend(fintech.spain.alfa.product.testing.TestLoan.expectedExtensionPrice(amount, extensionDays), today)
            .extend(fintech.spain.alfa.product.testing.TestLoan.expectedExtensionPrice(amount, extensionDays), today)
            .applyPenalty(today).postToDc()

        loan.triggerDcActions()
        loan.resolveDerivedValues()

        when:
        TimeMachine.useFixedClockAt(date("2020-06-25"))
        consumer.consume()
        loan.triggerDcActions()

        then:
        loan.loan.status == LoanStatus.OPEN
        loan.loan.statusDetail == LoanStatusDetail.ACTIVE
        loan.debt.portfolio == 'Current'
        !loan.loan.penaltySuspended
        !loan.testClient.client.excludedFromASNEF

        when:
        dcService.logAction(new LogDebtActionCommand(
            debtId: loan.debt.id,
            actionName: "ChangePortfolio",
            comments: "loan is active, extension period is more than 170 days, client has repaid more than 100% of the principal",
            agent: "Agent A",
            status: "NoStatus",
            nextActionAt: TimeMachine.now().plusSeconds(5),
            nextAction: "OutgoingCall",
            bulkActions: [
                "ChangePortfolio": new LogDebtActionCommand.BulkAction(
                    params: ["portfolio": "ExtensionCollections"]
                )
            ]
        ))
        loan.applyPenalty(TimeMachine.today())
        loan.triggerDcActions()
        loan.resolveDerivedValues()

        then:
        loan.debt.portfolio == 'ExtensionCollections'
        loan.loan.penaltySuspended
        loan.loan.penaltyApplied == 37.60
        loan.testClient.client.excludedFromASNEF

        when: "penalties don't apply after several months passed"
        TimeMachine.useFixedClockAt(date("2020-12-31"))
        loan.applyPenalty(TimeMachine.today())
        loan.triggerDcActions()
        loan.resolveDerivedValues()

        then:
        loan.debt.portfolio == 'ExtensionCollections'
        loan.loan.penaltySuspended
        loan.loan.penaltyApplied == 37.60
        loan.testClient.client.excludedFromASNEF

    }
}
