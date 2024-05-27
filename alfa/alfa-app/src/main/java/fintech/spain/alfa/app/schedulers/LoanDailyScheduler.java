package fintech.spain.alfa.app.schedulers;

import com.google.common.base.Throwables;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.TimeMachine;
import fintech.lending.core.db.Entities;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.LoanStatus;
import fintech.quartz.QuartzConfig;
import fintech.spain.alfa.product.dc.StrategyIdentifier;
import fintech.spain.alfa.product.dc.StrategyRegistry;
import fintech.spain.alfa.product.lending.penalty.PenaltyService;
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

import java.util.List;

@Slf4j
@Component
@DisallowConcurrentExecution
public class LoanDailyScheduler implements Job {

    static final String NAME = "LoanDailyScheduler";

    @Autowired
    private LoanService loanService;

    @Autowired
    private PenaltyService penaltyService;

    @Autowired
    private JPAQueryFactory queryFactory;
    @Autowired
    private StrategyRegistry strategyRegistry;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Running loan daily scheduler");
        updateLoanDerivedValues();
        applyPenalties();
        applyFees();
    }
    private void applyFees() {
        List<Long> loanIds = queryFactory.select(Entities.loan.id)
            .from(Entities.loan)
            .where(
                Entities.loan.status.eq(LoanStatus.OPEN)
                    .and(Entities.loan.overdueDays.goe(0))
            )
            .orderBy(Entities.loan.id.asc())
            .fetch();
        log.info("Found {} open loans to generate fees", loanIds.size());
        int failed = 0;
        for (int i = 0; i < loanIds.size(); i++) {
            Long loanId = loanIds.get(i);
            try {
                Loan loan = loanService.getLoan(loanId);
                if (loan.getFeeStrategyId() != null) {
                    StrategyIdentifier strategyIdentifier = strategyRegistry.getStrategy(loan.getCompany());
                    strategyIdentifier.applyFee(loan);
                }
            } catch (Exception e) {
                failed++;
                log.error("Failed to generate fee for loan {}: {}", loanId, Throwables.getRootCause(e).getMessage());
            }
            if (i % 100 == 0) {
                log.info("Processed applying fee {} loans out of {}", i + 1, loanIds.size());
            }
        }
        log.info("Completed loan fees generation, processed {}, failed {}", loanIds.size(), failed);

    }

    private void applyPenalties() {
        List<Long> loanIds = queryFactory.select(Entities.loan.id)
            .from(Entities.loan)
            .where(
                Entities.loan.status.eq(LoanStatus.OPEN)
                    .and(Entities.loan.overdueDays.goe(0))
            )
            .orderBy(Entities.loan.id.asc())
            .fetch();
        log.info("Found {} open loans to generate penalty", loanIds.size());
        int failed = 0;
        for (int i = 0; i < loanIds.size(); i++) {
            Long loanId = loanIds.get(i);
            try {
                Loan loan = loanService.getLoan(loanId);
                if (loan.getPenaltyStrategyId() != null) {
                    StrategyIdentifier strategyIdentifier = strategyRegistry.getStrategy(loan.getCompany());
                    strategyIdentifier.applyPenalty(loan);
                }
            } catch (Exception e) {
                failed++;
                log.error("Failed to generate penalty for loan {}: {}", loanId, Throwables.getRootCause(e).getMessage());
            }
            if (i % 100 == 0) {
                log.info("Processed applying penalty {} loans out of {}", i + 1, loanIds.size());
            }
        }
        log.info("Completed loan penalty generation, processed {}, failed {}", loanIds.size(), failed);
    }

    private void updateLoanDerivedValues() {
        List<Long> loanIds = queryFactory.select(Entities.loan.id)
            .from(Entities.loan)
            .where(Entities.loan.status.eq(LoanStatus.OPEN))
            .orderBy(Entities.loan.id.asc())
            .fetch();

        log.info("Found {} open loans", loanIds.size());
        int failed = 0;
        for (int i = 0; i < loanIds.size(); i++) {
            Long loanId = loanIds.get(i);
            try {
                loanService.resolveLoanDerivedValues(loanId, TimeMachine.today());
            } catch (Exception e) {
                failed++;
                log.error("Failed to resolve derived values for loan {}: {}", loanId, Throwables.getRootCause(e).getMessage());
            }
            if (i % 100 == 0) {
                log.info("Processed {} loans out of {}", i + 1, loanIds.size());
            }
        }
        log.info("Completed loan derived values scheduler, processed {}, failed {}", loanIds.size(), failed);
    }

    @Bean(name = "loanDailySchedulerJob")
    public JobDetail job() {
        return JobBuilder.newJob().ofType(this.getClass())
            .storeDurably()
            .requestRecovery()
            .withIdentity(NAME)
            .build();
    }

    @Bean(name = "loanDailySchedulerTrigger")
    public CronTriggerFactoryBean trigger(@Qualifier("loanDailySchedulerJob") JobDetail jobDetail) {
        return QuartzConfig.createCronTrigger(jobDetail, "0 1 0 * * ?");
    }
}
