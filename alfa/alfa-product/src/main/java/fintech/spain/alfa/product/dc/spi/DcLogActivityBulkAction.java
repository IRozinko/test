package fintech.spain.alfa.product.dc.spi;

import fintech.activity.ActivityService;
import fintech.activity.commands.AddActivityCommand;
import fintech.dc.commands.LogDebtActionCommand;
import fintech.dc.spi.BulkActionContext;
import fintech.dc.spi.BulkActionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DcLogActivityBulkAction implements BulkActionHandler {

    @Autowired
    private ActivityService activityService;

    @Override
    public void handle(BulkActionContext context) {
        LogDebtActionCommand source = context.getCommand();

        AddActivityCommand command = new AddActivityCommand();
        command.setClientId(context.getDebt().getClientId());
        command.setAction(source.getActionName());
        command.setAgent(source.getAgent());
        command.setResolution(source.getResolution());
        command.setComments(source.getComments());
        command.setDebtId(context.getDebt().getId());
        command.setTopic("DebtCollection");
        command.setSource("DebtCollection");
        activityService.addActivity(command);
    }
}
