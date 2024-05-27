package fintech.dc.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.DateUtils;
import fintech.dc.DcAgentService;
import fintech.dc.commands.AddAgentAbsenceCommand;
import fintech.dc.commands.RemoveAgentAbsenceCommand;
import fintech.dc.commands.SaveAgentCommand;
import fintech.dc.db.DcAgentAbsenceEntity;
import fintech.dc.db.DcAgentAbsenceRepository;
import fintech.dc.db.DcAgentEntity;
import fintech.dc.db.DcAgentRepository;
import fintech.dc.db.Entities;
import fintech.dc.model.AgentPriority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;
import static fintech.BigDecimalUtils.amount;
import static fintech.dc.db.Entities.debt;

@Slf4j
@Component
@Transactional
public class DcAgentServiceBean implements DcAgentService {

    private final DcAgentRepository agentRepository;
    private final DcAgentAbsenceRepository agentAbsenceRepository;
    private final JPAQueryFactory queryFactory;

    @Autowired
    public DcAgentServiceBean(DcAgentRepository agentRepository, DcAgentAbsenceRepository agentAbsenceRepository,
                              JPAQueryFactory queryFactory) {
        this.agentRepository = agentRepository;
        this.agentAbsenceRepository = agentAbsenceRepository;
        this.queryFactory = queryFactory;
    }

    @Override
    public Long saveAgent(SaveAgentCommand command) {
        log.info("Saving agent [{}]", command);
        DcAgentEntity entity = agentRepository.getOptional(Entities.agent.agent.eq(command.getAgent()))
            .orElseGet(DcAgentEntity::new);
        entity.setAgent(command.getAgent());
        entity.setDisabled(command.isDisabled());
        entity.getPortfolios().clear();
        entity.getPortfolios().addAll(command.getPortfolios());
        return agentRepository.saveAndFlush(entity).getId();
    }

    @Override
    public Long addAgentAbsence(AddAgentAbsenceCommand command) {
        log.info("Adding agent absence: [{}]", command);
        DcAgentEntity agent = agentRepository.getOptional(Entities.agent.agent.eq(command.getAgent()))
            .orElseThrow(() -> new IllegalArgumentException("Agent not found"));

        DcAgentAbsenceEntity absence = new DcAgentAbsenceEntity();
        absence.setAgent(agent);
        absence.setDateFrom(command.getDateFrom());
        absence.setDateTo(command.getDateTo());
        absence.setReason(command.getReason());
        return agentAbsenceRepository.saveAndFlush(absence).getId();
    }

    @Override
    public void removeAgentAbsence(RemoveAgentAbsenceCommand command) {
        log.info("Removing agent absence: [{}]", command);
        agentAbsenceRepository.delete(command.getId());
    }

    @Override
    public List<String> getActiveAgents(LocalDate when, String portfolioName) {
        return queryFactory.select(Entities.agent.agent).from(Entities.agent)
            .where(Entities.agent.portfolios.contains(portfolioName).and(Entities.agent.disabled.isFalse())
                .and(JPAExpressions.selectFrom(Entities.agentAbsence)
                    .where(Entities.agentAbsence.dateFrom.loe(when)
                        .and(Entities.agentAbsence.dateTo.goe(when)))
                    .notExists()))
            .fetch();
    }

    @Override
    public List<AgentPriority> getAgentPriorities(LocalDate when, String portfolioName, Long excludingDebtId) {
        // doesn't make sense to check debt queue weights before today
        // using LocalDate instead of TimeMachine to work properly when initializing debts via demo data
        LocalDate todayOrLater = DateUtils.max(when, LocalDate.now());

        Tuple total = total(portfolioName, todayOrLater, excludingDebtId);

        Long totalDebtCount = firstNonNull(total.get(debt.count()), 0L);
        BigDecimal totalAmountDue = firstNonNull(total.get(debt.totalDue.sum()), amount(0));

        List<Tuple> byAgent = byAgent(portfolioName, todayOrLater, excludingDebtId);

        List<String> activeAgents = getActiveAgents(when, portfolioName);
        return activeAgents.stream()
            .map(toAgentPriority(totalDebtCount, totalAmountDue, byAgent))
            .sorted(Comparator.comparingDouble(AgentPriority::getPriority))
            .collect(Collectors.toList());
    }

    private List<Tuple> byAgent(String portfolioName, LocalDate when, Long excludingDebtId) {
        JPAQuery<Tuple> where = queryFactory.select(debt.agent, debt.count(), debt.totalDue.sum())
            .from(debt)
            .where(debt.portfolio.eq(portfolioName).and(debt.agent.isNotNull()))
            .where(ExpressionUtils.or(debt.nextActionAt.isNull(), debt.nextActionAt.lt(when.plusDays(1).atStartOfDay())));
        if (excludingDebtId != null) {
            where.where(debt.id.ne(excludingDebtId));
        }
        return where
            .groupBy(debt.agent)
            .fetch();
    }

    private Tuple total(String portfolioName, LocalDate when, Long excludingDebtId) {
        JPAQuery<Tuple> where = queryFactory.select(debt.count(), debt.totalDue.sum())
            .from(debt)
            .where(debt.portfolio.eq(portfolioName).and(debt.agent.isNotNull()))
            .where(ExpressionUtils.or(debt.nextActionAt.isNull(), debt.nextActionAt.lt(when.plusDays(1).atStartOfDay())));
        if (excludingDebtId != null) {
            where.where(debt.id.ne(excludingDebtId));
        }
        return where
            .fetchOne();
    }

    private Function<String, AgentPriority> toAgentPriority(Long totalDebtCount, BigDecimal totalAmountDue, List<Tuple> byAgent) {
        return agent -> byAgent.stream()
            .filter(data -> agent.equals(data.get(debt.agent)))
            .findFirst()
            .map(data -> toAgentPriority(totalDebtCount, totalAmountDue, data))
            .orElseGet(() -> emptyPriority(agent));
    }

    private AgentPriority emptyPriority(String agent) {
        AgentPriority priority = new AgentPriority();
        priority.setAgent(agent);
        priority.setAmountDue(amount(0));
        priority.setDebtCount(0L);
        priority.setPriority(0.0d);
        return priority;
    }

    private AgentPriority toAgentPriority(Long totalDebtCount, BigDecimal totalAmountDue, Tuple agentData) {
        String agent = agentData.get(debt.agent);
        Long debtCount = firstNonNull(agentData.get(debt.count()), 0L);
        BigDecimal amountDue = firstNonNull(agentData.get(debt.totalDue.sum()), amount(0));

        double debtPriority = totalDebtCount == 0 ? 0.0d : debtCount * 1.0d / totalDebtCount;
        double amountPriority = totalAmountDue.doubleValue() == 0 ? 0.0d : amountDue.doubleValue() * 1.0d / totalAmountDue.doubleValue();
        double priority = debtPriority + amountPriority;

        AgentPriority queue = new AgentPriority();
        queue.setAgent(agent);
        queue.setDebtCount(debtCount);
        queue.setAmountDue(amountDue);
        queue.setPriority(priority);
        return queue;
    }
}
