package fintech.spain.alfa.product.workflow.upsell;

import fintech.lending.core.loan.events.LoanDisbursedEvent;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.lending.events.OfferApprovedEvent;
import fintech.spain.alfa.product.settings.AlfaSettings;
import fintech.spain.alfa.product.workflow.common.Attributes;
import fintech.spain.alfa.product.workflow.common.CancelApplication;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.spain.alfa.product.workflow.common.CreateTask;
import fintech.spain.alfa.product.workflow.common.RejectApplication;
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks;
import fintech.spain.alfa.product.workflow.undewrtiting.handlers.AttributeExists;
import fintech.spain.alfa.product.workflow.undewrtiting.handlers.CancelApplicationOnWorkflowTerminated;
import fintech.spain.alfa.product.workflow.undewrtiting.handlers.CancelOpenTasks;
import fintech.spain.alfa.product.workflow.undewrtiting.handlers.CancelPendingDisbursement;
import fintech.spain.alfa.product.workflow.upsell.handlers.UpsellActivity;
import fintech.spain.alfa.product.workflow.upsell.handlers.UpsellCreditLimitActivity;
import fintech.spain.alfa.product.workflow.upsell.handlers.UpsellGenerateAgreementActivity;
import fintech.spain.alfa.product.workflow.upsell.handlers.UpsellOfferEmailActivity;
import fintech.spain.alfa.product.workflow.upsell.handlers.UpsellPrepareOfferActivity;
import fintech.workflow.spi.WorkflowBuilder;
import fintech.workflow.spi.WorkflowDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

import static fintech.spain.alfa.product.workflow.common.NoopEventTrigger.event;
import static fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.APPROVE_OFFER;
import static fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.CREDIT_LIMIT;
import static fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.EXPORT_DISBURSEMENT;
import static fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.GENERATE_AGREEMENT;
import static fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.PREPARE_OFFER;
import static fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.UPSELL;
import static fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.UPSELL_OFFER_CALL;
import static fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.UPSELL_OFFER_EMAIL;
import static fintech.workflow.spi.WorkflowBuilder.completed;

@Component
public class UpsellWorkflow {

    public static final String WORKFLOW = "Upsell";

    @Autowired
    private SettingsService settingsService;

    public WorkflowDefinition build() {
        AlfaSettings.ApplicationOfUpsellSettings settings = settingsService.getJson(AlfaSettings.ACTIVITY_SETTINGS, AlfaSettings.ApplicationOfUpsellSettings.class);

        WorkflowBuilder workflow = new WorkflowBuilder(WORKFLOW);

        workflow.systemActivity(CREDIT_LIMIT)
            .resolutions(Resolutions.OK)
            .handler(UpsellCreditLimitActivity.class)
            .add();

        workflow.systemActivity(PREPARE_OFFER)
            .resolutions(Resolutions.OK)
            .waitForAll(WorkflowBuilder.completed(CREDIT_LIMIT, Resolutions.OK))
            .handler(UpsellPrepareOfferActivity.class)
            .add();

        workflow.agentActivity(UPSELL_OFFER_CALL)
            .resolutions(Resolutions.APPROVE, Resolutions.REJECT, Resolutions.CLIENT_REQUESTS_EMAIL, Resolutions.EXPIRE)
            .waitForAll(WorkflowBuilder.completed(PREPARE_OFFER, Resolutions.OK))
            .onStarted(CreateTask.class, UnderwritingTasks.UpsellOfferCall.TYPE, settings.getMaxDaysUpsellOfferCallTaskActive())
            .onResolution(Resolutions.EXPIRE, CancelApplication.class)
            .onResolution(Resolutions.REJECT, RejectApplication.class)
            .terminateWorkflowOnResolutions(Resolutions.REJECT)
            .expireWorkflowOnResolutions(Resolutions.EXPIRE)
            .onClosed(CancelOpenTasks.class)
            .add();

        workflow.systemActivity(UPSELL_OFFER_EMAIL)
            .resolutions(Resolutions.OK)
            .waitForAll(WorkflowBuilder.completed(UPSELL_OFFER_CALL, Resolutions.CLIENT_REQUESTS_EMAIL))
            .handler(UpsellOfferEmailActivity.class)
            .add();

        workflow.clientActivity(APPROVE_OFFER)
            .resolutions(Resolutions.APPROVE, Resolutions.EXPIRE)
            .waitForAll(WorkflowBuilder.completed(UPSELL_OFFER_EMAIL, Resolutions.OK))
            .completeOnTrigger(event(OfferApprovedEvent.class), Resolutions.APPROVE)
            .onResolution(Resolutions.EXPIRE, RejectApplication.class)
            .expireWorkflowOnResolutions(Resolutions.EXPIRE)
            .expires((int) Duration.ofDays(Optional.ofNullable(settings.getMaxDaysUpsellApproveOfferActivityActive()).orElse(0L)).getSeconds(), Resolutions.EXPIRE)
            .add();

        workflow.systemActivity(GENERATE_AGREEMENT)
            .resolutions(Resolutions.OK)
            .autoCompleteWithResolutionIf(Resolutions.OK, AttributeExists.class, Attributes.UPSELL_AGREEMENT_ATTACHMENT_ID)
            .waitForAny(WorkflowBuilder.completed(UPSELL_OFFER_CALL, Resolutions.APPROVE), WorkflowBuilder.completed(APPROVE_OFFER, Resolutions.APPROVE))
            .handler(UpsellGenerateAgreementActivity.class)
            .add();

        workflow.systemActivity(UPSELL)
            .resolutions(Resolutions.OK)
            .waitForAll(WorkflowBuilder.completed(GENERATE_AGREEMENT, Resolutions.OK))
            .handler(UpsellActivity.class)
            .add();

        workflow.agentActivity(EXPORT_DISBURSEMENT)
            .resolutions(Resolutions.OK)
            .waitForAll(WorkflowBuilder.completed(UPSELL, Resolutions.OK))
            .completeOnTrigger(event(LoanDisbursedEvent.class), Resolutions.OK)
            .completeWorkflowOnResolutions(Resolutions.OK)
            .add();

        workflow.addOnTerminatedListener(CancelApplicationOnWorkflowTerminated.class);
        workflow.addOnTerminatedListener(CancelPendingDisbursement.class);
        workflow.addOnExpiredListener(CancelApplicationOnWorkflowTerminated.class);
        workflow.addOnExpiredListener(CancelPendingDisbursement.class);

        return workflow.build();
    }

    public static class Activities {
        public static final String CREDIT_LIMIT = "CreditLimit";
        public static final String PREPARE_OFFER = "PrepareOffer";
        public static final String UPSELL_OFFER_CALL = "UpsellOfferCall";
        public static final String UPSELL_OFFER_EMAIL = "UpsellOfferEmail";
        public static final String APPROVE_OFFER = "ApproveOffer";
        public static final String GENERATE_AGREEMENT = "GenerateAgreement";
        public static final String UPSELL = "Upsell";
        public static final String EXPORT_DISBURSEMENT = "ExportDisbursement";
    }
}
