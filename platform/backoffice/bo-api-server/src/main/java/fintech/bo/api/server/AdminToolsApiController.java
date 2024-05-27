package fintech.bo.api.server;

import fintech.admintools.AdminToolsService;
import fintech.admintools.ExecuteAdminActionCommand;
import fintech.admintools.ScenarioInfo;
import fintech.admintools.TriggerSchedulerCommand;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.admintools.ExecuteAdminActionRequest;
import fintech.bo.api.model.admintools.ListAdminActionsResponse;
import fintech.bo.api.model.admintools.RunDemoScenarioRequest;
import fintech.bo.api.model.admintools.TriggerSchedulerRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
public class AdminToolsApiController {

    @Autowired
    private AdminToolsService adminToolsService;

    // Fixme: remove using ExecutorService - bad practice, unhandled errors, silent failures, etc...
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @GetMapping("/api/bo/admin-tools/list-actions")
    public ListAdminActionsResponse listAdminActions() {
        List<String> actions = adminToolsService.listAvailableActions();
        ListAdminActionsResponse response = new ListAdminActionsResponse();
        response.setActions(actions);
        return response;
    }

    @PostMapping("/api/bo/admin-tools/execute-action")
    public IdResponse executeAction(@Valid @RequestBody ExecuteAdminActionRequest request) {
        ExecuteAdminActionCommand command = new ExecuteAdminActionCommand();
        command.setName(request.getName());
        command.setParams(request.getParams());
        Long id = adminToolsService.execute(command);
        return new IdResponse(id);
    }

    @PostMapping("/api/bo/admin-tools/trigger-scheduler")
    public void triggerScheduler(@Valid @RequestBody TriggerSchedulerRequest request) {
        TriggerSchedulerCommand command = new TriggerSchedulerCommand();
        command.setName(request.getName());
         adminToolsService.triggerScheduler(command);
    }

    @GetMapping("/api/bo/admin-tools/list-demo-scenarios")
    public List<ScenarioInfo> listDemoScenarios() {
        return adminToolsService.listAvailableDemoScenarios();
    }

    @SneakyThrows
    @PostMapping("/api/bo/admin-tools/run-demo-scenario")
    public void runDemoScenario(@Valid @RequestBody RunDemoScenarioRequest request) {
        // without async it sometimes causes very very weird Hibernate optimistic lock exceptions
        // same demo scenarios would run fine from integration tests or command line, but fail if not run async
        executor.submit(() -> adminToolsService.runDemoScenario(request.getName(), request.getParameters())).get();
    }
}
