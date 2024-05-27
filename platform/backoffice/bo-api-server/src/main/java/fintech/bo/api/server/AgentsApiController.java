package fintech.bo.api.server;

import fintech.bo.api.model.agents.DisableAgentRequest;
import fintech.bo.api.model.agents.UpdateAgentRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.task.command.AddAgentCommand;
import fintech.task.AgentService;
import fintech.task.command.DisableAgentCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AgentsApiController {

    @Autowired
    private AgentService agentService;

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CS_AGENTS_EDIT})
    @PostMapping(path = "/api/bo/agents")
    public void createOrUpdate(@RequestBody UpdateAgentRequest request) {
        log.info("Updating agent [{}]", request);

        AddAgentCommand addAgentCommand = new AddAgentCommand();
        addAgentCommand.setEmail(request.getEmail());
        addAgentCommand.setTaskTypes(request.getTaskTypes());
        agentService.addAgent(addAgentCommand);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CS_AGENTS_EDIT})
    @PostMapping(path = "/api/bo/agents/disable")
    public void disable(@RequestBody DisableAgentRequest request) {
        log.info("Disabling agent [{}]", request.getEmail());

        DisableAgentCommand disableAgentCommand = new DisableAgentCommand();
        disableAgentCommand.setEmail(request.getEmail());

        agentService.disableAgent(disableAgentCommand);
    }
}
