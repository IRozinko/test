package fintech.spain.alfa.app.schedulers;

import fintech.quartz.QuartzConfig;
import fintech.spain.alfa.product.lending.ReschedulingLoanConsumerBean;
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

@Component
@DisallowConcurrentExecution
public class LoanReschedulingScheduler implements Job {

    @Autowired
    private ReschedulingLoanConsumerBean reschedulingLoanConsumerBean;

    @Override
    public void execute(JobExecutionContext context) {
        reschedulingLoanConsumerBean.consume();
    }

    @Bean(name = "loanReschedulingSchedulerJob")
    public JobDetail job() {
        return JobBuilder.newJob().ofType(this.getClass())
            .storeDurably()
            .withIdentity("LoanReschedulingScheduler")
            .build();
    }

    @Bean(name = "loanReschedulingSchedulerTrigger")
    public CronTriggerFactoryBean trigger(@Qualifier("loanReschedulingSchedulerJob") JobDetail jobDetail) {
        return QuartzConfig.createCronTrigger(jobDetail, "0 0 8 ? * * *"); // every day at 8am
    }
}
