package fintech.spain.alfa.web.services.navigation.spi;

import fintech.spain.alfa.web.config.security.WebApiUser;
import fintech.workflow.Workflow;

import java.util.Optional;

public interface NavigationContext {

    Optional<WebApiUser> getUser();

    Optional<Workflow> getWorkflow();

    NavigationProvider getProvider();
}
