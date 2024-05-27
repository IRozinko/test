package fintech.dc.spi.handlers;

import fintech.dc.spi.ActionContext;
import fintech.dc.spi.ActionHandler;
import org.springframework.stereotype.Component;

@Component
public class NoopAction implements ActionHandler {

    private int executions = 0;

    private ActionContext lastContext;

    @Override
    public void handle(ActionContext context) {
        executions++;
    }

    public int getExecutions() {
        return executions;
    }

    public ActionContext getLastContext() {
        return lastContext;
    }
}
