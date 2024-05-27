package fintech.workflow.spi;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class NoOpActivity implements ActivityHandler {

    private final String resolution;
    private final String comment;

    public NoOpActivity(String resolution, String comment) {
        this.resolution = resolution;
        this.comment = comment;
    }

    @Override
    public ActivityResult handle(ActivityContext context) {
        return ActivityResult.resolution(resolution, comment);
    }
}
