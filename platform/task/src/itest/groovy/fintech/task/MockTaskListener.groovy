package fintech.task

import fintech.task.spi.TaskContext
import fintech.task.spi.TaskListener
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
class MockTaskListener implements TaskListener {

    def static executed = 0

    MockTaskListener(String dummyArg) {
        assert dummyArg
    }

    @Override
    void handle(TaskContext context) {
        executed++
    }
}
