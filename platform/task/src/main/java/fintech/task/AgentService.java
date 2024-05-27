package fintech.task;

import fintech.task.command.AddAgentCommand;
import fintech.task.command.DisableAgentCommand;
import fintech.task.model.Agent;

import java.util.Optional;

public interface AgentService {

    Long addAgent(AddAgentCommand command);

    void disableAgent(DisableAgentCommand command);

    Optional<Agent> findByEmail(String email);
}
