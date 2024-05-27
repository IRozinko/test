package fintech.quartz;

import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class QuartzConfig {

    @Autowired(required = false)
    private List<Trigger> triggers;

    @Autowired
    private Environment environment;

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        QuartzJobFactory jobFactory = new QuartzJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @DependsOn("flyway.quartz")
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory, DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(environment.acceptsProfiles("!itest"));
        factory.setStartupDelay(10);
        factory.setDataSource(dataSource);
        factory.setJobFactory(jobFactory);
        factory.setConfigLocation(new ClassPathResource("/custom-quartz.properties"));
        if ((triggers != null) && !triggers.isEmpty()) {
            factory.setTriggers(triggers.toArray(new Trigger[0]));
        }
        return factory;
    }

    public static SimpleTriggerFactoryBean createTrigger(JobDetail jobDetail, long pollFrequencyMs) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        // each trigger is defined in a different group, named as the job, so that it can be paused
        factoryBean.setGroup(jobDetail.getKey().getName());
        factoryBean.setStartDelay(0L);
        factoryBean.setRepeatInterval(pollFrequencyMs);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        // in case of misfire, ignore all missed triggers and continue :
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
        return factoryBean;
    }

    // Use this method for creating cron triggers instead of simple triggers:
    public static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        // each trigger is defined in a different group, named as the job, so that it can be paused
        factoryBean.setGroup(jobDetail.getKey().getName());
        factoryBean.setCronExpression(cronExpression);
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        return factoryBean;
    }
}
