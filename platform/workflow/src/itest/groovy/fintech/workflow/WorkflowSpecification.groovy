package fintech.workflow

import fintech.workflow.spi.WorkflowBuilder
import fintech.workflow.spi.WorkflowDefinition
import org.apache.commons.lang3.RandomStringUtils

abstract class WorkflowSpecification extends BaseSpecification {
    protected static final long CLIENT_ID = 1L
    protected static final long LOAN_ID = 2L
    protected WorkflowDefinition definition
    protected Workflow workflow
    protected Long workflowId

    protected static builder() {
        return new WorkflowBuilder(randomName())
    }

    private static String randomName() {
        return RandomStringUtils.randomAlphabetic(10)
    }

    protected startWorkflow(WorkflowBuilder builder) {
        definition = builder.build()
        workflowRegistry.addDefinition({ definition }, -1)
        workflowId = workflowService.startWorkflow(StartWorkflowCommand.withLoan(definition.getWorkflowName(), CLIENT_ID, LOAN_ID, [:]))
        workflow = workflowService.getWorkflow(workflowId)
    }

    protected Workflow getWorkflow() {
        return workflowService.getWorkflow(workflowId)
    }
}
