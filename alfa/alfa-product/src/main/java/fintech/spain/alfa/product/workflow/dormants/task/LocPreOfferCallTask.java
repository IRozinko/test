package fintech.spain.alfa.product.workflow.dormants.task;

import fintech.spain.alfa.product.workflow.common.CompleteActivity;
import fintech.spain.alfa.product.workflow.common.ExpireActivity;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.task.spi.TaskDefinition;
import fintech.task.spi.TaskDefinitionBuilder;

import static com.google.common.collect.ImmutableList.of;

public class LocPreOfferCallTask {

    public final static String TYPE = "LocPreOfferCall";
    public final static String TYPE_RECENT_INSTANTOR = "LocPreOfferCall_RecentInstantor";

    public static final String NO_ANSWER = "NoAnswer";
    public static final String UNREACHABLE = "Unreachable";
    public static final String CLIENT_INTERESTED = "ClientInterested";
    public static final String CLIENT_NOT_INTERESTED = "ClientNotInterested";
    public static final String EXPIRE = "Expire";

    public static TaskDefinition build(String type) {
        return new TaskDefinitionBuilder(type)
            .group("DormantsLoc")
            .description("Remind client about Loc Pre Offer opportunity")
            .resolution(NO_ANSWER).asPostpone().withPostponeHours(of(1L, 2L, 4L, 8L, 24L)).add()
            .resolution(UNREACHABLE).asPostpone().withPostponeHours(of(1L, 2L, 4L, 8L, 24L)).add()
            .resolution(CLIENT_NOT_INTERESTED).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
            .resolution(CLIENT_INTERESTED).onCompleted(CompleteActivity.class, Resolutions.ACCEPT).add()
            .resolution(EXPIRE).onCompleted(ExpireActivity.class).add()
            .defaultExpireResolution(EXPIRE)
            .priority(110)
            .priorityAfterPostpone(170)
            .build();
    }
}
