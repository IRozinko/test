package fintech.spain.alfa.app.admintools;

import fintech.TimeMachine;
import fintech.admintools.AdminAction;
import fintech.admintools.AdminActionContext;
import fintech.dc.spi.DebtBatchJobs;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static fintech.DateUtils.dateTime;

@Component
public class DcTriggerAdminAction implements AdminAction {

    @Autowired
    private DebtBatchJobs debtExecutor;

    @Override
    public String getName() {
        return "DcTrigger (pause scheduled job before)";
    }

    @Override
    public void execute(AdminActionContext context) {
        LocalDateTime when = TimeMachine.now();
        if (StringUtils.isNotBlank(context.getParams())) {
            // expecting date time in input in Spain timezone
            when = dateTime(context.getParams())
                .atZone(ZoneId.of("Europe/Madrid"))
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime();
        }
        int batchSize = 1000;
        int total = 0;
        int count;
        do {
            count = debtExecutor.triggerActions(when, batchSize);
            total += count;
            context.updateProgress("Triggered " + total + " dc actions");
        } while (count > 0);
        context.updateProgress("Completed, total " + total + " dc actions triggered");
    }
}
