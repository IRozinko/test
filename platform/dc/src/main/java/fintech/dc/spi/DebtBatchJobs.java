package fintech.dc.spi;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import fintech.TimeMachine;
import fintech.dc.DcAgentService;
import fintech.dc.DcService;
import fintech.dc.commands.AssignDebtCommand;
import fintech.dc.db.DebtEntity;
import fintech.dc.db.DebtRepository;
import fintech.dc.db.Entities;
import fintech.dc.impl.BatchAssignment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DebtBatchJobs {

    private final JPQLQueryFactory queryFactory;
    private final DcService dcService;
    private final DebtRepository debtRepository;
    private final BatchAssignment batchAssignment;
    private final TransactionTemplate txTemplate;
    private final DcAgentService dcAgentService;
    private final int batchSize;

    @Autowired
    public DebtBatchJobs(JPQLQueryFactory queryFactory, DcService dcService,
                         DebtRepository debtRepository, BatchAssignment batchAssignment,
                         TransactionTemplate txTemplate, DcAgentService dcAgentService,
                         @Value("${dc.batchSize:50}") int batchSize) {
        this.queryFactory = queryFactory;
        this.dcService = dcService;
        this.debtRepository = debtRepository;
        this.batchAssignment = batchAssignment;
        this.txTemplate = txTemplate;
        this.dcAgentService = dcAgentService;
        this.batchSize = batchSize;
    }

    @Transactional(propagation = Propagation.NEVER)
    public int triggerActions(LocalDateTime when) {
        return triggerActions(when, batchSize);
    }

    @Transactional(propagation = Propagation.NEVER)
    public int triggerActions(LocalDateTime when, int batchSize) {
        JPQLQuery<Long> query = queryFactory.select(Entities.debt.id)
            .from(Entities.debt)
            .where(Entities.debt.executeAt.loe(when))
            .limit(batchSize)
            .orderBy(Entities.debt.executeAt.asc(), Entities.debt.id.asc());
        Stopwatch stopwatch = Stopwatch.createStarted();
        int c = 0;
        try (CloseableIterator<Long> iterator = query.iterate()) {
            while (iterator.hasNext()) {
                Long debtId = iterator.next();
                try {
                    dcService.triggerActions(debtId);
                } catch (Exception e) {
                    log.error("Failed to trigger debt [" + debtId + "] actions", e);
                    logError(debtId, e);
                }
                c++;
            }
        }
        if (c > 0) {
            log.info("Triggered {} dc actions in {} ms", c, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }

        return c;
    }

    @Transactional(propagation = Propagation.NEVER)
    public void assignDebtsByBatch() {
        List<String> portfoliosRequiringBatchAssignment = queryFactory.selectDistinct(Entities.debt.portfolio)
            .from(Entities.debt)
            .where(Entities.debt.batchAssignmentRequired.isTrue())
            .fetch();
        for (String portfolio : portfoliosRequiringBatchAssignment) {
            List<String> agents = dcAgentService.getActiveAgents(TimeMachine.today(), portfolio);
            List<DebtEntity> batch = debtRepository.findAll(Entities.debt.batchAssignmentRequired.isTrue().and(Entities.debt.portfolio.eq(portfolio)), Entities.debt.id.asc());
            log.info("Running batch assignment for portfolio [{}], found [{}] agents and [{}] debts", portfolio, agents.size(), batch.size());
            List<BatchAssignment.AgentAssignment> assignments = batchAssignment.distributeBatch(agents, batch);
            for (BatchAssignment.AgentAssignment assignment : assignments) {
                for (Long debtId : assignment.getAssignedDebtIds()) {
                    try {
                        dcService.assignDebt(new AssignDebtCommand()
                            .setAgent(assignment.getAgent())
                            .setDebtId(debtId)
                            .setComment("Batch assignment")
                        );
                    } catch (Exception e) {
                        log.error("Failed to batch-assign debt", e);
                    }
                }
            }
        }
    }

    private void logError(Long debtId, Exception e) {
        txTemplate.execute((TransactionCallback<Object>) status -> {
            queryFactory.update(Entities.debt)
                .set(Entities.debt.executeAt, TimeMachine.now().plusHours(1))
                .set(Entities.debt.lastExecutionResult, Throwables.getRootCause(e).getMessage())
                .where(Entities.debt.id.eq(debtId))
                .execute();
            return 1;
        });
    }
}
