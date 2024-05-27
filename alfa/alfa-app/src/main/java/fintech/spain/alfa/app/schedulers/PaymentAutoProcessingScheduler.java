package fintech.spain.alfa.app.schedulers;

import fintech.TimeMachine;
import fintech.payments.spi.BatchPaymentAutoProcessor;
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
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@DisallowConcurrentExecution
public class PaymentAutoProcessingScheduler implements Job {

    @Autowired
    private BatchPaymentAutoProcessor batchPaymentAutoProcessor;

    @Override
    public void execute(JobExecutionContext context) {
        batchPaymentAutoProcessor.autoProcessPending(500, TimeMachine.today());
    }

    @Bean(name = "paymentAutoProcessingSchedulerJob")
    public JobDetail job() {
        return JobBuilder.newJob().ofType(this.getClass())
            .storeDurably()
            .withIdentity("PaymentAutoProcessingScheduler")
            .build();
    }

    @Bean(name = "paymentAutoProcessingSchedulerTrigger")
    public SimpleTriggerFactoryBean trigger(@Qualifier("paymentAutoProcessingSchedulerJob") JobDetail jobDetail) {
        return QuartzConfig.createTrigger(jobDetail, 5000);
    }
}
