package fintech.spain.alfa.web.services.navigation.impl;

import fintech.TimeMachine;
import fintech.spain.alfa.web.services.navigation.UiState;
import fintech.spain.alfa.web.services.navigation.spi.NavigationContext;
import fintech.spain.alfa.web.services.navigation.spi.NavigationProvider;
import fintech.workflow.Activity;
import fintech.workflow.Actor;
import fintech.workflow.Workflow;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.*;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;

@Component
public class UnderwritingWFsNavigationProvider implements NavigationProvider {

    private final NavigationContext navigationContext;

    public UnderwritingWFsNavigationProvider(NavigationContext navigationContext) {
        this.navigationContext = navigationContext;
    }

    @Override
    public String getState() {
        Optional<Workflow> workflowMaybe = navigationContext.getWorkflow();
        if (workflowMaybe.isPresent()) {
            Workflow workflow = workflowMaybe.get();
            List<Activity> activeActivities = workflow.getActiveActivities();

            String clientActivity = uiState(activeActivities, Actor.CLIENT);
            if (clientActivity != null) {
                return clientActivity;
            }

            String agentActivity = uiState(activeActivities, Actor.AGENT);
            if (agentActivity != null) {
                return agentActivity;
            }

            String firstSystemWithUiState = activeActivities.stream()
                .filter(activity -> activity.getActor() == Actor.SYSTEM)
                .min(comparing(Activity::getUiState, Comparator.nullsLast(naturalOrder())))
                .map(Activity::getUiState)
                .orElse(null);
            if (firstSystemWithUiState != null) {
                return firstSystemWithUiState;
            }
            return getStateForLegacyWorkflowVersion(workflow);
        }

        return UiState.PROFILE;
    }

    //todo should be removed later when all WFs with version <19 are closed on prod/stage
    private String getStateForLegacyWorkflowVersion(Workflow workflow) {

        String state = workflow.getActiveActivities().stream().findFirst()
            .map(this::mapState)
            .orElse(UiState.PROFILE);

        // After the INSTANTOR_MANUAL_CHECK activity, we want the user waiting on the frontend maximum 30 seconds so we're overwriting the state
        // from UNDERWRITING_IN_PROGRESS to REGISTRATION_PROCESSING. After 30 seconds if the state is changed, then we send the new state
        // to the frontend (should be REGISTRATION_APPROVE_LOAN_OFFER); if the state is not changed, we send the real UNDERWRITING_IN_PROGRESS state
        if (state.equals(UiState.UNDERWRITING_IN_PROGRESS)) {
            Optional<LocalDateTime> startedWaitingAt = workflow.getActivities().stream()
                .filter(a -> a.getName().equals(INSTANTOR_MANUAL_CHECK)).findFirst()
                .map(Activity::getCompletedAt);
            if (startedWaitingAt.isPresent() && ChronoUnit.SECONDS.between(startedWaitingAt.get(), TimeMachine.now()) <= 30) {
                return UiState.REGISTRATION_PROCESSING;
            }
        }
        return state;
    }

    @Override
    public Map<String, Object> getStateData() {
        return navigationContext.getWorkflow()
            .map(wf -> wf.activity(INSTANTOR_CALLBACK).getAttempts())
            .map(attempts -> Collections.<String, Object>singletonMap("INSTANTOR_ATTEMPTS", attempts))
            .orElse(Collections.emptyMap());
    }

    private String mapState(Activity activity) {
        switch (activity.getName()) {
            case APPLICATION_FORM:
                return UiState.REGISTRATION_PERSONAL_DATA;
            case PHONE_VERIFICATION:
                return UiState.REGISTRATION_PHONE_VERIFICATION;
            case DOCUMENT_FORM:
                return UiState.REGISTRATION_INSTANTOR;
            case INSTANTOR_REVIEW:
                return UiState.REGISTRATION_INSTANTOR_REVIEW;
            case LOAN_OFFER_SMS:
            case APPROVE_LOAN_OFFER:
                return UiState.REGISTRATION_APPROVE_LOAN_OFFER;
            case DNI_DOC_UPLOAD:
                return UiState.REGISTRATION_DNI_DOC_UPLOAD;
            case ID_DOCUMENT_MANUAL_TEXT_EXTRACTION:
            case DOWJONES_MANUAL_CHECK:
                return UiState.REGISTRATION_DNI_DOC_VALIDATION;
            case IOVATION_BLACKBOX:
            case IOVATION_BLACKBOX_RUN_1:
            case IOVATION_BLACKBOX_RUN_2:
            case IOVATION_BLACKBOX_RUN_AFFILIATES:
                return UiState.REGISTRATION_IOVATION_BLACK_BOX;
            case COLLECT_BASIC_INFORMATION:
            case MANDATORY_LENDING_RULES:
            case BASIC_LENDING_RULES:
            case PRESTO_CROSSCHECK:
            case PRESTO_CROSSCHECK_RULES:
            case IOVATION:
            case IOVATION_CHECK_REPEATED:
            case IOVATION_RULES:
            case IOVATION_RUN_1:
            case IOVATION_CHECK_REPEATED_RUN_1:
            case IOVATION_RULES_RUN_1:
            case IOVATION_RUN_2:
            case IOVATION_CHECK_REPEATED_RUN_2:
            case IOVATION_RULES_RUN_2:
            case INSTANTOR_CALLBACK:
            case EQUIFAX_RUN_1:
            case EQUIFAX_RULES_RUN_1:
            case EXPERIAN_CAIS_RESUMEN_RUN_1:
            case EXPERIAN_CAIS_OPERACIONES_RUN_1:
            case EXPERIAN_RULES_RUN_1:
            case INGLOBALY:
            case INGLOBALY_RULES:
            case ISSUE_LOAN:
            case REVALIDATE_ID_DOC:
            case CHECK_VALID_ID_DOC:
            case INSTANTOR_RULES:
            case EXPORT_DISBURSEMENT:
                return UiState.REGISTRATION_PROCESSING;
            case WAITING_EXPORT_DISBURSEMENT:
                return UiState.PROFILE;
            default:
                return UiState.UNDERWRITING_IN_PROGRESS;
        }
    }

    private String uiState(List<Activity> activities, Actor actor) {
        return activities.stream()
            .filter(activity -> activity.getActor().equals(actor))
            .findFirst()
            .map(Activity::getUiState)
            .orElse(null);
    }
}
