package fintech.spain.alfa.web.services.navigation.impl;

import fintech.spain.alfa.web.config.security.WebApiUser;
import fintech.spain.alfa.product.workflow.personal.ChangeBankAccountWorkflow;
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows;
import fintech.spain.alfa.web.services.navigation.spi.NavigationContext;
import fintech.spain.alfa.web.services.navigation.spi.NavigationProvider;
import fintech.workflow.Workflow;
import fintech.workflow.WorkflowQuery;
import fintech.workflow.WorkflowService;
import fintech.workflow.WorkflowStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NavigationContextBean implements NavigationContext {

    private final ApplicationContext applicationContext;

    private final WorkflowService workflowService;

    public NavigationContextBean(
        ApplicationContext applicationContext,
        WorkflowService workflowService
    ) {
        this.applicationContext = applicationContext;
        this.workflowService = workflowService;
    }

    @Override
    public Optional<WebApiUser> getUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(authentication -> (WebApiUser) authentication.getPrincipal());
    }

    @Override
    public Optional<Workflow> getWorkflow() {
        return getUser()
            .map(WebApiUser::getClientId)
            .map(clientId -> WorkflowQuery.byClientId(clientId, WorkflowStatus.ACTIVE))
            .flatMap(query -> workflowService.findWorkflows(query).stream().findFirst());
    }

    @Override
    public NavigationProvider getProvider() {
        return getWorkflow()
            .map(this::mapWorkflowToNavigationProvider)
            .orElseGet(() -> applicationContext.getBean(DefaultNavigationProvider.class));
    }

    // inner implementation of choosing of navigation provider
    private NavigationProvider mapWorkflowToNavigationProvider(Workflow workflow) {
        Class<? extends NavigationProvider> aClass;
        switch (workflow.getName()) {
            case UnderwritingWorkflows.FIRST_LOAN:
            case UnderwritingWorkflows.FIRST_LOAN_AFFILIATE:
                aClass = UnderwritingWFsNavigationProvider.class;
                break;
            case ChangeBankAccountWorkflow.WORKFLOW:
                aClass = ChangeBankAccountWFNavigationProvider.class;
                break;
            default:
                aClass = DefaultNavigationProvider.class;
        }
        return applicationContext.getBean(aClass);
    }
}
