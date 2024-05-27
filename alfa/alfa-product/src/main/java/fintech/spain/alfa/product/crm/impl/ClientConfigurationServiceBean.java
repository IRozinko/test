package fintech.spain.alfa.product.crm.impl;

import fintech.Validate;
import fintech.spain.alfa.product.crm.spi.ClientConfigurationService;
import fintech.spain.alfa.product.workflow.personal.ChangeBankAccountWorkflow;
import fintech.workflow.StartWorkflowCommand;
import fintech.workflow.Workflow;
import fintech.workflow.WorkflowQuery;
import fintech.workflow.WorkflowService;
import fintech.workflow.WorkflowStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientConfigurationServiceBean implements ClientConfigurationService {

    private final WorkflowService workflowService;

    public ClientConfigurationServiceBean(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @Override
    public void initiateChangingBankAccount(long clientId) {
        StartWorkflowCommand command = new StartWorkflowCommand();
        command.setClientId(clientId);
        command.setWorkflowName(ChangeBankAccountWorkflow.WORKFLOW);

        List<Workflow> activeWorkflows = workflowService.findWorkflows(WorkflowQuery.byClientId(clientId, ChangeBankAccountWorkflow.WORKFLOW, WorkflowStatus.ACTIVE));
        Validate.isTrue(activeWorkflows.isEmpty(), "There is another active workflow");

        workflowService.startWorkflow(command);

    }
}
