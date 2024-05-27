package fintech.workflow;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class StartWorkflowCommand {

    private String workflowName;
    private Long clientId;
    private Long applicationId;
    private Long loanId;
    private Long parentWorkflowId;
    private Map<String, String> attributes = new HashMap<>();


    public static StartWorkflowCommand withLoanApplication(String workflowName, Long clientId, Long applicationId, Map<String, String> attributes) {
        StartWorkflowCommand command = new StartWorkflowCommand();
        command.setWorkflowName(workflowName);
        command.setApplicationId(applicationId);
        command.setClientId(clientId);
        command.setAttributes(attributes);
        return command;
    }

    public static StartWorkflowCommand withLoan(String workflowName, Long clientId, Long loanId, Map<String, String> attributes) {
        StartWorkflowCommand command = new StartWorkflowCommand();
        command.setWorkflowName(workflowName);
        command.setLoanId(loanId);
        command.setClientId(clientId);
        command.setAttributes(attributes);
        return command;
    }

    public static StartWorkflowCommand withClient(String workflowName, Long clientId, Map<String, String> attributes) {
        StartWorkflowCommand command = new StartWorkflowCommand();
        command.setWorkflowName(workflowName);
        command.setClientId(clientId);
        command.setAttributes(attributes);
        return command;
    }
}
