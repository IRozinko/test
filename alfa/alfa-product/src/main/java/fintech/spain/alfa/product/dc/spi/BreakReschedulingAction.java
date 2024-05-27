package fintech.spain.alfa.product.dc.spi;

import fintech.TimeMachine;
import fintech.dc.model.Debt;
import fintech.dc.spi.ActionContext;
import fintech.dc.spi.ActionHandler;
import fintech.spain.dc.command.BreakReschedulingCommand;
import fintech.spain.alfa.product.dc.DcFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BreakReschedulingAction implements ActionHandler {

    @Autowired
    private DcFacade dcFacade;

    @Override
    public void handle(ActionContext context) {
        Debt debt = context.getDebt();
        dcFacade.breakRescheduling(new BreakReschedulingCommand()
            .setLoanId(debt.getLoanId())
            .setWhen(TimeMachine.today())
        );
    }

}
