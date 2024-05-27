package fintech.workflow

import fintech.workflow.spi.TriggerContext
import fintech.workflow.spi.TriggerHandler
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import java.time.Duration

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
class MockTriggerHandler implements TriggerHandler {

    static def executed = 0

    public MockTriggerHandler(Duration duration, boolean fromMidnight, String param) {
    }

    @Override
    void handle(TriggerContext context) {
        executed++
    }
}
