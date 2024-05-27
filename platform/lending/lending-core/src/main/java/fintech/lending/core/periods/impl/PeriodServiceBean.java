package fintech.lending.core.periods.impl;

import com.google.common.base.Throwables;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.lending.core.db.Entities;
import fintech.lending.core.loan.spi.LoanRegistry;
import fintech.lending.core.periods.PeriodService;
import fintech.lending.core.periods.PeriodStatus;
import fintech.lending.core.periods.PeriodStatusDetail;
import fintech.lending.core.periods.commands.ClosePeriodCommand;
import fintech.lending.core.periods.db.PeriodEntity;
import fintech.lending.core.periods.db.PeriodRepository;
import fintech.lending.core.periods.spi.PeriodClosingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;

import static fintech.lending.core.db.Entities.period;
import static fintech.lending.core.periods.PeriodStatusDetail.FAILED;
import static fintech.lending.core.periods.PeriodStatusDetail.NEW;

@Slf4j
@DependsOn({"flyway.lending"})
@Component
class PeriodServiceBean implements PeriodService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PeriodRepository periodRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private LoanRegistry loanRegistry;

    @Autowired
    private TransactionTemplate txTemplate;

    @Transactional(propagation = Propagation.NEVER)
    @Override
    public void closePeriod(ClosePeriodCommand command) {
        log.info("Closing period [{}] on date [{}]", command.getPeriodDate(), command.getCloseDate());
        PeriodStatusDetail statusDetail = getPeriodEntity(command.getPeriodDate()).getStatusDetail();
        Validate.isTrue(NEW == statusDetail || FAILED == statusDetail, "Can't close period with status [%s]", statusDetail);

        generateNextOpenPeriods(command.getPeriodDate());
        requireValid(command);
        startClosing(command);
        try {
            PeriodClosingStrategy.PeriodClosingResult result = loanRegistry.getPeriodClosingStrategy()
                .closePeriod(command.getPeriodDate());
            closingFinished(command.getPeriodDate(), result);
        } catch (Exception e) {
            closingFailed(command.getPeriodDate(), e);
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void failPeriod(LocalDate periodDate, String resultLog) {
        closingFailed(periodDate, resultLog);
    }

    @Override
    public boolean isClosedOrClosing(LocalDate periodDate) {
        PeriodEntity period = periodRepository.findOne(Entities.period.periodDate.eq(periodDate));
        return period.getStatus() == PeriodStatus.CLOSED || period.getStatusDetail() == PeriodStatusDetail.CLOSING;
    }

    @Override
    public boolean isOfStatus(LocalDate periodDate, PeriodStatus status) {
        return periodRepository.exists(period.periodDate.eq(periodDate).and(period.status.eq(status)));
    }

    @Override
    public boolean isOfStatusDetail(LocalDate periodDate, PeriodStatusDetail statusDetail) {
        return periodRepository.exists(period.periodDate.eq(periodDate).and(period.statusDetail.eq(statusDetail)));
    }

    @Override
    public LocalDate getCurrentPeriod() {
        LocalDate period = getCurrentNonClosedPeriod();
        Validate.notNull(period, "No open periods initialized");
        return period;
    }

    private LocalDate getCurrentNonClosedPeriod() {
        return queryFactory
            .select(Entities.period.periodDate.min())
            .from(Entities.period)
            .where(Entities.period.status.ne(PeriodStatus.CLOSED))
            .fetchOne();
    }

    private void requireValid(ClosePeriodCommand command) {
        Validate.isTrue(command.getCloseDate().isAfter(command.getPeriodDate()),
            "Can't close before period has ended");
    }

    private void startClosing(ClosePeriodCommand command) {
        txTemplate.execute((status) -> {
            PeriodEntity period = getPeriodEntity(command.getPeriodDate());
            period.setClosingStartedAt(TimeMachine.now());
            period.open(PeriodStatusDetail.CLOSING);
            period.setCloseDate(command.getCloseDate());
            return true;
        });
    }

    private PeriodEntity getPeriodEntity(LocalDate periodDate) {
        PeriodEntity period = periodRepository.findOne(Entities.period.periodDate.eq(periodDate));
        return Validate.notNull(period, "Period not found");
    }

    private Void closingFailed(LocalDate periodDate, Throwable e) {
        log.error("Failed to close period {}", periodDate, e);
        return closingFailed(periodDate, Throwables.getStackTraceAsString(e));
    }

    private Void closingFailed(LocalDate periodDate, String resultLog) {
        txTemplate.execute((status) -> {
            PeriodEntity period = periodRepository.findOne(Entities.period.periodDate.eq(periodDate));
            period.open(FAILED);
            period.setResultLog(resultLog);
            period.setClosingEndedAt(TimeMachine.now());
            return true;
        });
        return null;
    }

    private void closingFinished(LocalDate periodDate, PeriodClosingStrategy.PeriodClosingResult result) {
        log.info("Closed period {} with result {}", periodDate, result);
        txTemplate.execute((status) -> {
            PeriodEntity period = getPeriodEntity(periodDate);
            period.close(PeriodStatusDetail.FINISHED, TimeMachine.now(), result.getResultLog());
            return true;
        });
    }

    private void generateNextOpenPeriods(LocalDate periodFrom) {
        generateNextOpenPeriods(periodFrom, 5);
    }

    @Override
    public void generateNextOpenPeriods(LocalDate from, int count) {
        txTemplate.execute((status) -> {
            for (int i = 0; i < count; i++) {
                LocalDate periodDate = from.plusDays(i);
                if (!periodRepository.exists(Entities.period.periodDate.eq(periodDate))) {
                    PeriodEntity period = new PeriodEntity();
                    period.setPeriodDate(periodDate);
                    period.setStatus(PeriodStatus.OPEN);
                    periodRepository.save(period);
                }
            }
            return true;
        });
    }

    @Override
    public void setup(LocalDate when) {
        if (maxPeriodDate() == null) {
            log.info("Initializing periods");
            generateNextOpenPeriods(when, 10);
        }
    }

    private LocalDate maxPeriodDate() {
        return queryFactory
            .select(period.periodDate.max())
            .from(period)
            .fetchOne();
    }
}
