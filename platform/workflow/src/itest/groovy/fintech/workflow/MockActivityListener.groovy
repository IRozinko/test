package fintech.workflow

import fintech.workflow.spi.ActivityContext
import fintech.workflow.spi.ActivityListener
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
class MockActivityListener implements ActivityListener {

    static def executed = 0;

    @Override
    void handle(ActivityContext context) {
        executed++;
    }
}
