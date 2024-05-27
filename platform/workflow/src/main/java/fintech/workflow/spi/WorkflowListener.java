package fintech.workflow.spi;

public interface WorkflowListener {

    void handle(WorkflowListenerContext context);

}
