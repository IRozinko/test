package fintech.spain.alfa.product.workflow.dormants.task;

import fintech.spain.alfa.product.workflow.common.CompleteActivity;
import fintech.spain.alfa.product.workflow.common.ExpireActivity;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.task.spi.TaskDefinition;
import fintech.task.spi.TaskDefinitionBuilder;

import static com.google.common.collect.ImmutableList.of;

public class LocPhoneValidationCallTask {

    public final static String TYPE = "LocPhoneValidationCall";

    public static final String POSTPONE = "Postpone";
    public static final String UNREACHABLE = "Unreachable";
    public static final String PHONE_IS_VALID = "PhoneIsValid";
    public static final String PHONE_IS_INVALID = "PhoneIsInvalid";
    public static final String EXPIRE = "Expire";

    public static TaskDefinition build() {
        return new TaskDefinitionBuilder(TYPE)
            .group("DormantsLoc")
            .description("Validate client phone number")
            .resolution(POSTPONE).asPostpone().withPostponeHours(of(1L, 2L, 4L, 8L, 24L)).add()
            .resolution(UNREACHABLE).asPostpone().withPostponeHours(of(1L, 2L, 4L, 8L, 24L)).add()
            .resolution(PHONE_IS_VALID).onCompleted(CompleteActivity.class, Resolutions.ACCEPT).add()
            .resolution(PHONE_IS_INVALID).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
            .resolution(EXPIRE).onCompleted(ExpireActivity.class).add()
            .defaultExpireResolution(EXPIRE)
            .priority(5)
            .priorityAfterPostpone(60)
            .build();
    }
}
