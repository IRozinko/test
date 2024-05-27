package fintech.dc

import fintech.ClasspathUtils
import fintech.JsonUtils
import fintech.dc.commands.AssignDebtCommand
import fintech.dc.commands.SaveAgentCommand
import fintech.dc.db.DcAgentRepository
import fintech.dc.model.DcSettings
import fintech.dc.spi.DebtBatchJobs
import org.springframework.beans.factory.annotation.Autowired

class BatchAssignmentTest extends BaseSpecification {

    @Autowired
    DcAgentRepository agentRepository

    @Autowired
    DebtBatchJobs debtBatchJobs

    @Autowired
    DcAgentService dcAgentService

    @Autowired
    DcSettingsService dcSettingsService

    def setup() {
        settings = JsonUtils.readValue(ClasspathUtils.resourceToString("test-dc-batch-settings.json"), DcSettings.class)
        dcSettingsService.saveSettings(settings, true)

        dcAgentService.saveAgent(new SaveAgentCommand(agent: "Agent A", portfolios: ["Collections"]))
        dcAgentService.saveAgent(new SaveAgentCommand(agent: "Agent B", portfolios: ["Collections"]))
    }


    def "assign debts by batch"() {
        when:
        def debt1 = dcService.postLoan(postCommand().setLoanId(1).setLoanNumber("1").setDpd(1).setTotalDue(100.00g))
        def debt2 = dcService.postLoan(postCommand().setLoanId(2).setLoanNumber("2").setDpd(2).setTotalDue(100.00g))
        def debt3 = dcService.postLoan(postCommand().setLoanId(3).setLoanNumber("3").setDpd(3).setTotalDue(100.00g))

        then: "no debts auto assigned yet"
        dcService.get(debt1).agent == null
        dcService.get(debt2).agent == null
        dcService.get(debt3).agent == null

        when:
        debtBatchJobs.assignDebtsByBatch()

        then:
        dcService.get(debt1).agent == "Agent A"
        dcService.get(debt2).agent == "Agent B"
        dcService.get(debt3).agent == "Agent A"
    }

    def "already assigned debts ignored in batch assignment"() {
        when:
        def debt1 = dcService.postLoan(postCommand().setLoanId(1).setLoanNumber("1").setDpd(1).setTotalDue(100.00g))
        def debt2 = dcService.postLoan(postCommand().setLoanId(2).setLoanNumber("2").setDpd(2).setTotalDue(100.00g))
        def debt3 = dcService.postLoan(postCommand().setLoanId(3).setLoanNumber("3").setDpd(3).setTotalDue(100.00g))
        dcService.assignDebt(new AssignDebtCommand(debtId: debt1, agent: "Agent A"))

        then:
        dcService.get(debt1).agent == "Agent A"
        dcService.get(debt2).agent == null
        dcService.get(debt3).agent == null

        when:
        debtBatchJobs.assignDebtsByBatch()

        then:
        dcService.get(debt1).agent == "Agent A"
        dcService.get(debt2).agent == "Agent A"
        dcService.get(debt3).agent == "Agent B"
    }

    def "debt amount influences prioritization"() {
        when:
        def debt1 = dcService.postLoan(postCommand().setLoanId(1).setLoanNumber("1").setDpd(1).setTotalDue(200.00g))
        def debt2 = dcService.postLoan(postCommand().setLoanId(2).setLoanNumber("2").setDpd(2).setTotalDue(100.00g))
        def debt3 = dcService.postLoan(postCommand().setLoanId(3).setLoanNumber("3").setDpd(3).setTotalDue(100.00g))

        and:
        debtBatchJobs.assignDebtsByBatch()

        then:
        dcService.get(debt1).agent == "Agent A"
        dcService.get(debt2).agent == "Agent B"
        dcService.get(debt3).agent == "Agent B"
    }

    def "Changing to Collections portfolio in batch mode ignored in case of RESCHEDULED status"() {
        given:
        def command = postCommand().setLoanId(3).setLoanNumber("3").setDpd(-3).setTotalDue(0.00g)
        def debt = dcService.postLoan(command)

        when:
        dcService.assignDebt(new AssignDebtCommand(debtId: debt, agent: "Agent A"))
        dcService.postLoan(command.setTotalDue(100.0g).setLoanStatusDetail("RESCHEDULED"))

        then:
        dcService.get(debt).agent == "Agent A"
    }

    def "Changing to Collections portfolio in batch mode not ignored in case of ACTIVE status"() {
        given:
        def command = postCommand().setLoanId(3).setLoanNumber("3").setDpd(-3).setTotalDue(0.00g)
        def debt = dcService.postLoan(command)

        when:
        dcService.assignDebt(new AssignDebtCommand(debtId: debt, agent: "Agent A"))
        dcService.postLoan(command.setTotalDue(100.0g).setLoanStatusDetail("ACTIVE"))

        then:
        dcService.get(debt).agent == null
    }
}
