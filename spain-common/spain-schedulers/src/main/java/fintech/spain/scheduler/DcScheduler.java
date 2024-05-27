package fintech.spain.scheduler;

import fintech.TimeMachine;
import fintech.dc.spi.DebtBatchJobs;
import fintech.quartz.QuartzConfig;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class DcScheduler implements Job {

    @Autowired
    private DebtBatchJobs debtExecutor;

    @Override
    public void execute(JobExecutionContext context) {
        debtExecutor.triggerActions(TimeMachine.now());
    }

    @Bean(name = "dcSchedulerJob")
    public JobDetail job() {
        return JobBuilder.newJob().ofType(this.getClass())
            .storeDurably()
            .withIdentity("DcScheduler")
            .build();
    }

    @Bean(name = "dcSchedulerTrigger")
    public SimpleTriggerFactoryBean trigger(@Qualifier("dcSchedulerJob") JobDetail jobDetail) {
        return QuartzConfig.createTrigger(jobDetail, 1000);
    }
}
