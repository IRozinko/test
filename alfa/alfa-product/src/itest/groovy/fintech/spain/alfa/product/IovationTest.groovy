package fintech.spain.alfa.product

import fintech.JsonUtils
import fintech.TimeMachine
import fintech.iovation.impl.MockIovationProvider
import fintech.settings.commands.UpdatePropertyCommand
import fintech.spain.alfa.product.cms.CmsSetup
import fintech.spain.alfa.product.settings.AlfaSettings
import fintech.workflow.WorkflowStatus

import static fintech.spain.alfa.product.settings.AlfaSettings.IOVATION_SETTINGS
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.*
import static fintech.workflow.ActivityStatus.COMPLETED

class IovationTest extends AbstractAlfaTest {

    def "iovation reject"() {
        given:
        def settings = settingsService.getJson(AlfaSettings.LENDING_RULES_IOVATION, AlfaSettings.IovationRuleSettings.class)
        mockIovationProvider.response = MockIovationProvider.generateResponse(settings.rejectOnResults[0])

        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()

        then:
        assert workflow.toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_IOVATION_RESULT
        assert workflow.toClient().emailCount(CmsSetup.LOAN_REJECTED_NOTIFICATION) == 1
    }

    def "iovation rules not rejected in case of repeated client and rule fails"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()

        when:
        def loan = client
            .toLoanWorkflow()
            .runAll()
            .exportDisbursement()
            .toLoan()
            .repayAll(TimeMachine.today())

        then:
        assert loan.isPaid()

        when:
        def settings = settingsService.getJson(AlfaSettings.LENDING_RULES_IOVATION, AlfaSettings.IovationRuleSettings.class)
        mockIovationProvider.response = MockIovationProvider.generateResponse(settings.rejectOnResults[0])
        settings.setAutoApproveRepeaters(true)
        saveJsonSettings(AlfaSettings.LENDING_RULES_IOVATION, settings)

        def workflow = client.submitApplicationAndStartFirstLoanWorkflow(150.00, 15, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()

        then:
        assert workflow.getActivityResolution(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.IOVATION_RULES_RUN_1) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        assert workflow.getActivityResolutionDetail(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.IOVATION_RULES_RUN_1) == "AutoCompleted"
    }

    def "IOVATION_RULES_RUN_1 should be skipped if IOVATION_RUN_1 was EXPIRED"() {
        given:
        def wf = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 15, TimeMachine.today())
            .toLoanWorkflow()

        when:
        wf.runBeforeActivity(IOVATION_RUN_1)
            .completeActivity(IOVATION_RUN_1, fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE)
            .runBeforeActivity(MANDATORY_LENDING_RULES)

        then:
        with(wf.getActivity(IOVATION_RULES_RUN_1)) {
            status == COMPLETED
            resolution == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        }
    }

    def "workflow should terminate if IOVATION_RUN_2 was expired"() {
        given:
        def wf = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 15, TimeMachine.today())
            .toLoanWorkflow()

        when:
        wf.runBeforeActivity(IOVATION_RUN_1)
            .completeActivity(IOVATION_RUN_1, fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE)
            .runBeforeActivity(IOVATION_RUN_2)
            .completeActivity(IOVATION_RUN_2, fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE)
            .runBeforeActivity(EXPERIAN_CAIS_RESUMEN_RUN_2)

        then:
        wf.getWorkflow().status == WorkflowStatus.TERMINATED
    }

    def "workflow should terminate if IOVATION_BLACKBOX_RUN was expired"() {
        given:
        def wf = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .submitApplicationAndStartFirstLoanWorkflow(100.00, 15, TimeMachine.today())
            .toLoanWorkflow()

        when:
        wf.runBeforeActivity(IOVATION_BLACKBOX_RUN_1)
            .completeActivity(IOVATION_BLACKBOX_RUN_1, fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE)
            .runBeforeActivity(IOVATION_BLACKBOX_RUN_2)
            .completeActivity(IOVATION_BLACKBOX_RUN_2, fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE)
            .runBeforeActivity(EXPERIAN_CAIS_RESUMEN_RUN_2)

        then:
        wf.getWorkflow().status == WorkflowStatus.TERMINATED
        wf.getActivity(IOVATION_RUN_1).resolution == fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE
        wf.getActivity(IOVATION_CHECK_REPEATED_RUN_1).resolution == fintech.spain.alfa.product.workflow.common.Resolutions.OK
        wf.getActivity(IOVATION_RULES_RUN_1).resolution == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP

        wf.getActivity(IOVATION_RUN_2).resolution == fintech.spain.alfa.product.workflow.common.Resolutions.EXPIRE
        wf.getActivity(IOVATION_CHECK_REPEATED_RUN_2).resolution == fintech.spain.alfa.product.workflow.common.Resolutions.OK
        wf.getActivity(IOVATION_RULES_RUN_2).resolution == fintech.spain.alfa.product.workflow.common.Resolutions.REJECT
    }

    def "Iovation activities skipped"() {
        given:
        skipIovation()
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        when:
        def workflow = client
            .signUp()
            .toLoanWorkflow()
            .runAfterActivity(MANDATORY_LENDING_RULES)

        then:
        workflow.getActivityStatus(IOVATION_BLACKBOX_RUN_1) == COMPLETED
        workflow.getActivityResolution(IOVATION_BLACKBOX_RUN_1) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        workflow.getActivityStatus(IOVATION_RUN_1) == COMPLETED
        workflow.getActivityResolution(IOVATION_RUN_1) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        workflow.getActivityStatus(IOVATION_CHECK_REPEATED_RUN_1) == COMPLETED
        workflow.getActivityResolution(IOVATION_CHECK_REPEATED_RUN_1) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        workflow.getActivityStatus(IOVATION_RULES_RUN_1) == COMPLETED
        workflow.getActivityResolution(IOVATION_RULES_RUN_1) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP

        when:
        workflow.runAfterActivity(REVALIDATE_ID_DOC)

        then:
        workflow.getActivityStatus(IOVATION_BLACKBOX_RUN_2) == COMPLETED
        workflow.getActivityResolution(IOVATION_BLACKBOX_RUN_2) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        workflow.getActivityStatus(IOVATION_RUN_2) == COMPLETED
        workflow.getActivityResolution(IOVATION_RUN_2) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        workflow.getActivityStatus(IOVATION_CHECK_REPEATED_RUN_2) == COMPLETED
        workflow.getActivityResolution(IOVATION_CHECK_REPEATED_RUN_2) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        workflow.getActivityStatus(IOVATION_RULES_RUN_2) == COMPLETED
        workflow.getActivityResolution(IOVATION_RULES_RUN_2) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
    }

    def "Iovation activities skipped for affiliates workflow"() {
        given:
        skipIovation()
        def client = fintech.spain.alfa.product.testing.TestFactory.newAffiliateClient().randomEmailAndName("test").signUp()

        when:
        def workflow = client.toLoanAffiliateWorkflow()
        workflow.runAfterActivity(PREPARE_OFFER)

        then:
        workflow.getActivityStatus(IOVATION_BLACKBOX_RUN_1) == COMPLETED
        workflow.getActivityResolution(IOVATION_BLACKBOX_RUN_1) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        workflow.getActivityStatus(IOVATION_RUN_1) == COMPLETED
        workflow.getActivityResolution(IOVATION_RUN_1) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        workflow.getActivityStatus(IOVATION_CHECK_REPEATED_RUN_1) == COMPLETED
        workflow.getActivityResolution(IOVATION_CHECK_REPEATED_RUN_1) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
        workflow.getActivityStatus(IOVATION_RULES_RUN_1) == COMPLETED
        workflow.getActivityResolution(IOVATION_RULES_RUN_1) == fintech.spain.alfa.product.workflow.common.Resolutions.SKIP
    }

    def skipIovation() {
        AlfaSettings.IovationSettings settings = settingsService.getJson(IOVATION_SETTINGS, AlfaSettings.IovationSettings.class);
        settings.setEnabled(false)
        settingsService.update(new UpdatePropertyCommand(name: IOVATION_SETTINGS, textValue: JsonUtils.writeValueAsString(settings)))
    }

}
