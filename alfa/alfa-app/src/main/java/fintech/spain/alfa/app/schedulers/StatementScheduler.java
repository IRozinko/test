package fintech.spain.alfa.app.schedulers;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.payments.StatementService;
import fintech.payments.model.StatementStatus;
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

import java.util.List;

import static fintech.payments.db.Entities.statement;

@Slf4j
@Component
@DisallowConcurrentExecution
public class StatementScheduler implements Job {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private StatementService statementService;

    @Override
    public void execute(JobExecutionContext context) {
        List<Long> newStatementIds = findNewStatements(100);

        if (!newStatementIds.isEmpty()) {
            log.info("Found [{}] statements to process", newStatementIds.size());
        }
        newStatementIds.forEach((id) -> statementService.processStatement(id));
    }

    List<Long> findNewStatements(int batchSize) {
        return queryFactory
            .select(statement.id)
            .from(statement)
            .where(statement.status.eq(StatementStatus.NEW))
            .limit(batchSize).fetch();
    }

    @Bean(name = "statementSchedulerJob")
    public JobDetail job() {
        return JobBuilder.newJob().ofType(this.getClass())
            .storeDurably()
            .withIdentity("StatementScheduler")
            .build();
    }

    @Bean(name = "statementSchedulerTrigger")
    public SimpleTriggerFactoryBean trigger(@Qualifier("statementSchedulerJob") JobDetail jobDetail) {
        return QuartzConfig.createTrigger(jobDetail, 5000);
    }

}
