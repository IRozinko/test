package fintech.spain.alfa.product.workflow.dormants.task;

import fintech.spain.alfa.product.workflow.common.CompleteActivity;
import fintech.spain.alfa.product.workflow.common.ExpireActivity;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.task.spi.TaskDefinition;
import fintech.task.spi.TaskDefinitionBuilder;

import static com.google.common.collect.ImmutableList.of;

public class LocApproveLoanOfferCallTask {

    public final static String TYPE = "LocApproveLoanOfferCallTask";

    public static final String POSTPONE = "Postpone";
    public static final String UNREACHABLE = "Unreachable";
    public static final String CLIENT_REJECTED_OFFER = "ClientRejectedOffer";
    public static final String CLIENT_INTERESTED = "ClientInterested";
    public static final String EXPIRE = "Expire";

    public static TaskDefinition build() {
        return new TaskDefinitionBuilder(TYPE)
            .group("DormantsLoc")
            .description("Remind client about Loc Offer")
            .resolution(POSTPONE).asPostpone().withPostponeHours(of(1L, 2L, 4L, 8L, 24L)).add()
            .resolution(UNREACHABLE).asPostpone().withPostponeHours(of(1L, 2L, 4L, 8L, 24L)).add()
            .resolution(CLIENT_REJECTED_OFFER).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
            .resolution(CLIENT_INTERESTED).onCompleted(CompleteActivity.class, Resolutions.ACCEPT).add()
            .resolution(EXPIRE).onCompleted(ExpireActivity.class).add()
            .defaultExpireResolution(EXPIRE)
            .priority(30)
            .priorityAfterPostpone(90)
            .build();
    }
}
