package fintech.bo.api.server;

import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.api.model.workflow.ActivityInfoResponse;
import fintech.bo.api.model.workflow.AddEditDynamicActivityListenerRequest;
import fintech.bo.api.model.workflow.TerminateWorkflowRequest;
import fintech.bo.api.model.workflow.WorkflowInfoResponse;
import fintech.workflow.ActivityListenerStatus;
import fintech.workflow.DynamicActivityListenersService;
import fintech.workflow.UpdateDynamicActivityListenerCommand;
import fintech.workflow.WorkflowService;
import fintech.workflow.spi.WorkflowRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class WorkflowApiController {

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private WorkflowRegistry workflowRegistry;

    @Autowired
    private DynamicActivityListenersService dynamicActivityListenersService;

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.WORKFLOW_TERMINATE})
    @PostMapping("/api/bo/workflows/terminate")
    public void terminateWorkflow(@Valid @RequestBody TerminateWorkflowRequest request) {
        workflowService.terminateWorkflow(request.getWorkflowId(), request.getReason());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.WORKFLOW_EDIT})
    @GetMapping("/api/bo/workflows/list")
    public List<WorkflowInfoResponse> listWorkflows() {
        return workflowRegistry.getDefinitions().stream()
            .map(wd -> new WorkflowInfoResponse().setWorkflowName(wd.getWorkflowName()).setWorkflowVersion(wd.getWorkflowVersion()).setActivities(
                wd.getActivities().stream()
                    .map(ad -> new ActivityInfoResponse().setActivityName(ad.getActivityName()).setResolutions(ad.getResolutions()))
                    .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.WORKFLOW_EDIT})
    @PostMapping("/api/bo/workflows/add-edit-listener")
    public void addDynamicActivityListener(@Valid @RequestBody AddEditDynamicActivityListenerRequest request) {
        dynamicActivityListenersService.addOrEditListener(new UpdateDynamicActivityListenerCommand()
            .setWorkflowName(request.getWorkflowName())
            .setId(request.getId())
            .setName(request.getName())
            .setVersion(request.getVersion())
            .setTriggerName(request.getTriggerName())
            .setResolution(request.getResolution())
            .setListenerStatus(ActivityListenerStatus.valueOf(request.getListenerStatus()))
            .setActivityName(request.getActivityName())
            .setDelay(request.getDelaySec() == null ? null : Duration.ofSeconds(request.getDelaySec()))
            .setFromMidnight(request.getFromMidnight())
            .setArgs(request.getParams())
        );
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.WORKFLOW_EDIT})
    @PostMapping("/api/bo/workflows/remove-listener")
    public void removeListener(@Valid @RequestBody IdRequest request) {
        dynamicActivityListenersService.removeDynamicListener(request.getId());
    }
}
