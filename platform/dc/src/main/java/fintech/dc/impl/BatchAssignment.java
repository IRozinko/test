package fintech.dc.impl;

import com.google.common.collect.ImmutableList;
import fintech.dc.db.DebtEntity;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BatchAssignment {

    public List<AgentAssignment> distributeBatch(List<String> agents, List<DebtEntity> batch) {
        if (agents.isEmpty() || batch.isEmpty()) {
            return ImmutableList.of();
        }
        double totalDebtAmount = batch.stream().map(DebtEntity::getTotalDue).reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
        List<AgentAssignment> agentAssignments = agents.stream()
            .map(agent -> new AgentAssignment(agent, batch.size(), totalDebtAmount))
            .collect(Collectors.toList());

        for (DebtEntity debt : batch) {
            AgentAssignment agentToAssign = pickFirstAgent(agentAssignments);
            agentToAssign.assignDebt(debt);
        }
        return agentAssignments;
    }

    private AgentAssignment pickFirstAgent(List<AgentAssignment> agentAssignments) {
        Optional<AgentAssignment> first = agentAssignments.stream().min(Comparator.comparingDouble(AgentAssignment::getPriority));
        return first.orElseThrow(() -> new IllegalStateException("No agent found to assign"));
    }

    @Data
    public static class AgentAssignment {
        private String agent;
        private int totalDebtCount;
        private double totalDebtAmount;

        private int assignedDebtCount;
        private double assignedDebtAmount;
        private double priority;

        private List<Long> assignedDebtIds = new ArrayList<>();

        public AgentAssignment(String agent, int totalDebtCount, double totalDebtAmount) {
            this.agent = agent;
            this.totalDebtCount = totalDebtCount;
            this.totalDebtAmount = totalDebtAmount;
        }

        public void assignDebt(DebtEntity entity) {
            this.assignedDebtCount++;
            this.assignedDebtAmount += entity.getTotalDue().doubleValue();
            this.assignedDebtIds.add(entity.getId());

            double debtPriority = totalDebtCount == 0 ? 0.0d : assignedDebtCount * 1.0d / totalDebtCount;
            double amountPriority = totalDebtAmount == 0 ? 0.0d : assignedDebtAmount * 1.0d / totalDebtAmount;
            this.priority = debtPriority + amountPriority;
        }
    }

}
