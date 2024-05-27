package fintech.workflow

import fintech.testing.integration.AbstractBaseSpecification
import fintech.workflow.db.ActivityListenerRepository
import fintech.workflow.spi.WorkflowRegistry
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    WorkflowRegistry workflowRegistry

    @Autowired
    WorkflowService workflowService

    @Autowired
    TriggerRegistry triggerRegistry

    @Autowired
    ActivityListenerRepository activityListenerRepository

    public def final MOCK_TRIGGER_HANDLER_NAME = "MOCK_TRIGGER_HANDLER_NAME"

    def setup() {
        testDatabase.cleanDb()
        MockWorkflowListener.executed = 0
        MockActivityHandler.executed = 0
        MockExceptionActivityHandler.executed = 0
        MockActivityListener.executed = 0
        MockTriggerHandler.executed = 0
        triggerRegistry.addTriggerHandler(MOCK_TRIGGER_HANDLER_NAME, MockTriggerHandler.class)
    }
}
