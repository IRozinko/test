package fintech.bo.api.server;

import fintech.activity.ActivityService;
import fintech.activity.commands.AddActivityCommand;
import fintech.activity.model.Activity;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.activity.AddActivityRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.api.server.security.BackofficeUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static fintech.activity.model.FindActivitiesQuery.findByAction;

@RestController
public class ActivityApiController {


    @Autowired
    private ActivityService activityService;

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CLIENT_ADD_ACTIVITY})
    @PostMapping("api/bo/activity/add-activity")
    public IdResponse addAction(@AuthenticationPrincipal BackofficeUser user, @Valid @RequestBody AddActivityRequest request) {
        AddActivityCommand command = new AddActivityCommand();
        command.setClientId(request.getClientId());
        command.setAction(request.getAction());
        command.setComments(request.getComments());
        command.setAgent(user.getUsername());
        command.setResolution(request.getResolution());
        command.setTopic(request.getTopic());
        command.setSource("Backoffice");

        request.getBulkActions().forEach(action -> {
            AddActivityCommand.BulkAction a = new AddActivityCommand.BulkAction();
            a.setParams(action.getParams());
            a.setType(action.getType());
            command.getBulkActions().add(a);
        });

        Long id = activityService.addActivity(command);
        return new IdResponse(id);
    }

    @GetMapping("api/bo/activity/{clientId}")
    public ResponseEntity<List<Activity>> getActivities(@PathVariable Long clientId, @RequestParam String action) {
        return ResponseEntity.ok(activityService.findActivities(findByAction(clientId, action)));
    }
}
