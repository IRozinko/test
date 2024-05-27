package fintech.dc

import fintech.ClasspathUtils
import fintech.JsonUtils
import fintech.TimeMachine
import fintech.dc.commands.LogDebtActionCommand
import fintech.dc.db.Entities
import fintech.dc.model.DcSettings

import java.time.LocalDate

import static fintech.DateUtils.dateTime

class DcTest extends BaseSpecification {

    def setup() {
        settings = JsonUtils.readValue(ClasspathUtils.resourceToString("test-dc-settings.json"), DcSettings.class)
        dcSettingsService.saveSettings(settings, true)
    }

    def "post new loan"() {
        when:
        def id = dcService.postLoan(postCommand())

        then:
        with(debt(id)) {
            loanId == 1L
            clientId == 2L
            dpd == -2
            maxDpd == 10
            totalDue == 100.00g
            totalOutstanding == 500.00g
            totalPaid == 50.00g

            portfolio == "Current"
            status == "NoStatus"
            priority == 100
        }
    }

    def "paid in early collection"() {
        when:
        def debtId = dcService.postLoan(postCommand())

        then:
        debt(debtId).portfolio == "Current"

        when:
        dcService.postLoan(postCommand().setDpd(2))

        then:
        with (debt(debtId)) {
            portfolio == "NotInDc"
            status == "NoStatus"
            !nextActionAt
        }

        when:
        dcService.postLoan(postCommand().setTotalDue(0.0g))

        then:
        debt(debtId).portfolio == "Paid"
        debt(debtId).status == "NoStatus"
        debt(debtId).nextAction == null
        debt(debtId).nextActionAt == null
    }

    def "trigger actions via executor"() {
        when:
        def debtId = dcService.postLoan(postCommand().setTriggerActionsImmediately(false))

        then:
        debt(debtId).portfolio == "Current"

        when:
        debtExecutor.triggerActions(TimeMachine.now().plusMinutes(1))

        then:
        debt(debtId).portfolio == "Current"
    }

    def "trigger frequency condition"() {
        when:
        def debtId = dcService.postLoan(postCommand().setDpd(8).setTriggerActionsImmediately(true))
        debtExecutor.triggerActions(TimeMachine.now().plusDays(1))

        then:
        debt(debtId).portfolio == "Collections"
        queryFactory.selectFrom(Entities.action).where(Entities.action.debt.id.eq(debtId).and(Entities.action.actionName.eq("SendNotification (7 DPD)"))).fetchCount() == 1

        when:
        TimeMachine.useFixedClockAt(LocalDate.now().plusDays(2))
        dcService.postLoan(postCommand().setDpd(-1).setTriggerActionsImmediately(true))
        TimeMachine.useFixedClockAt(LocalDate.now().plusDays(7))
        dcService.postLoan(postCommand().setDpd(-1).setTriggerActionsImmediately(true))

        then:
        queryFactory.selectFrom(Entities.action).where(Entities.action.debt.id.eq(debtId).and(Entities.action.actionName.eq("SendNotification (7 DPD)"))).fetchCount() == 1

    }

    def "log action"() {
        given:
        def debtId = dcService.postLoan(postCommand())

        when:
        dcService.logAction(new LogDebtActionCommand(
            debtId: debtId,
            actionName: "ChangePortfolio",
            comments: "Client is dead",
            agent: "Agent A",
            status: "Negative",
            nextActionAt: dateTime("2001-01-01 11:00:00"),
            nextAction: "OutgoingCall",
            bulkActions: [
                    "ChangePortfolio": new LogDebtActionCommand.BulkAction(
                        params: ["portfolio": "Dead"]
                    )
            ]
        ))

        then:
        with (dcService.get(debtId)) {
            portfolio == "Dead"
            nextActionAt == dateTime("2001-01-01 11:00:00")
            nextAction == "OutgoingCall"
        }
    }
}
