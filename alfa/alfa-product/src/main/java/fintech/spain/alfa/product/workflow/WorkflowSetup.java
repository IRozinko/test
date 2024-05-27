package fintech.spain.alfa.product.workflow;

import fintech.spain.alfa.product.workflow.common.SendNotificationDynamicOnTrigger;
import fintech.spain.alfa.product.workflow.common.SendNotificationOnTrigger;
import fintech.spain.alfa.product.workflow.dormants.task.LocApproveLoanOfferCallTask;
import fintech.spain.alfa.product.workflow.dormants.task.LocPhoneValidationCallTask;
import fintech.spain.alfa.product.workflow.dormants.task.LocPreOfferCallTask;
import fintech.spain.alfa.product.workflow.dormants.task.LocPrestoReminderCallTask;
import fintech.spain.alfa.product.workflow.personal.ChangeBankAccountWorkflow;
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks;
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows;
import fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow;
import fintech.task.spi.TaskRegistry;
import fintech.workflow.TriggerRegistry;
import fintech.workflow.spi.WorkflowRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static fintech.spain.alfa.product.workflow.dormants.task.LocPrestoReminderCallTask.INSTANTOR_FORM_REMINDER_TYPE;
import static fintech.spain.alfa.product.workflow.dormants.task.LocPrestoReminderCallTask.INSTANTOR_REVIEW_REMINDER_TYPE;
import static fintech.spain.alfa.product.workflow.dormants.task.LocPrestoReminderCallTask.SET_PWD_REMINDER_TYPE;

@Component
public class WorkflowSetup {

    @Autowired
    private WorkflowRegistry workflowRegistry;

//    @Autowired
//    private List<UnderwritingWorkflows> firstLoanWorkflows;

    @Autowired
    private ChangeBankAccountWorkflow changeBankAccountWorkflow;

    @Autowired
    private TriggerRegistry triggerRegistry;

    public void setUp() {

        workflowRegistry.clear();
//        firstLoanWorkflows.forEach(wf -> workflowRegistry.addDefinition(wf::firstLoanWorkflow, 30));
        workflowRegistry.addDefinition(() -> changeBankAccountWorkflow.build(), 30);


        // Common triggers
        triggerRegistry.addTriggerHandler(Triggers.SEND_NOTIFICATION_DYNAMIC_TRIGGER, SendNotificationDynamicOnTrigger.class);
        triggerRegistry.addTriggerHandler(Triggers.SEND_NOTIFICATION_TRIGGER, SendNotificationOnTrigger.class);


    }
}
