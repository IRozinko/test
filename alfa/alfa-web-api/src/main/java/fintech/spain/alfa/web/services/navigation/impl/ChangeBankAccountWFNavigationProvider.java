package fintech.spain.alfa.web.services.navigation.impl;

import fintech.spain.alfa.web.services.navigation.UiState;
import fintech.spain.alfa.web.services.navigation.spi.NavigationContext;
import fintech.spain.alfa.web.services.navigation.spi.NavigationProvider;
import fintech.spain.alfa.product.workflow.personal.ChangeBankAccountWorkflow.Activities;
import fintech.workflow.Activity;
import fintech.workflow.Workflow;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class ChangeBankAccountWFNavigationProvider implements NavigationProvider {

    private final NavigationContext navigationContext;

    public ChangeBankAccountWFNavigationProvider(NavigationContext navigationContext) {
        this.navigationContext = navigationContext;
    }

    @Override
    public String getState() {
        return navigationContext.getWorkflow()
            .map(Workflow::getActivities)
            .flatMap(activities -> activities.stream()
                .filter(Activity::isActive).findFirst()
            )
            .map(Activity::getName)
            .map(this::mapState)
            .orElse(UiState.PROFILE);
    }

    private String mapState(String activity) {
        switch (activity) {
            case Activities.CHANGE_BANK_ACCOUNT_VERIFY:
                return UiState.CHANGE_BANK_ACCOUNT_VERIFY;
            case Activities.CHANGE_BANK_ACCOUNT_INSTANTOR_CALLBACK:
                return UiState.CHANGE_BANK_ACCOUNT_IN_PROGRESS;
            case Activities.CHANGE_BANK_ACCOUNT_CHOICE:
                return UiState.CHANGE_BANK_ACCOUNT_CHOICE;
            default:
                return UiState.PROFILE;
        }
    }

    @Override
    public Map<String, Object> getStateData() {
        return Collections.emptyMap();
    }
}
