package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.workflow.ActivityStatus;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.AutoCompletePrecondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class IsActivityInStatus implements AutoCompletePrecondition {

    private final String name;

    private final ActivityStatus status;

    public IsActivityInStatus(String name, ActivityStatus status) {
        this.name = name;
        this.status = status;
    }

    @Override
    public boolean isTrueFor(ActivityContext context) {
        return context.getWorkflow().getActivities().stream()
            .filter(activity -> StringUtils.equals(activity.getName(), name))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(String.format("Unable to find activity [%s] in workflow [%s]", name, context.getWorkflow())))
            .getStatus() == status;
    }
}
