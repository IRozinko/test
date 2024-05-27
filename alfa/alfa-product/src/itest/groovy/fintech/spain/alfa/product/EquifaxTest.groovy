package fintech.spain.alfa.product

import fintech.spain.alfa.product.settings.AlfaSettings
import fintech.spain.equifax.mock.MockedEquifaxResponse
import fintech.spain.alfa.product.cms.CmsSetup

class EquifaxTest extends AbstractAlfaTest {

    AlfaSettings.EquifaxRuleSettings settings

    def setup() {
        settings = settingsService.getJson(AlfaSettings.LENDING_RULES_EQUIFAX, AlfaSettings.EquifaxRuleSettings.class)
        settings.newClientCheck.maxTotalUnpaidBalance = 100.00
        settings.newClientCheck.maxNumberOfCreditors = 1
        settings.newClientCheck.excludeUnpaidBalanceOfTelco = true
        settings.newClientCheck.maxDelincuencyDays = 109
        settings.newClientCheck.maxNumberOfDaysOfWorstSituation = 100
        saveJsonSettings(AlfaSettings.LENDING_RULES_EQUIFAX, settings)
    }

    def "more than max unpaid balance"() {
        given:
        mockEquifaxProvider.responseSupplier = MockedEquifaxResponse.DEBT_AMOUNT

        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()

        then:
        assert workflow.toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_EQUIFAX_DEBT_AMOUNT
        assert workflow.toClient().emailCount(CmsSetup.LOAN_REJECTED_NOTIFICATION) == 1
    }

    def "telco unpaid balance is excluded"() {
        given:
        mockEquifaxProvider.responseSupplier = MockedEquifaxResponse.DEBT_AMOUNT_TELCO

        expect:
        assert fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .isCompleted()

        when:
        settings.newClientCheck.excludeUnpaidBalanceOfTelco = false
        saveJsonSettings(AlfaSettings.LENDING_RULES_EQUIFAX, settings)

        then:
        assert fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .toApplication().isRejected()
    }

    def "more than max number of creditors"() {
        given:
        mockEquifaxProvider.responseSupplier = MockedEquifaxResponse.DEBT_COUNT

        expect:
        assert fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_EQUIFAX_DEBT_COUNT
    }

    def "reject by delincuency days"() {
        given:
        mockEquifaxProvider.responseSupplier = MockedEquifaxResponse.DELINCUENCY_DAYS

        expect:
        assert fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_EQUIFAX_DELINCUENCY_DAYS
    }

    def "reject by worst situation days"() {
        given:
        mockEquifaxProvider.responseSupplier = MockedEquifaxResponse.WORST_SITUATION_DAYS

        expect:
        assert fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
            .toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_EQUIFAX_NUMBER_OF_DAYS_OF_WORST_SITUATION
    }
}
