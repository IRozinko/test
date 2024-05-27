package fintech.spain.alfa.product

import fintech.lending.core.application.LoanApplicationStatusDetail
import fintech.spain.alfa.product.settings.AlfaSettings
import fintech.spain.crosscheck.impl.SpainCrosscheckResponse
import fintech.spain.alfa.product.cms.CmsSetup

class CrosscheckTest extends AbstractAlfaTest {

    AlfaSettings.CrosscheckRuleSettings settings

    def setup() {
        settings = settingsService.getJson(AlfaSettings.LENDING_RULES_CROSSCHECK, AlfaSettings.CrosscheckRuleSettings.class)
    }

    def "has active loan"() {
        given:
        mockSpainCrosscheckProvider.setResponse(new SpainCrosscheckResponse()
            .setError(false)
            .setResponseStatusCode(200)
            .setAttributes(new SpainCrosscheckResponse.Attributes()
            .setBlacklisted(false)
            .setFound(true)
            .setMaxDpd(0)
            .setOpenLoans(1)
            .setRepeatedClient(false)
        ))

        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()

        then:
        assert workflow.toApplication().getStatusDetail() == LoanApplicationStatusDetail.CANCELLED
        assert workflow.toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_CROSSCHECK_ACTIVE_LOAN
        assert workflow.toClient().emailCount(CmsSetup.LOAN_REJECTED_NOTIFICATION) == 1
    }

    def "blacklisted"() {
        given:
        mockSpainCrosscheckProvider.setResponse(new SpainCrosscheckResponse()
            .setError(false)
            .setResponseStatusCode(200)
            .setAttributes(new SpainCrosscheckResponse.Attributes()
            .setBlacklisted(true)
            .setFound(false)
            .setMaxDpd(0)
            .setOpenLoans(0)
            .setRepeatedClient(false)
        ))

        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()

        then:
        assert workflow.toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_CROSSCHECK_BLACKLISTED
    }

    def "max dpd"() {
        given:
        mockSpainCrosscheckProvider.setResponse(new SpainCrosscheckResponse()
            .setError(false)
            .setResponseStatusCode(200)
            .setAttributes(new SpainCrosscheckResponse.Attributes()
            .setBlacklisted(false)
            .setFound(true)
            .setMaxDpd((int) settings.getMaxDpd() + 1)
            .setOpenLoans(0)
            .setRepeatedClient(false)
        ))

        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()

        then:
        assert workflow.toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_CROSSCHECK_MAX_DPD
    }

    def "has active application"() {
        given:
        mockSpainCrosscheckProvider.setResponse(new SpainCrosscheckResponse()
            .setError(false)
            .setResponseStatusCode(200)
            .setAttributes(new SpainCrosscheckResponse.Attributes()
            .setBlacklisted(false)
            .setFound(true)
            .setMaxDpd(0)
            .setOpenLoans(0)
            .setRepeatedClient(false)
            .setActiveRequest(true)
        ))

        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()

        then:
        assert workflow.toApplication().getStatusDetail() == LoanApplicationStatusDetail.CANCELLED
        assert workflow.toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_CROSSCHECK_ACTIVE_APPLICATION
        assert workflow.toClient().emailCount(CmsSetup.LOAN_REJECTED_NOTIFICATION) == 1
    }
}
