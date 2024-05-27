package fintech.spain.alfa.product.workflow.personal;

import fintech.instantor.events.InstantorResponseFailed;
import fintech.instantor.events.InstantorResponseProcessed;
import fintech.spain.alfa.product.workflow.common.NoopEventTrigger;
import fintech.spain.alfa.product.workflow.common.RemoveSpecialLinkActivity;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.spain.alfa.product.workflow.undewrtiting.handlers.InstantorRetryRequested;
import fintech.workflow.spi.WorkflowBuilder;
import fintech.workflow.spi.WorkflowDefinition;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static fintech.spain.alfa.product.workflow.personal.ChangeBankAccountWorkflow.Activities.CHANGE_BANK_ACCOUNT_CHOICE;
import static fintech.spain.alfa.product.workflow.personal.ChangeBankAccountWorkflow.Activities.CHANGE_BANK_ACCOUNT_INSTANTOR_CALLBACK;
import static fintech.spain.alfa.product.workflow.personal.ChangeBankAccountWorkflow.Activities.CHANGE_BANK_ACCOUNT_LINK;
import static fintech.spain.alfa.product.workflow.personal.ChangeBankAccountWorkflow.Activities.CHANGE_BANK_ACCOUNT_VERIFY;
import static fintech.workflow.spi.WorkflowBuilder.completed;

@Component
public class ChangeBankAccountWorkflow {
    public static final String WORKFLOW = "ChangeBankAccount";

    public static final class Activities {
        public static final String CHANGE_BANK_ACCOUNT_LINK = "ChangeBankAccountLink";
        public static final String CHANGE_BANK_ACCOUNT_VERIFY = "ChangeBankAccountVerify";
        public static final String CHANGE_BANK_ACCOUNT_CHOICE = "ChangeBankAccountChoice";
        public static final String CHANGE_BANK_ACCOUNT_INSTANTOR_CALLBACK = "ChangeBankAccountInstantorCallback";
    }

    public WorkflowDefinition build() {
        WorkflowBuilder workflow = new WorkflowBuilder(WORKFLOW);

        workflow.clientActivity(CHANGE_BANK_ACCOUNT_LINK).resolutions(Resolutions.OK, Resolutions.EXPIRE)
            // TODO don't generate a special link for this, just start the workflow
//            .onStarted(GenerateSpecialLinkActivity.class, CHANGE_BANK_ACCOUNT_LINK)
//            .completeOnTrigger(event(SpecialLinkActivated.class), OK) // fixme: what kind of SL ?
            .expires((int) Duration.ofHours(48).getSeconds(), Resolutions.EXPIRE)
            .expireWorkflowOnResolutions(Resolutions.EXPIRE)
            .add();

        workflow.clientActivity(CHANGE_BANK_ACCOUNT_VERIFY).resolutions(Resolutions.INSTANTOR_FORM_COMPLETED, Resolutions.EXPIRE)
            .waitForAll(WorkflowBuilder.completed(CHANGE_BANK_ACCOUNT_LINK, Resolutions.OK))
            .reactivationSupported(true)
            .expires((int) Duration.ofHours(48).getSeconds(), Resolutions.EXPIRE)
            .expireWorkflowOnResolutions(Resolutions.EXPIRE)
            .add();

        workflow.clientActivity(CHANGE_BANK_ACCOUNT_INSTANTOR_CALLBACK).resolutions(Resolutions.OK, Resolutions.FAIL, Resolutions.EXPIRE)
            .waitForAll(WorkflowBuilder.completed(CHANGE_BANK_ACCOUNT_VERIFY, Resolutions.INSTANTOR_FORM_COMPLETED))
            .reactivationSupported(true)
            .expires((int) Duration.ofHours(48).getSeconds(), Resolutions.EXPIRE)
            .completeOnTrigger(NoopEventTrigger.event(InstantorResponseProcessed.class), Resolutions.OK)
            .completeOnTrigger(NoopEventTrigger.event(InstantorResponseFailed.class), Resolutions.FAIL)
            .expireWorkflowOnResolutions(Resolutions.EXPIRE)
            .add();

        workflow.clientActivity(CHANGE_BANK_ACCOUNT_CHOICE).resolutions(Resolutions.OK, Resolutions.REQUEST_RETRY, Resolutions.EXPIRE)
            .waitForAll(WorkflowBuilder.completed(CHANGE_BANK_ACCOUNT_INSTANTOR_CALLBACK, Resolutions.OK))
            .reactivationSupported(true)
            .onResolution(Resolutions.REQUEST_RETRY, InstantorRetryRequested.class)
            .expires((int) Duration.ofHours(48).getSeconds(), Resolutions.EXPIRE)
            .expireWorkflowOnResolutions(Resolutions.EXPIRE)
            .completeWorkflowOnResolutions(Resolutions.OK)
            .add();

        workflow.addOnCompletedListener(RemoveSpecialLinkActivity.class, CHANGE_BANK_ACCOUNT_LINK);
        workflow.addOnTerminatedListener(RemoveSpecialLinkActivity.class, CHANGE_BANK_ACCOUNT_LINK);
        workflow.addOnExpiredListener(RemoveSpecialLinkActivity.class, CHANGE_BANK_ACCOUNT_LINK);
        return workflow.build();
    }
}
