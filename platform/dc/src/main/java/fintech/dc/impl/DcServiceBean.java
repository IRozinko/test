package fintech.dc.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.dc.DcAgentService;
import fintech.dc.DcService;
import fintech.dc.DcSettingsService;
import fintech.dc.commands.*;
import fintech.dc.db.DebtActionEntity;
import fintech.dc.db.DebtActionRepository;
import fintech.dc.db.DebtEntity;
import fintech.dc.db.DebtRepository;
import fintech.dc.db.Entities;
import fintech.dc.model.AgentPriority;
import fintech.dc.model.DcSettings;
import fintech.dc.model.Debt;
import fintech.dc.spi.BulkActionHandler;
import fintech.dc.spi.DcRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static fintech.dc.model.ActionStatus.COMPLETED;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.Validate.isTrue;

@Slf4j
@Transactional
@Component
public class DcServiceBean implements DcService {

    public static final String SYSTEM_AGENT = "SYSTEM";
    public static final String LOAN_STATUS_OPEN = "OPEN";
    private static final LocalTime WORKING_TIME_END = LocalTime.of(21, 0);

    private final DebtRepository debtRepository;
    private final DebtActionRepository actionRepository;
    private final DcRegistry registry;
    private final JPAQueryFactory queryFactory;
    private final DcSettingsService dcSettingsService;
    private final DcAgentService dcAgentService;

    @Autowired
    public DcServiceBean(DebtRepository debtRepository, DebtActionRepository actionRepository,
                         DcRegistry registry, JPAQueryFactory queryFactory,
                         DcSettingsService dcSettingsService, DcAgentService dcAgentService) {
        this.debtRepository = debtRepository;
        this.actionRepository = actionRepository;
        this.registry = registry;
        this.queryFactory = queryFactory;
        this.dcSettingsService = dcSettingsService;
        this.dcAgentService = dcAgentService;
    }

    @Override
    public Long postLoan(PostLoanCommand command) {
        DebtEntity entity = queryFactory
            .selectFrom(Entities.debt)
            .where(Entities.debt.loanId.eq(command.getLoanId()))
            .fetchOne();

        if (entity == null) {
            entity = createNewDebt(command);
        }
        entity = updateDebt(entity, command);
        if (command.getState() != null && command.getStatus() != null) {
            entity.setDebtState(command.getState());
            entity.setDebtStatus(command.getStatus());
        }

        if (command.isTriggerActionsImmediately()) {
            triggerActions(entity.getId());
        } else {
            scheduleActionTriggers(entity);
        }
        log.info("Posted loan to DC, loan id [{}], status detail [{}], total due [{}], trigger immediately: [{}]", command.getLoanId(), command.getLoanStatusDetail(), command.getTotalDue(), command.isTriggerActionsImmediately());
        return entity.getId();
    }

    @Override
    public Long logAction(LogDebtActionCommand command) {
        DebtEntity debt = debtRepository.getRequired(command.getDebtId());
        DebtActionEntity action = new DebtActionEntity();
        DcSettings settings = dcSettingsService.getSettings();

        copyDebtValuesBefore(action, debt);

        if (command.getStatus() != null) {
            debt.setStatus(command.getStatus());
        }

        debt.setSubStatus(command.getSubStatus());

        if (command.getNextAction() != null) {
            debt.setNextAction(command.getNextAction());
            Validate.notNull(command.getNextActionAt(), "No value for next action date");
            debt.setNextActionAt(command.getNextActionAt());
        }

        command.getBulkActions().forEach((k, v) -> {
            BulkActionHandler panelHandler = registry.getBulkActionHandler(k);
            BulkActionContextImpl context = new BulkActionContextImpl(k, command, debt.toValueObject(), settings, v.getParams());
            panelHandler.handle(context);
        });

        debt.setLastAction(command.getActionName());
        debt.setLastActionAt(TimeMachine.now());

        action.setActionName(command.getActionName());
        action.setActionStatus(COMPLETED);
        action.setAgent(command.getAgent());
        action.setComments(command.getComments());
        action.setResolution(command.getResolution());

        DcSettings.Portfolio portfolioSettings = settings.findPortfolio(debt.getPortfolio());
        if (portfolioSettings.hasStatusByName(debt.getStatus())) {
            debt.setPriority(portfolioSettings.statusByName(debt.getStatus()).getPriority());
        }

        copyDebtValuesAfter(action, debt);

        action = actionRepository.save(action);

        if (debt.isAutoAssignmentRequired()) {
            AutoAssignDebtCommand autoAssignDebtCommand = new AutoAssignDebtCommand();
            autoAssignDebtCommand.setDebtId(debt.getId());
            autoAssignDebtCommand.setTryToStickWithCurrentAgent(true);
            autoAssignDebt(autoAssignDebtCommand);
        }
        scheduleActionTriggers(debt);
        return action.getId();
    }

    @Override
    public void assignDebt(AssignDebtCommand command) {
        log.info("Assigning debt to agent [{}]", command);
        DebtEntity debt = debtRepository.getRequired(command.getDebtId());

        if (isSold(debt.toValueObject())) {
            log.info("Sold debt [{}] can't be reassigned", debt.getId());
            return;
        }

        if (isExternalized(debt.toValueObject())) {
            log.info("Externalized debt [{}] can't be reassigned", debt.getId());
            return;
        }

        if (command.getAgent().equals(debt.getAgent())) {
            log.info("Debt already assigned to this agent: [{}]", command);
            return;
        }

        DebtActionEntity action = new DebtActionEntity();
        copyDebtValuesBefore(action, debt);

        debt.setAgent(command.getAgent());
        debt.setAutoAssignmentRequired(false);
        debt.setBatchAssignmentRequired(false);
        action.setActionStatus(COMPLETED);
        action.setActionName("Assign");
        action.setAgent(SYSTEM_AGENT);
        action.setAssignedToAgent(command.getAgent());
        action.setComments(command.getComment());

        copyDebtValuesAfter(action, debt);
        actionRepository.save(action);
    }

    @Override
    public void unassignDebt(UnassignDebtCommand command) {
        log.info("Unassigning debt [{}]", command);
        DebtEntity debt = debtRepository.getRequired(command.getDebtId());

        DebtActionEntity action = new DebtActionEntity();
        copyDebtValuesBefore(action, debt);

        debt.setAgent(null);
        debt.setAutoAssignmentRequired(false);
        debt.setBatchAssignmentRequired(false);
        action.setActionStatus(COMPLETED);
        action.setActionName("Unassign");
        action.setComments(command.getComment());
        action.setAgent(SYSTEM_AGENT);

        copyDebtValuesAfter(action, debt);
        actionRepository.save(action);
    }

    @Override
    public Optional<String> autoAssignDebt(AutoAssignDebtCommand command) {
        DebtEntity debt = debtRepository.getRequired(command.getDebtId());

        if (isSold(debt.toValueObject())) {
            log.info("Sold debt [{}] can't be reassigned", debt.getId());
            return Optional.empty();
        }

        if (isExternalized(debt.toValueObject())) {
            log.info("Externalized debt [{}] can't be reassigned", debt.getId());
            return Optional.empty();
        }

        LocalDate when = debt.getNextActionAt() == null ? TimeMachine.today() : debt.getNextActionAt().toLocalDate();

        List<AgentPriority> agentPriorities =
            dcAgentService.getAgentPriorities(when, debt.getPortfolio(), debt.getId())
                .stream().filter(p -> StringUtils.isBlank(command.getExcludeAgent()) || !StringUtils.equalsIgnoreCase(command.getExcludeAgent(), p.getAgent()))
                .collect(Collectors.toList());
        if (!agentPriorities.isEmpty()) {
            AgentPriority first = agentPriorities.get(0);
            if (StringUtils.equalsIgnoreCase(first.getAgent(), debt.getAgent())) {
                debt.setAutoAssignmentRequired(false);
                debt.setBatchAssignmentRequired(false);
                return Optional.of(debt.getAgent());
            }
            if (command.isTryToStickWithCurrentAgent()) {
                // this prevents of 'shuffling' debt between agents
                // which is unwanted behaviour in case, for example, debt moves between portfolios
                // and same agent is also working on new portfolio
                boolean couldBeAssignedToCurrentAgent = agentPriorities.stream().anyMatch(p -> StringUtils.equalsIgnoreCase(p.getAgent(), debt.getAgent()));
                if (couldBeAssignedToCurrentAgent) {
                    debt.setAutoAssignmentRequired(false);
                    debt.setBatchAssignmentRequired(false);
                    return Optional.of(debt.getAgent());
                }
            }
            AssignDebtCommand assignDebtCommand = new AssignDebtCommand();
            assignDebtCommand.setAgent(first.getAgent());
            assignDebtCommand.setComment("Auto assigned");
            assignDebtCommand.setDebtId(command.getDebtId());
            assignDebt(assignDebtCommand);
            return Optional.of(first.getAgent());
        }
        if (!StringUtils.isBlank(debt.getAgent())) {
            log.info("Could not auto-assign debt [{}], no agents found", command.getDebtId());
            UnassignDebtCommand unassignDebtCommand = new UnassignDebtCommand();
            unassignDebtCommand.setDebtId(command.getDebtId());
            unassignDebtCommand.setComment("No agent to auto-assign");
            unassignDebt(unassignDebtCommand);
        }
        return Optional.empty();
    }

    @Override
    public Debt get(Long debtId) {
        return debtRepository.getRequired(debtId).toValueObject();
    }

    @Override
    public Optional<Debt> findByLoanId(Long loanId) {
        return findEntityByLoanId(loanId).map(DebtEntity::toValueObject);
    }

    @Override
    public void triggerActions(Long debtId) {
        DebtEntity debt = debtRepository.getRequired(debtId);
        DcSettings settings = dcSettingsService.getSettings();
        triggerActions(debtId, () -> runActions(debt, settings));
    }

    @Override
    public void triggerActionsOnVoidTransaction(Long debtId) {
        DebtEntity debt = debtRepository.getRequired(debtId);
        DcSettings settings = dcSettingsService.getSettings();
        Triggers triggerResolver = new Triggers(registry, settings);
        triggerActions(debtId, () -> runActions(debt, triggerResolver, triggerResolver.findTriggersOnVoidTransaction()));
    }

    private void triggerActions(Long debtId, Supplier<List<DebtActionEntity>> executedActions) {
        DebtEntity debt = debtRepository.getRequired(debtId);
        DcSettings settings = dcSettingsService.getSettings();

        DcSettings.AgingBucket agingBucket = settings.resolveAgingBucket(debt.getDpd());
        debt.setAgingBucket(agingBucket.getName());

        List<DebtActionEntity> actions = executedActions.get();
        actionRepository.save(actions);

        if (debt.getLoanStatus().equals(LOAN_STATUS_OPEN)) {
            debt.setExecuteAt(TimeMachine.now().plusHours(1).plusMinutes(RandomUtils.nextInt(0, 30)));
        } else {
            debt.setExecuteAt(LocalDateTime.of(TimeMachine.today().plusDays(1), WORKING_TIME_END).plusHours(RandomUtils.nextInt(0, 6)).plusMinutes(RandomUtils.nextInt(0, 30)));
        }
        debt.setLastExecutedAt(TimeMachine.now());
        debt.setLastExecutionResult("OK");

        if (debt.isAutoAssignmentRequired()) {
            AutoAssignDebtCommand autoAssignDebtCommand = new AutoAssignDebtCommand();
            autoAssignDebtCommand.setDebtId(debt.getId());
            autoAssignDebtCommand.setTryToStickWithCurrentAgent(true);
            autoAssignDebt(autoAssignDebtCommand);
        }
    }

    @Override
    public void changeCompany(ChangeCompanyCommand command) {
        isTrue(portfolioExists(command.getPortfolio()), format("No %s portfolio found", command.getPortfolio()));

        DebtEntity debtEntity = debtRepository.getRequired(command.getDebtId());
        DebtActionEntity action = new DebtActionEntity();
        copyDebtValuesBefore(action, debtEntity);

        debtEntity.setOwningCompany(command.getOwningCompany());
        debtEntity.setManagingCompany(command.getManagingCompany());
        debtEntity.setPortfolio(command.getPortfolio());
        debtEntity.setAutoAssignmentRequired(false);
        debtEntity.setBatchAssignmentRequired(false);

        action.setActionName("Debt " + command.getOperationType());
        action.setActionStatus(COMPLETED);
        copyDebtValuesAfter(action, debtEntity);
        actionRepository.saveAndFlush(action);
    }

    @Override
    public void edit(EditDebtCommand command) {
        isTrue(portfolioExists(command.getPortfolio()), format("No %s portfolio configured in system", command.getPortfolio()));

        DebtEntity debtEntity = debtRepository.getRequired(command.getDebtId());
        DebtActionEntity action = new DebtActionEntity();
        copyDebtValuesBefore(action, debtEntity);

        debtEntity.setOwningCompany(command.getOwningCompany());
        debtEntity.setManagingCompany(command.getManagingCompany());
        debtEntity.setPortfolio(command.getPortfolio());
        debtEntity.setNextAction(command.getNextAction());
        debtEntity.setNextActionAt(command.getNextActionAt());
        debtEntity.setStatus(command.getStatus());
        debtEntity.setSubStatus(command.getSubStatus());

        action.setActionName("Debt " + command.getOperationType());
        action.setActionStatus(COMPLETED);
        copyDebtValuesAfter(action, debtEntity);
        actionRepository.saveAndFlush(action);
    }

    @Override
    public void edit(ChangeDebtStateCommand command) {
        DebtEntity debtEntity = debtRepository.getRequired(command.getDebtId());
        debtEntity.setDebtState(command.getState());
        debtEntity.setDebtStatus(command.getStatus());
        debtEntity.setDebtSubStatus(command.getSubStatus());
        debtRepository.saveAndFlush(debtEntity);
    }

    @Override
    public boolean isSold(Debt debt) {
        DcSettings.Companies companies = dcSettingsService.getSettings().getCompanies();
        String owningCompany = debt.getOwningCompany();
        String managingCompany = debt.getManagingCompany();
        return nonNull(managingCompany) && !managingCompany.equals(companies.getDefaultManagingCompany()) && nonNull(owningCompany) && !owningCompany.equals(companies.getDefaultOwningCompany());
    }

    @Override
    public boolean isExternalized(Debt debt) {
        DcSettings.Companies companies = dcSettingsService.getSettings().getCompanies();
        String owningCompany = debt.getOwningCompany();
        String managingCompany = debt.getManagingCompany();
        return nonNull(managingCompany) && !managingCompany.equals(companies.getDefaultManagingCompany()) && nonNull(owningCompany) && owningCompany.equals(companies.getDefaultOwningCompany());
    }

    private void scheduleActionTriggers(DebtEntity entity) {
        entity.setExecuteAt(TimeMachine.now().plusSeconds(10));
    }

    private DebtEntity updateDebt(DebtEntity entity, PostLoanCommand command) {
        entity.setTotalDue(command.getTotalDue());
        entity.setInterestDue(command.getInterestDue());
        entity.setPrincipalDue(command.getPrincipalDue());
        entity.setPenaltyDue(command.getPenaltyDue());
        entity.setFeeDue(command.getFeeDue());
        entity.setTotalOutstanding(command.getTotalOutstanding());
        entity.setInterestOutstanding(command.getInterestOutstanding());
        entity.setPrincipalOutstanding(command.getPrincipalOutstanding());
        entity.setPenaltyOutstanding(command.getPenaltyOutstanding());
        entity.setFeeOutstanding(command.getFeeOutstanding());
        entity.setTotalPaid(command.getTotalPaid());
        entity.setInterestPaid(command.getInterestPaid());
        entity.setPrincipalPaid(command.getPrincipalPaid());
        entity.setPenaltyPaid(command.getPenaltyPaid());
        entity.setFeePaid(command.getFeePaid());
        entity.setDpd(command.getDpd());
        entity.setMaxDpd(command.getMaxDpd());
        entity.setLoanStatus(command.getLoanStatus());
        entity.setLoanStatusDetail(command.getLoanStatusDetail());
        entity.setPaymentDueDate(command.getPaymentDueDate());
        entity.setMaturityDate(command.getMaturityDate());
        entity.setLastPaid(command.getLastPaid());
        entity.setLastPaymentDate(command.getLastPaymentDate());
        entity.setPeriodCount(command.getPeriodCount());
        return debtRepository.saveAndFlush(entity);
    }

    private DebtEntity createNewDebt(PostLoanCommand command) {
        DebtEntity entity = new DebtEntity();
        entity.setLoanId(command.getLoanId());
        entity.setClientId(command.getClientId());
        entity.setPortfolio("Current");
        entity.setStatus("NoStatus");
        entity.setPriority(100);
        entity.setAgingBucket("N/A");
        entity.setLoanNumber(command.getLoanNumber());
        DcSettings.Companies companies = dcSettingsService.getSettings().getCompanies();
        entity.setOwningCompany(companies.getDefaultOwningCompany());
        entity.setManagingCompany(companies.getDefaultManagingCompany());
        return entity;
    }

    private List<DebtActionEntity> runActions(DebtEntity debt, DcSettings settings) {
        Triggers triggerResolver = new Triggers(registry, settings);
        List<DcSettings.Trigger> triggers = triggerResolver.findTriggers(debt);
        return runActions(debt, triggerResolver, triggers);
    }

    private List<DebtActionEntity> runActions(DebtEntity debt, Triggers triggerResolver, List<DcSettings.Trigger> triggers) {
        String originalPortfolio = debt.getPortfolio();
        List<DebtActionEntity> actions = new ArrayList<>();
        for (DcSettings.Trigger trigger : triggers) {
            if (!StringUtils.equals(originalPortfolio, debt.getPortfolio())) {
                return actions;
            }
            if (triggerResolver.shouldTrigger(trigger, debt)) {
                DebtActionEntity entity = new DebtActionEntity();
                copyDebtValuesBefore(entity, debt);

                triggerResolver.trigger(trigger, debt);

                entity.setActionStatus(COMPLETED);
                entity.setActionName(trigger.getName());
                entity.setAgent(SYSTEM_AGENT);
                debt.setLastAction(trigger.getName());
                debt.setLastActionAt(TimeMachine.now());

                copyDebtValuesAfter(entity, debt);
                actions.add(entity);
            }
        }
        return actions;
    }

    private void copyDebtValuesAfter(DebtActionEntity entity, DebtEntity debt) {
        entity.setDebt(debt);
        entity.setClientId(debt.getClientId());
        entity.setLoanId(debt.getLoanId());
        entity.setDebtStatus(debt.getStatus());
        entity.setTotalDue(debt.getTotalDue());
        entity.setInterestDue(debt.getInterestDue());
        entity.setPrincipalDue(debt.getPrincipalDue());
        entity.setPenaltyDue(debt.getPenaltyDue());
        entity.setFeeDue(debt.getFeeDue());
        entity.setTotalOutstanding(debt.getTotalOutstanding());
        entity.setInterestOutstanding(debt.getInterestOutstanding());
        entity.setPrincipalOutstanding(debt.getPrincipalOutstanding());
        entity.setPenaltyOutstanding(debt.getPenaltyOutstanding());
        entity.setFeeOutstanding(debt.getFeeOutstanding());
        entity.setTotalPaid(debt.getTotalPaid());
        entity.setInterestPaid(debt.getInterestPaid());
        entity.setPrincipalPaid(debt.getPrincipalPaid());
        entity.setPenaltyPaid(debt.getPenaltyPaid());
        entity.setFeePaid(debt.getFeePaid());
        entity.setPortfolio(debt.getPortfolio());
        entity.setPriority(debt.getPriority());
        entity.setDpd(debt.getDpd());
        entity.setMaxDpd(debt.getMaxDpd());
        entity.setAgingBucket(debt.getAgingBucket());
        entity.setNextAction(debt.getNextAction());
        entity.setNextActionAt(debt.getNextActionAt());
        entity.setPromiseDueDate(debt.getPromiseDueDate());
        entity.setPromiseAmount(debt.getPromiseAmount());
        entity.setManagingCompanyAfter(debt.getManagingCompany());
        entity.setOwningCompanyAfter(debt.getOwningCompany());
    }

    private void copyDebtValuesBefore(DebtActionEntity entity, DebtEntity debt) {
        entity.setPortfolioBefore(debt.getPortfolio());
        entity.setDebtStatusBefore(debt.getStatus());
        entity.setManagingCompanyBefore(debt.getManagingCompany());
        entity.setOwningCompanyBefore(debt.getOwningCompany());
    }

    private Optional<DebtEntity> findEntityByLoanId(Long loanId) {
        return debtRepository.getOptional(Entities.debt.loanId.eq(loanId));
    }

    private boolean portfolioExists(String portfolio) {
        DcSettings.Portfolio maybePortfolio = dcSettingsService.getSettings().findPortfolio(portfolio);
        return maybePortfolio.getName().equals(portfolio);
    }
}
