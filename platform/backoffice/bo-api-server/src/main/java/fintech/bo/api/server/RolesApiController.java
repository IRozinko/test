package fintech.bo.api.server;

import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.api.model.permissions.SaveRoleRequest;
import fintech.security.user.SaveRoleCommand;
import fintech.security.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class RolesApiController {

    @Autowired
    private UserService userService;

    @Secured({BackofficePermissions.ADMIN})
    @PostMapping(path = "/api/bo/roles")
    public void update(@RequestBody SaveRoleRequest request) {
        log.info("Updating role [{}]", request);

        SaveRoleCommand saveRoleCommand = new SaveRoleCommand();
        saveRoleCommand.setName(request.getName());
        saveRoleCommand.setPermissions(request.getPermissions());

        userService.saveRole(saveRoleCommand);
    }

    @Secured({BackofficePermissions.ADMIN})
    @DeleteMapping(path = "/api/bo/roles/{name}")
    public void update(@PathVariable String name) {
        log.info("Deleting role [{}]", name);

        userService.deleteRole(name);
    }
}
