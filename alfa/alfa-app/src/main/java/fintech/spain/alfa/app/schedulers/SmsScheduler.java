package fintech.spain.alfa.app.schedulers;

import fintech.TimeMachine;
import fintech.quartz.QuartzConfig;
import fintech.sms.impl.SmsQueueConsumer;
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
public class SmsScheduler implements Job {

    @Autowired
    private SmsQueueConsumer consumer;

    @Override
    public void execute(JobExecutionContext context) {
        consumer.consume(TimeMachine.now());
    }

    @Bean(name = "smsSchedulerJob")
    public JobDetail job() {
        return JobBuilder.newJob().ofType(this.getClass())
            .storeDurably()
            .withIdentity("SmsScheduler")
            .build();
    }

    @Bean(name = "smsSchedulerTrigger")
    public SimpleTriggerFactoryBean trigger(@Qualifier("smsSchedulerJob") JobDetail jobDetail) {
        return QuartzConfig.createTrigger(jobDetail, 5000);
    }

}
