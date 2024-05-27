package fintech.task.impl;

import fintech.Validate;
import fintech.task.command.AddAgentCommand;
import fintech.task.model.Agent;
import fintech.task.AgentService;
import fintech.task.command.DisableAgentCommand;
import fintech.task.db.AgentEntity;
import fintech.task.db.AgentRepository;
import fintech.task.db.Entities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class AgentServiceBean implements AgentService {

    @Autowired
    private AgentRepository agentRepository;

    @Override
    public Long addAgent(AddAgentCommand command) {
        Validate.notBlank(command.getEmail(), "Blank agent email");
        Optional<Agent> existing = findByEmail(command.getEmail());
        AgentEntity agent;
        if (existing.isPresent()) {
            log.info("Updating existing agent: [{}]", command);
            agent = agentRepository.getRequired(existing.get().getId());
        } else {
            log.info("Adding new agent: [{}]", command);
            agent = new AgentEntity();
            agent.setEmail(command.getEmail().trim().toLowerCase());
        }
        agent.setDisabled(false);
        agent.setTaskTypes(command.getTaskTypes().stream().collect(Collectors.joining(",")));
        return agentRepository.saveAndFlush(agent).getId();

    }

    @Override
    public Optional<Agent> findByEmail(String email) {
        email = email.trim().toLowerCase();
        return agentRepository.getOptional(Entities.agent.email.eq(email)).map(AgentEntity::toValueObject);
    }

    @Override
    public void disableAgent(DisableAgentCommand command) {
        Validate.notBlank(command.getEmail(), "Blank agent email");
        Optional<Agent> existing = findByEmail(command.getEmail());
        if (existing.isPresent()) {
            AgentEntity agent = agentRepository.getRequired(existing.get().getId());
            log.info("Disabling agent: [{}]", agent);
            agent.setDisabled(true);
        } else {
            log.info("No agent found to disable: [{}]", command);
        }
    }
}
