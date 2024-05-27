package fintech.lending.core.application.commands;


import lombok.Data;

@Data
public class AttachWorkflowCommand {

    private Long applicationId;
    private Long workflowId;
    private String loanApplicationStatusDetail;

}
