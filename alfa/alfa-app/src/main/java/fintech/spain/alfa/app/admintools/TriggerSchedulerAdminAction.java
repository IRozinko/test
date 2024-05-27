package fintech.spain.alfa.app.admintools;

import fintech.admintools.AdminAction;
import fintech.admintools.AdminActionContext;
import fintech.quartz.QuartzService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TriggerSchedulerAdminAction implements AdminAction {

    @Autowired
    private QuartzService quartzService;

    @Override
    public String getName() {
        return "TriggerScheduler";
    }

    @SneakyThrows
    @Override
    public void execute(AdminActionContext context) {
        quartzService.triggerJob(context.getParams());
    }

}
