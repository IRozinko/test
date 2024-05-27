package fintech.bo.spain.alfa.task;

import fintech.bo.components.task.TaskRegistry;
import fintech.bo.spain.alfa.task.wf.dormants.LocApproveLoanOfferCallTask;
import fintech.bo.spain.alfa.task.wf.dormants.LocPhoneValidationCall;
import fintech.bo.spain.alfa.task.wf.dormants.LocPreOfferCallTask;
import fintech.bo.spain.alfa.task.wf.dormants.LocReminderTask;
import fintech.bo.task.InstantorHelpCallTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.bo.spain.alfa.task.wf.dormants.LocReminderTask.INSTANTOR_FORM_REMINDER_TYPE;
import static fintech.bo.spain.alfa.task.wf.dormants.LocReminderTask.INSTANTOR_REVIEW_REMINDER_TYPE;
import static fintech.bo.spain.alfa.task.wf.dormants.LocReminderTask.SET_PWD_REMINDER_TYPE;

@Component
public class TaskSetup {

    private final TaskRegistry taskRegistry;

    @Autowired
    public TaskSetup(TaskRegistry taskRegistry) {
        this.taskRegistry = taskRegistry;
    }

    public void init() {
        // for a future final refactor can be useful https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#aop-atconfigurable
        taskRegistry.registerTask("LoanOfferCall", LoanOfferCallTask::new);
        taskRegistry.registerTask("InstantorManualCheck", InstantorManualCheckTask::new);
        taskRegistry.registerTask("WealthinessCheck", WealthinessCheckTask::new);
        taskRegistry.registerTask("DocumentCheck", DocumentCheckTask::new);
        taskRegistry.registerTask("ApplicationFormCall", CallTask::new);
        taskRegistry.registerTask("UpsellOfferCall", UpsellOfferCall::new);
        taskRegistry.registerTask("InstantorHelpCall", InstantorHelpCallTask::new);
        taskRegistry.registerTask("ExtensionSaleCall", ExtensionSaleCallTask::new);
        taskRegistry.registerTask("IdDocumentManualTextExtraction", IdDocumentManualTextExtractionTask::new);
        taskRegistry.registerTask("IdDocumentManualValidation", IdDocumentManualValidationTask::new);
        taskRegistry.registerTask("DowJonesCheck", DowJonesCheckTask::new);
        taskRegistry.registerTask("ScoringManualVerification", ScoringManualVerificationTask::new);

        {
            // Dormant WF Tasks
            taskRegistry.registerTask(LocPreOfferCallTask.TYPE, LocPreOfferCallTask::new);
            taskRegistry.registerTask(LocPreOfferCallTask.TYPE_RECENT_INSTANTOR, LocPreOfferCallTask::new);
            taskRegistry.registerTask("LocApproveLoanOfferCallTask", LocApproveLoanOfferCallTask::new);
            taskRegistry.registerTask("LocPhoneValidationCall", LocPhoneValidationCall::new);


            taskRegistry.registerTask(SET_PWD_REMINDER_TYPE, LocReminderTask::new);
            taskRegistry.registerTask(INSTANTOR_REVIEW_REMINDER_TYPE, LocReminderTask::new);
            taskRegistry.registerTask(INSTANTOR_FORM_REMINDER_TYPE, LocReminderTask::new);
        }
    }
}
