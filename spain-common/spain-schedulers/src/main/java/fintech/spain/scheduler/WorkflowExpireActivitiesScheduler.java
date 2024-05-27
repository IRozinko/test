package fintech.spain.scheduler;

import fintech.TimeMachine;
import fintech.quartz.QuartzConfig;
import fintech.workflow.impl.WorkflowBackgroundJobs;
import lombok.SneakyThrows;
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
public class WorkflowExpireActivitiesScheduler implements Job {

    @Autowired
    private WorkflowBackgroundJobs consumer;

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext context) {
        consumer.expireActivities(TimeMachine.now());
    }

    @Bean(name = "workflowExpireActivitiesSchedulerJob")
    public JobDetail job() {
        return JobBuilder.newJob().ofType(this.getClass())
            .storeDurably()
            .withIdentity("WorkflowExpireActivitiesScheduler")
            .build();
    }

    @Bean(name = "workflowExpireActivitiesSchedulerTrigger")
    public SimpleTriggerFactoryBean trigger(@Qualifier("workflowExpireActivitiesSchedulerJob") JobDetail jobDetail) {
        return QuartzConfig.createTrigger(jobDetail, 1000);
    }
}
