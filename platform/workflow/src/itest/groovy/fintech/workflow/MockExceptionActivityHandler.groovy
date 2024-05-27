package fintech.workflow

import fintech.workflow.spi.ActivityContext
import fintech.workflow.spi.ActivityHandler
import fintech.workflow.spi.ActivityResult
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
class MockExceptionActivityHandler implements ActivityHandler {

    static def executed = 0;

    @Override
    ActivityResult handle(ActivityContext context) {
        executed++;
        throw new RuntimeException("Activity error")
    }
}
