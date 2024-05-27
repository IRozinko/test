package fintech.spain.alfa.product.workflow.dormants.task;

import fintech.spain.alfa.product.workflow.common.CompleteActivity;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.task.spi.TaskDefinition;
import fintech.task.spi.TaskDefinitionBuilder;

import static com.google.common.collect.ImmutableList.of;


public class LocPrestoReminderCallTask {

    public final static String SET_PWD_REMINDER_TYPE = "LocPrestoSetPwdReminderCall";
    public static final String INSTANTOR_REVIEW_REMINDER_TYPE = "LocInstantorReviewReminderCall";
    public static final String INSTANTOR_FORM_REMINDER_TYPE = "LocInstantorFormReminderCall";

    public static final String POSTPONE = "Postpone";
    public static final String UNREACHABLE = "Unreachable";
    public static final String REJECT = "Reject";
    public static final String DONE = "Done";
    public static final String EXPIRE = "Expire";


    public static TaskDefinition build(String type, String description) {
        return new TaskDefinitionBuilder(type)
            .group("DormantsLoc")
            .description(description)
            .resolution(POSTPONE).asPostpone().withPostponeHours(of(1L, 2L, 4L, 8L, 24L)).add()
            .resolution(UNREACHABLE).asPostpone().withPostponeHours(of(1L, 2L, 4L, 8L, 24L)).add()
            .resolution(DONE).add()
            .resolution(EXPIRE).add()
            .resolution(REJECT).onCompleted(CompleteActivity.class, Resolutions.CANCEL).add()
            .defaultExpireResolution(EXPIRE)
            .priority(10)
            .priorityAfterPostpone(70)
            .build();
    }
}
