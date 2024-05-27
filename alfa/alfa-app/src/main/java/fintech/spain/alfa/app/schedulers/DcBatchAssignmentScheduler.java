package fintech.spain.alfa.app.schedulers;

import fintech.dc.spi.DebtBatchJobs;
import fintech.quartz.QuartzConfig;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@DisallowConcurrentExecution
public class DcBatchAssignmentScheduler implements Job {

    static final String NAME = "DcBatchAssignmentScheduler";

    @Autowired
    private DebtBatchJobs debtBatchJobs;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Running daily DC batch assignment scheduler");
        debtBatchJobs.assignDebtsByBatch();
    }

    @Bean(name = "dcBatchAssignmentSchedulerJob")
    public JobDetail job() {
        return JobBuilder.newJob().ofType(this.getClass())
            .storeDurably()
            .requestRecovery()
            .withIdentity(NAME)
            .build();
    }

    @Bean(name = "dcBatchAssignmentSchedulerTrigger")
    public CronTriggerFactoryBean trigger(@Qualifier("dcBatchAssignmentSchedulerJob") JobDetail jobDetail) {
        return QuartzConfig.createCronTrigger(jobDetail, "0 0 6 * * ?");
    }
}
