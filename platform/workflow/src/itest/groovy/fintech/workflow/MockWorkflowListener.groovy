package fintech.workflow

import fintech.workflow.spi.WorkflowListener
import fintech.workflow.spi.WorkflowListenerContext
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component


@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
class MockWorkflowListener implements WorkflowListener {

    static def executed = 0;

    @Override
    void handle(WorkflowListenerContext context) {
        executed++;
    }
}
