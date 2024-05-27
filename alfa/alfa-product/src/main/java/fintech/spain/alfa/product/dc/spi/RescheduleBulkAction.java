package fintech.spain.alfa.product.dc.spi;

import fintech.JsonUtils;
import fintech.TimeMachine;
import fintech.dc.DcService;
import fintech.dc.model.Debt;
import fintech.dc.spi.BulkActionContext;
import fintech.dc.spi.BulkActionHandler;
import fintech.spain.dc.command.RescheduleCommand;
import fintech.spain.dc.model.ReschedulingPreview;
import fintech.spain.alfa.product.dc.DcFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RescheduleBulkAction implements BulkActionHandler {


    @Autowired
    private DcFacade dcFacade;
    @Autowired
    private DcService dcService;

    @Override
    public void handle(BulkActionContext context) {
        Debt debt = context.getDebt();
        String schedule = context.getRequiredParam("schedule", String.class);
        ReschedulingPreview preview = JsonUtils.readValue(schedule, ReschedulingPreview.class);
        dcFacade.reschedule(new RescheduleCommand()
            .setLoanId(debt.getLoanId())
            .setPreview(preview)
            .setWhen(TimeMachine.today())
        );
        dcService.triggerActions(debt.getId());
    }

}
