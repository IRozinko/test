package fintech.dc

import fintech.ClasspathUtils
import fintech.JsonUtils
import fintech.dc.commands.AddAgentAbsenceCommand
import fintech.dc.commands.AssignDebtCommand
import fintech.dc.commands.RemoveAgentAbsenceCommand
import fintech.dc.commands.SaveAgentCommand
import fintech.dc.db.DcAgentRepository
import fintech.dc.model.DcSettings
import org.springframework.beans.factory.annotation.Autowired

import static fintech.DateUtils.date
import static fintech.TimeMachine.today

class DcAgentsTest extends BaseSpecification {

    @Autowired
    DcAgentRepository agentRepository

    @Autowired
    DcAgentService dcAgentService

    def setup() {
        settings = JsonUtils.readValue(ClasspathUtils.resourceToString("test-dc-settings.json"), DcSettings.class)
        dcSettingsService.saveSettings(settings, true)
    }

    def "save agent"() {
        expect:
        agentRepository.count() == 0

        when:
        def id = dcAgentService.saveAgent(new SaveAgentCommand(agent: "Agent A", portfolios: ["Reminder", "EarlyCollection"]))

        then:
        agentRepository.count() == 1

        and:
        with(agentRepository.getRequired(id)) {
            agent == "Agent A"
            !disabled
            portfolios == ["Reminder", "EarlyCollection"]
        }

        when:
        dcAgentService.saveAgent(new SaveAgentCommand(agent: "Agent A", disabled: true, portfolios: ["LateCollection", "EarlyCollection"]))

        then:
        with(agentRepository.getRequired(id)) {
            agent == "Agent A"
            disabled
            portfolios == ["LateCollection", "EarlyCollection"]
        }

        when:
        dcAgentService.saveAgent(new SaveAgentCommand(agent: "Agent A", disabled: true, portfolios: []))

        then:
        with(agentRepository.getRequired(id)) {
            portfolios == []
        }
    }


    def "empty agent priorities"() {
        when:
        def priorities = dcAgentService.getAgentPriorities(today(), "EarlyCollection", null)

        then:
        priorities.empty
    }

    def "agent priorities"() {
        given:
        def debt1 = dcService.postLoan(postCommand().setLoanId(1).setLoanNumber("1").setDpd(4).setTotalDue(100.00g))
        def debt2 = dcService.postLoan(postCommand().setLoanId(2).setLoanNumber("2").setDpd(4).setTotalDue(400.00g))
        def debt3 = dcService.postLoan(postCommand().setLoanId(3).setLoanNumber("3").setDpd(4).setTotalDue(300.00g))
        dcAgentService.saveAgent(new SaveAgentCommand(agent: "Agent A", portfolios: ["Collections"]))
        dcAgentService.saveAgent(new SaveAgentCommand(agent: "Agent B", portfolios: ["Collections"]))

        expect:
        dcAgentService.getAgentPriorities(today(), "Collections", null).size() == 2

        when:
        dcService.assignDebt(new AssignDebtCommand(agent: "Agent A", debtId: debt1))
        def priorities = dcAgentService.getAgentPriorities(today(), "Collections", null)

        then:
        priorities.size() == 2
        priorities[0].agent == "Agent B"
        priorities[0].debtCount == 0
        priorities[0].amountDue == 0.00g
        priorities[1].agent == "Agent A"
        priorities[1].debtCount == 1
        priorities[1].amountDue == 100.00g

        when:
        dcService.assignDebt(new AssignDebtCommand(agent: "Agent B", debtId: debt2))
        priorities = dcAgentService.getAgentPriorities(today(), "Collections", null)

        then:
        priorities.size() == 2
        priorities[0].agent == "Agent A"
        priorities[0].debtCount == 1
        priorities[0].amountDue == 100.00g
        priorities[1].agent == "Agent B"
        priorities[1].debtCount == 1
        priorities[1].amountDue == 400.00g

        when:
        dcService.assignDebt(new AssignDebtCommand(agent: "Agent A", debtId: debt3))
        priorities = dcAgentService.getAgentPriorities(today(), "Collections", null)

        then:
        priorities.size() == 2
        priorities[0].agent == "Agent B"
        priorities[0].debtCount == 1
        priorities[0].amountDue == 400.00g
        priorities[1].agent == "Agent A"
        priorities[1].debtCount == 2
        priorities[1].amountDue == 400.00g
    }

    def "auto assign debts"() {
        given:
        dcAgentService.saveAgent(new SaveAgentCommand(agent: "Agent A", portfolios: ["Collections"]))
        dcAgentService.saveAgent(new SaveAgentCommand(agent: "Agent B", portfolios: ["Collections"]))

        when:
        def debt1 = dcService.postLoan(postCommand().setLoanId(1).setLoanNumber("1").setDpd(4).setTotalDue(100.00g))
        def debt2 = dcService.postLoan(postCommand().setLoanId(2).setLoanNumber("2").setDpd(4).setTotalDue(400.00g))
        def debt3 = dcService.postLoan(postCommand().setLoanId(3).setLoanNumber("3").setDpd(4).setTotalDue(300.00g))

        then:
        dcService.get(debt1).agent == "Agent A"
        dcService.get(debt2).agent == "Agent B"
        dcService.get(debt3).agent == "Agent A"
    }


    def "disabled agent not in priorities"() {
        given:
        dcAgentService.saveAgent(new SaveAgentCommand(agent: "Agent A", portfolios: ["EarlyCollection"]))

        expect:
        dcAgentService.getAgentPriorities(today(), "EarlyCollection", null).size() == 1

        when:
        dcAgentService.saveAgent(new SaveAgentCommand(agent: "Agent A", disabled: true, portfolios: ["EarlyCollection"]))

        then:
        dcAgentService.getAgentPriorities(today(), "EarlyCollection", null).size() == 0
    }

    def "agent without portfolio not in priorities"() {
        given:
        dcAgentService.saveAgent(new SaveAgentCommand(agent: "Agent A", portfolios: ["EarlyCollection"]))

        expect:
        dcAgentService.getAgentPriorities(today(), "LateCollection", null).size() == 0
    }

    def "agent with absence not in priorities"() {
        when:
        dcAgentService.saveAgent(new SaveAgentCommand(agent: "Agent A", portfolios: ["EarlyCollection"]))
        def absenceId = dcAgentService.addAgentAbsence(new AddAgentAbsenceCommand(agent: "Agent A", dateFrom: date("2001-01-02"), dateTo: date("2001-01-02")))
        dcAgentService.addAgentAbsence(new AddAgentAbsenceCommand(agent: "Agent A", dateFrom: date("2001-02-01"), dateTo: date("2001-02-28")))

        then:
        dcAgentService.getAgentPriorities(date("2001-01-01"), "EarlyCollection", null).size() == 1
        dcAgentService.getAgentPriorities(date("2001-01-02"), "EarlyCollection", null).size() == 0
        dcAgentService.getAgentPriorities(date("2001-01-03"), "EarlyCollection", null).size() == 1
        dcAgentService.getAgentPriorities(date("2001-02-10"), "EarlyCollection", null).size() == 0

        when:
        dcAgentService.removeAgentAbsence(new RemoveAgentAbsenceCommand(id: absenceId))

        then:
        dcAgentService.getAgentPriorities(date("2001-01-02"), "EarlyCollection", null).size() == 1
        dcAgentService.getAgentPriorities(date("2001-02-10"), "EarlyCollection", null).size() == 0
    }
}
