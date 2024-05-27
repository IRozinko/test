package fintech.spain.alfa.product.workflow.undewrtiting;

import fintech.spain.alfa.product.workflow.common.CheckNewIdentificationDocumentSaved;
import fintech.spain.alfa.product.workflow.common.CompleteActivity;
import fintech.spain.alfa.product.workflow.common.ExpireActivity;
import fintech.spain.alfa.product.workflow.common.PostponeParentTask;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.spain.alfa.product.workflow.undewrtiting.handlers.CalculateApplicationCreditLimitFromDocuments;
import fintech.task.spi.TaskDefinition;
import fintech.task.spi.TaskDefinitionBuilder;
import fintech.task.spi.TaskRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.google.common.collect.ImmutableList.of;


@Component
public class UnderwritingTasks {

    @Autowired
    private TaskRegistry taskRegistry;

    public static class LoanOfferCall {

        public static final String TYPE = "LoanOfferCall";

        public static final String POSTPONE = "Postpone";
        public static final String UNREACHABLE = "Unreachable";
        public static final String CLIENT_REJECTED_OFFER = "ClientRejectedOffer";
        public static final String CLIENT_APPROVED_OFFER = "ClientApprovedOffer";
        public static final String EXPIRE = "Expire";


        static TaskDefinition build() {
            return new TaskDefinitionBuilder(TYPE)
                .group("Underwriting")
                .description("Remind client about loan offer")
                .resolution(POSTPONE).asPostpone().add()
                .resolution(UNREACHABLE).asPostpone().add()
                .resolution(CLIENT_REJECTED_OFFER).onCompleted(CompleteActivity.class, Resolutions.CANCEL).add()
                .resolution(CLIENT_APPROVED_OFFER).onCompleted(CompleteActivity.class, Resolutions.APPROVE).add()
                .resolution(EXPIRE).onCompleted(ExpireActivity.class).add()
                .defaultExpireResolution(EXPIRE)
                .priority(40)
                .priorityAfterPostpone(80)
                .build();
        }
    }

    public static class WealthinessCheck {

        public static final String TYPE = "WealthinessCheck";

        public static final String POSTPONE = "Postpone";
        public static final String REJECT = "Reject";
        public static final String CLIENT_CANCELLED_APPLICATION = "ClientCancelledApplication";
        public static final String CANCEL_APPLICATION = "CancelApplication";
        public static final String APPROVE = "Approve";
        public static final String EXPIRE = "Expire";

        static TaskDefinition build() {
            return new TaskDefinitionBuilder(TYPE)
                .group("Underwriting")
                .description("Manual check of client wealthiness based on Instantor transactions")
                .resolution(POSTPONE).asPostpone().add()
                .resolution(REJECT).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
                .resolution(CLIENT_CANCELLED_APPLICATION).onCompleted(CompleteActivity.class, Resolutions.CANCEL).add()
                .resolution(CANCEL_APPLICATION).onCompleted(CompleteActivity.class, Resolutions.CANCEL).add()
                .resolution(APPROVE).onCompleted(CompleteActivity.class, Resolutions.APPROVE).add()
                .resolution(EXPIRE).onCompleted(ExpireActivity.class).add()
                .defaultExpireResolution(EXPIRE)
                .priority(9000)
                .priorityAfterPostpone(9000)
                .build();
        }
    }

    public static class DocumentCheck {

        public static final String TYPE = "DocumentCheck";

        public static final String POSTPONE = "Postpone";
        public static final String UNREACHABLE = "Unreachable";
        public static final String REJECT = "Reject";
        public static final String APPROVE = "Approve";
        public static final String EXPIRE = "Expire";

        static TaskDefinition build() {
            return new TaskDefinitionBuilder(TYPE)
                .group("Underwriting")
                .description("Manual check of client uploaded documents")
                .resolution(POSTPONE).asPostpone().add()
                .resolution(UNREACHABLE).asPostpone().add()
                .resolution(REJECT).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
                .resolution(APPROVE).onCompleted(CalculateApplicationCreditLimitFromDocuments.class).onCompleted(CompleteActivity.class, Resolutions.APPROVE).add()
                .resolution(EXPIRE).onCompleted(ExpireActivity.class).add()
                .defaultExpireResolution(EXPIRE)
                .priority(9000)
                .priorityAfterPostpone(9000)
                .build();
        }
    }

    public static class ApplicationFormCall {

        public static final String TYPE = "ApplicationFormCall";

        public static final String POSTPONE = "Postpone";
        public static final String UNREACHABLE = "Unreachable";
        public static final String CLIENT_CANCELLED_APPLICATION = "ClientCancelledApplication";
        public static final String CANCEL_APPLICATION = "CancelApplication";
        public static final String EXPIRE = "Expire";

        static TaskDefinition build() {
            return new TaskDefinitionBuilder(TYPE)
                .group("Underwriting")
                .description("Remind client about application form")
                .resolution(POSTPONE).asPostpone().add()
                .resolution(UNREACHABLE).asPostpone().add()
                .resolution(CLIENT_CANCELLED_APPLICATION).onCompleted(CompleteActivity.class, Resolutions.CANCEL).add()
                .resolution(CANCEL_APPLICATION).onCompleted(CompleteActivity.class, Resolutions.CANCEL).add()
                .resolution(EXPIRE).onCompleted(ExpireActivity.class).add()
                .defaultExpireResolution(EXPIRE)
                .priority(400)
                .priorityAfterPostpone(500)
                .build();
        }
    }

    public static class UpsellOfferCall {

        public static final String TYPE = "UpsellOfferCall";

        public static final String POSTPONE = "Postpone";
        public static final String UNREACHABLE = "Unreachable";
        public static final String CLIENT_ACCEPTED = "ClientAccepted";
        public static final String CLIENT_REQUESTS_EMAIL = "ClientRequestsEmail";
        public static final String CLIENT_REFUSED = "ClientRefused";
        public static final String EXPIRE = "Expire";

        static TaskDefinition build() {
            return new TaskDefinitionBuilder(TYPE)
                .group("Underwriting")
                .description("Offer loan upsell")
                .resolution(POSTPONE).asPostpone().add()
                .resolution(UNREACHABLE).asPostpone().add()
                .resolution(CLIENT_ACCEPTED).onCompleted(CompleteActivity.class, Resolutions.APPROVE).add()
                .resolution(CLIENT_REQUESTS_EMAIL).onCompleted(CompleteActivity.class, Resolutions.CLIENT_REQUESTS_EMAIL).add()
                .resolution(CLIENT_REFUSED).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
                .resolution(EXPIRE).onCompleted(ExpireActivity.class).add()
                .defaultExpireResolution(EXPIRE)
                .priority(120)
                .priorityAfterPostpone(180)
                .build();
        }
    }

    public static class InstantorHelpCall {

        public static final String TYPE = "InstantorHelpCall";

        public static final String COMPLETE = "Complete";
        public static final String POSTPONE = "Postpone";
        public static final String UNREACHABLE = "Unreachable";
        public static final String CLIENT_CANCELLED_APPLICATION = "ClientCancelledApplication";
        public static final String CANCEL_APPLICATION = "CancelApplication";
        public static final String EXPIRE = "Expire";

        static TaskDefinition build() {
            return new TaskDefinitionBuilder(TYPE)
                .group("Underwriting")
                .description("Help client with instantor form")
                .resolution(COMPLETE).add()
                .resolution(POSTPONE).asPostpone().add()
                .resolution(UNREACHABLE).asPostpone().add()
                .resolution(CLIENT_CANCELLED_APPLICATION).onCompleted(CompleteActivity.class, Resolutions.CANCEL).add()
                .resolution(CANCEL_APPLICATION).onCompleted(CompleteActivity.class, Resolutions.CANCEL).add()
                .resolution(EXPIRE).add()
                .defaultExpireResolution(EXPIRE)
                .priority(20)
                .priorityAfterPostpone(160)
                .build();
        }
    }

    public static class IdDocumentManualTextExtraction {

        public static final String TYPE = "IdDocumentManualTextExtraction";

        public static final String COMPLETE = "Complete";
        public static final String POSTPONE = "Postpone";
        public static final String BAD_QUALITY = "BadDocumentQuality";
        public static final String SIGNS_OF_FRAUD = "SignsOfFraud";
        public static final String CUSTOMER_REFUSED_TO_PROVIDE = "CustomerRefusedToProvide";
        public static final String INAPPROPRIATE_DOCUMENT = "InappropriateIdDocument";
        public static final String REJECT_DOCUMENT = "RejectOfDocumentExpiration";
        public static final String EXPIRE = "Expire";

        static TaskDefinition build() {
            return new TaskDefinitionBuilder(TYPE)
                .group("Underwriting")
                .description("Manual text extraction from client attachments")
                .dependsOnTask(AlltManualTasks.TYPE, 2)
                .resolution(POSTPONE).onPostponed(PostponeParentTask.class, AlltManualTasks.POSTPONE).withPostponeHours(of(4L, 8L, 24L)).asPostpone().add()
                .resolution(COMPLETE).onCompleted(CheckNewIdentificationDocumentSaved.class).onCompleted(CompleteActivity.class, Resolutions.OK).add()
                .resolution(BAD_QUALITY).asPostpone().withDetails(of("Bad Image Quality", "Not full document provided", "Wrong type of document provided")).withPostponeHours(of(24L)).add()
                .resolution(SIGNS_OF_FRAUD).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
                .resolution(CUSTOMER_REFUSED_TO_PROVIDE).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
                .resolution(INAPPROPRIATE_DOCUMENT).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
                .resolution(REJECT_DOCUMENT).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
                .resolution(EXPIRE).onCompleted(ExpireActivity.class).add()
                .defaultExpireResolution(EXPIRE)
                .priority(20)
                .priorityAfterPostpone(160)
                .build();
        }
    }

    public static class IdDocumentManualValidation {

        public static final String TYPE = "IdDocumentManualValidation";

        public static final String COMPLETE = "Complete";
        public static final String POSTPONE = "Postpone";
        public static final String BAD_QUALITY = "BadDocumentQuality";
        public static final String SIGNS_OF_FRAUD = "SignsOfFraud";
        public static final String CUSTOMER_REFUSED_TO_PROVIDE = "CustomerRefusedToProvide";
        public static final String REJECT = "Rejected by Agent";
        public static final String REJECT_AGE = "Reject of age";
        public static final String REJECT_DOCUMENT = "Reject of document expiration";
        public static final String EXPIRE = "Expire";

        static TaskDefinition build() {
            return new TaskDefinitionBuilder(TYPE)
                .group("Underwriting")
                .description("Manual identification document validation")
                .dependsOnTask(AlltManualTasks.TYPE, 4)
                .resolution(POSTPONE).onPostponed(PostponeParentTask.class, AlltManualTasks.POSTPONE).asPostpone().withPostponeHours(of(4L, 8L, 24L)).add()
                .resolution(COMPLETE).onCompleted(CheckNewIdentificationDocumentSaved.class).onCompleted(CompleteActivity.class, Resolutions.OK).add()
                .resolution(BAD_QUALITY).asPostpone().withDetails(of("Bad Image Quality", "Not full document provided", "Wrong type of document provided")).withPostponeHours(of(24L)).add()
                .resolution(SIGNS_OF_FRAUD).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
                .resolution(CUSTOMER_REFUSED_TO_PROVIDE).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
                .resolution(REJECT).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
                .resolution(REJECT_DOCUMENT).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
                .resolution(REJECT_AGE).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
                .resolution(EXPIRE).onCompleted(ExpireActivity.class).add()
                .defaultExpireResolution(EXPIRE)
                .priority(20)
                .priorityAfterPostpone(160)
                .build();
        }
    }

    public static class DowJonesCheckTask {

        public static final String TYPE = "DowJonesCheck";

        public static final String POSTPONE = "Postpone";
        public static final String REJECT = "Reject";
        public static final String EXPIRE = "Expire";
        public static final String APPROVE = "Approve";

        static TaskDefinition build() {
            return new TaskDefinitionBuilder(TYPE)
                .group("Underwriting")
                .description("Manual check DowJones response")
                .dependsOnTask(AlltManualTasks.TYPE, 5)
                .resolution(POSTPONE).onPostponed(PostponeParentTask.class, AlltManualTasks.POSTPONE).asPostpone().add()
                .resolution(REJECT).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
                .resolution(APPROVE).onCompleted(CompleteActivity.class, Resolutions.APPROVE).add()
                .resolution(EXPIRE).onCompleted(ExpireActivity.class).add()
                .defaultExpireResolution(EXPIRE)
                .priority(50)
                .priorityAfterPostpone(130)
                .build();
        }
    }

    public static class ScoringManualVerificationTask {

        public static final String TYPE = "ScoringManualVerification";

        public static final String POSTPONE = "Postpone";
        public static final String REJECT = "Reject";
        public static final String EXPIRE = "Expire";
        public static final String APPROVE = "Approve";

        public static TaskDefinition build() {
            return new TaskDefinitionBuilder(TYPE)
                .group("Underwriting")
                .description("Manual verification of Scoring data")
                .dependsOnTask(AlltManualTasks.TYPE, 3)
                .resolution(POSTPONE).onPostponed(PostponeParentTask.class, AlltManualTasks.POSTPONE).asPostpone().add()
                .resolution(REJECT).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
                .resolution(APPROVE).onCompleted(CompleteActivity.class, Resolutions.APPROVE).add()
                .resolution(EXPIRE).onCompleted(ExpireActivity.class).add()
                .defaultExpireResolution(EXPIRE)
                .priority(50)
                .priorityAfterPostpone(130)
                .build();
        }
    }

    public static class AlltManualTasks {

        public static final String TYPE = "AllManualTasks";
        public static final String POSTPONE = "Postpone";
        public static final String EXPIRE = "Expire";

        static TaskDefinition build() {
            return new TaskDefinitionBuilder(TYPE)
                .group("Underwriting")
                .description("All manual checks tasks")
                .resolution(Resolutions.OK).add()
                .resolution(POSTPONE).asPostpone().add()
                .resolution(EXPIRE).add()
                .defaultExpireResolution(EXPIRE)
                .priority(20)
                .build();
        }
    }

    public static class InstantorManualCheckTask {

        public static final String TYPE = "InstantorManualCheck";

        public static final String POSTPONE = "Postpone";
        public static final String REQUEST_RETRY = "RequestRetry";
        public static final String REJECT = "Reject";
        public static final String EXPIRE = "Expire";
        public static final String APPROVE = "Approve";

        public static TaskDefinition build() {
            return new TaskDefinitionBuilder(TYPE)
                .group("Underwriting")
                .description("Manual check of Instantor data")
                .dependsOnTask(AlltManualTasks.TYPE, 1)
                .resolution(POSTPONE).onPostponed(PostponeParentTask.class, AlltManualTasks.POSTPONE).asPostpone().add()
                .resolution(REQUEST_RETRY).onCompleted(CompleteActivity.class, Resolutions.REQUEST_RETRY).add()
                .resolution(REJECT).onCompleted(CompleteActivity.class, Resolutions.REJECT).add()
                .resolution(APPROVE).onCompleted(CompleteActivity.class, Resolutions.APPROVE).add()
                .resolution(EXPIRE).onCompleted(ExpireActivity.class).add()
                .defaultExpireResolution(EXPIRE)
                .priority(50)
                .priorityAfterPostpone(130)
                .build();
        }
    }

    public void setUp() {
        taskRegistry.addDefinition(LoanOfferCall::build);
        taskRegistry.addDefinition(WealthinessCheck::build);
        taskRegistry.addDefinition(DocumentCheck::build);
        taskRegistry.addDefinition(ApplicationFormCall::build);
        taskRegistry.addDefinition(UpsellOfferCall::build);
        taskRegistry.addDefinition(InstantorHelpCall::build);
        taskRegistry.addDefinition(IdDocumentManualTextExtraction::build);
        taskRegistry.addDefinition(IdDocumentManualValidation::build);
        taskRegistry.addDefinition(DowJonesCheckTask::build);
        taskRegistry.addDefinition(ScoringManualVerificationTask::build);
        taskRegistry.addDefinition(AlltManualTasks::build);
        taskRegistry.addDefinition(InstantorManualCheckTask::build);
    }
}
