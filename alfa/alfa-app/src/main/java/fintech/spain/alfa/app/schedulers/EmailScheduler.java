package fintech.spain.alfa.app.schedulers;

import fintech.email.impl.EmailQueueConsumer;
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
public class EmailScheduler implements Job {

    @Autowired
    private EmailQueueConsumer consumer;

    @Override
    public void execute(JobExecutionContext context) {
        consumer.consumeNow();
    }

    @Bean(name = "emailSchedulerJob")
    public JobDetail job() {
        return JobBuilder.newJob().ofType(this.getClass())
            .storeDurably()
            .withIdentity("EmailScheduler")
            .build();
    }

    @Bean(name = "emailSchedulerTrigger")
    public SimpleTriggerFactoryBean trigger(@Qualifier("emailSchedulerJob") JobDetail jobDetail) {
        return QuartzConfig.createTrigger(jobDetail, 5000);
    }
}
