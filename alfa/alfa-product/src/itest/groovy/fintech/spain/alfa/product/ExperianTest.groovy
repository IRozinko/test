package fintech.spain.alfa.product

import fintech.risk.checklist.CheckListConstants
import fintech.risk.checklist.commands.AddCheckListEntryCommand
import fintech.spain.alfa.product.cms.CmsSetup
import fintech.spain.alfa.product.settings.AlfaSettings

class ExperianTest extends AbstractAlfaTest {

    AlfaSettings.ExperianRuleSettings settings

    def setup() {
        settings = settingsService.getJson(AlfaSettings.LENDING_RULES_EXPERIAN, AlfaSettings.ExperianRuleSettings.class)
        settings.newClientCheck.maxUnpaidDebtAmount = 100.00
        settings.newClientCheck.maxUnpaidDebtCount = 1
        settings.newClientCheck.excludeDebtsWithProductoFinanciadoDescription = []
        settings.newClientCheck.rejectWhenSituacionPagoContains = ["Fallida"]
        settings.newClientCheck.excludeDebtsWithEndDate = false
        settings.newClientCheck.excludeDebtsOlderThanDays = 100000
        saveJsonSettings(AlfaSettings.LENDING_RULES_EXPERIAN, settings)
    }

    def "province code blacklisted"() {
        given:
        checkListService.addEntry(new AddCheckListEntryCommand(type: CheckListConstants.CHECKLIST_TYPE_PROVINCE_CODE, value1: "35"))
        mockExperianCaisProvider.resumenResponseResource = "experian-cais-resumen-province-code-35.xml"

        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()

        then:
        assert workflow.toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_PROVINCE_NOT_ALLOWED
        assert workflow.toClient().emailCount(CmsSetup.LOAN_REJECTED_NOTIFICATION) == 1
    }

    def "more than max debt amount"() {
        given:
        mockExperianCaisProvider.listOperacionesResponseSource = "experian-cais-operaciones-debt-amount.xml"

        expect:
        assert fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_EXPERIAN_DEBT_AMOUNT

        when: "exclude telco debt"
        settings.newClientCheck.excludeDebtsWithProductoFinanciadoDescription = ["Telecomunicaciones"]
        saveJsonSettings(AlfaSettings.LENDING_RULES_EXPERIAN, settings)

        then:
        assert fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .isCompleted()
    }

    def "more than max debt count"() {
        given:
        mockExperianCaisProvider.listOperacionesResponseSource = "experian-cais-operaciones-debt-count.xml"

        expect:
        assert fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_EXPERIAN_DEBT_COUNT

        when: "exclude telco debt"
        settings.newClientCheck.excludeDebtsWithProductoFinanciadoDescription = ["Telecomunicaciones"]
        saveJsonSettings(AlfaSettings.LENDING_RULES_EXPERIAN, settings)

        then:
        assert fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .isCompleted()
    }

    def "reject by payment situation"() {
        given:
        mockExperianCaisProvider.listOperacionesResponseSource = "experian-cais-operaciones-payment-situation.xml"

        expect:
        assert fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_EXPERIAN_PAYMENT_SITUATION
    }

    def "exclude finished debt"() {
        given:
        mockExperianCaisProvider.listOperacionesResponseSource = "experian-cais-operaciones-finished-debt.xml"

        expect:
        assert fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_EXPERIAN_DEBT_AMOUNT

        when:
        settings.newClientCheck.excludeDebtsWithEndDate = true
        saveJsonSettings(AlfaSettings.LENDING_RULES_EXPERIAN, settings)

        then:
        assert fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .isCompleted()
    }


    def "exclude old debt"() {
        given:
        mockExperianCaisProvider.listOperacionesResponseSource = "experian-cais-operaciones-old-debt.xml"

        expect:
        assert fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_EXPERIAN_DEBT_AMOUNT

        when:
        settings.newClientCheck.excludeDebtsOlderThanDays = 10
        saveJsonSettings(AlfaSettings.LENDING_RULES_EXPERIAN, settings)

        then:
        assert fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .isCompleted()
    }

    def "include debt with fake FechaFin"() {
        given:
        mockExperianCaisProvider.listOperacionesResponseSource = "experian-cais-operaciones-fake-fecha-fin.xml"

        when:
        settings.newClientCheck.excludeDebtsWithEndDate = true
        saveJsonSettings(AlfaSettings.LENDING_RULES_EXPERIAN, settings)

        then:
        def application = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .toApplication()
        assert application.rejected
        assert application.getCloseReason() == AlfaConstants.REJECT_REASON_EXPERIAN_DEBT_AMOUNT
    }
}
