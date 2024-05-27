package fintech.spain.alfa.product

import fintech.JsonUtils
import fintech.lending.core.application.LoanApplicationService
import fintech.payments.DisbursementService
import fintech.payments.settigs.PaymentsSettings
import fintech.settings.commands.UpdatePropertyCommand
import fintech.workflow.ActivityStatus
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import static fintech.payments.model.DisbursementStatusDetail.EXPORTED
import static fintech.payments.model.DisbursementStatusDetail.PENDING
import static fintech.payments.settigs.PaymentsSettingsService.PAYMENT_SETTINGS
import static fintech.spain.alfa.product.workflow.common.Attributes.DISBURSEMENT_ID
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.WAITING_EXPORT_DISBURSEMENT;

class ExportDisbursementActivityTest extends AbstractAlfaTest {

    @Autowired
    LoanApplicationService loanApplicationService

    @Autowired
    DisbursementService disbursementService

    @Unroll
    def "Export disbursemenet automatically during workflow"() {
        given:
        updatePaymentSettings(new PaymentsSettings(unnaxEnabled: unnax_enabled, autoExportEnabled: auto_export_enabled))
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()
        def wf = client.toLoanWorkflow()

        when:
        wf.runAfterActivity(WAITING_EXPORT_DISBURSEMENT)
        def disbursementId = (wf.getAttribute(DISBURSEMENT_ID)).map { Long.valueOf(it) }


        then:
        disbursementId.isPresent()
        disbursementService.getDisbursement(disbursementId.get()).statusDetail == disbursement_status_detail
        wf.getActivity(WAITING_EXPORT_DISBURSEMENT).status == activity_status

        where:
        unnax_enabled | auto_export_enabled | disbursement_status_detail | activity_status
        false         | false               | PENDING                    | ActivityStatus.ACTIVE
        false         | true                | PENDING                    | ActivityStatus.ACTIVE
        true          | false               | PENDING                    | ActivityStatus.ACTIVE
        true          | true                | EXPORTED                   | ActivityStatus.CANCELLED
    }

    def updatePaymentSettings(PaymentsSettings settings) {
        settingsService.update(new UpdatePropertyCommand(name: PAYMENT_SETTINGS,
            textValue: JsonUtils.writeValueAsString(settings)))
    }

}
