package fintech.spain.alfa.product.acceptance

import fintech.ClasspathUtils
import fintech.JsonUtils
import fintech.TimeMachine
import fintech.dc.DcAgentService
import fintech.dc.DcSettingsService
import fintech.dc.commands.SaveAgentCommand
import fintech.dc.model.DcSettings
import fintech.spain.alfa.product.AbstractAlfaTest

import org.springframework.beans.factory.annotation.Autowired

import static fintech.DateUtils.date

class AcceptancePortfolioTriggersTest extends AbstractAlfaTest {

    @Autowired
    DcAgentService dcAgentService

    @Autowired
    DcSettingsService dcSettingsService

    @Autowired
    DcTestCases dcTestCases

    def setup() {
        DcSettings settings = JsonUtils.readValue(ClasspathUtils.resourceToString("dc/test-dc-settings.json"), DcSettings.class)
        dcSettingsService.saveSettings(settings, true)
        dcAgentService.saveAgent(new SaveAgentCommand(agent: "test", portfolios: ["Collections"]))
    }

    def 'Disbursing loan does not trigger MoveToPaid portfolio'() {
        when:
        def worklflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()

        then:
        worklflow.toLoan().loan.totalOutstanding == 0.00
        worklflow.toLoan().debt.portfolio == 'Current'

        when:
        worklflow.toLoan().postToDc()

        then:
        worklflow.toLoan().loan.totalOutstanding == 0.00
        worklflow.toLoan().debt.portfolio == 'Current'

        when:
        worklflow.exportDisbursement()

        then:
        worklflow.toLoan().loan.totalOutstanding > 0.00
        worklflow.toLoan().debt.portfolio == 'Current'
    }

    def 'Auto assign debt to an agent when dpd is reached 61 from Collection portfolio '() {
        when:
        def issueDate = date("2021-05-23")
        TimeMachine.useFixedClockAt(issueDate)
        def loan = dcTestCases.loanInDpd(58).postToDc()

        then:
        loan.getLoan().totalOutstanding > 0.00
        loan.getDebt().portfolio == 'Collections'
        loan.getDebt().dpd == 58
        !loan.getDebt().agent

        when:
        TimeMachine.useFixedClockAt(issueDate.plusDays(2))
        loan.resolveDerivedValues()
        loan.triggerDcActions()

        then:
        loan.getLoan().totalOutstanding > 0.00
        loan.getDebt().portfolio == 'Collections'
        loan.getDebt().dpd == 60
        !loan.getDebt().agent

        when:
        TimeMachine.useFixedClockAt(issueDate.plusDays(3))
        loan.resolveDerivedValues()
        loan.triggerDcActions()

        then:
        loan.getLoan().totalOutstanding > 0.00
        loan.getDebt().portfolio == 'Collections'
        loan.getDebt().dpd == 61
        loan.getDebt().agent == 'test'

    }
}
