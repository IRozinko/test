package fintech.dc;

import fintech.dc.commands.AddAgentAbsenceCommand;
import fintech.dc.commands.RemoveAgentAbsenceCommand;
import fintech.dc.commands.SaveAgentCommand;
import fintech.dc.model.AgentPriority;

import java.time.LocalDate;
import java.util.List;

public interface DcAgentService {
    Long saveAgent(SaveAgentCommand command);

    Long addAgentAbsence(AddAgentAbsenceCommand command);

    void removeAgentAbsence(RemoveAgentAbsenceCommand command);

    List<String> getActiveAgents(LocalDate when, String portfolioName);

    List<AgentPriority> getAgentPriorities(LocalDate when, String portfolioName, Long excludingDebtId);
}
