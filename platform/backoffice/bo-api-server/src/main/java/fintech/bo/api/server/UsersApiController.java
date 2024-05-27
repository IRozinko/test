package fintech.bo.api.server;

import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.api.model.users.*;
import fintech.bo.api.server.security.BackofficeUser;
import fintech.security.user.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UsersApiController {

    @Autowired
    private UserService userService;

    @Secured({BackofficePermissions.ADMIN})
    @PostMapping(path = "/api/bo/users/update")
    public void updateUser(@RequestBody UpdateUserRequest request) {
        log.info("Updating user [{}]", request);
        UpdateUserCommand command = new UpdateUserCommand();
        command.setEmail(request.getEmail());
        command.setRoles(request.getRoles());
        userService.updateUser(command);
    }

    @Secured({BackofficePermissions.ADMIN})
    @PostMapping(path = "/api/bo/users/remove")
    public void remove(@RequestBody RemoveUserRequest request) {
        log.info("Disabling user [{}]", request.getEmail());
        RemoveUserCommand command = new RemoveUserCommand();
        command.setEmail(request.getEmail());
        userService.removeUser(command);
    }

    @Secured({BackofficePermissions.ADMIN})
    @PostMapping(path = "/api/bo/users/add")
    public void addUser(@RequestBody AddUserRequest request) {
        log.info("Adding user [{}]", request.getEmail());
        AddUserCommand command = new AddUserCommand();
        command.setEmail(request.getEmail());
        command.setTemporaryPassword(request.isTemporaryPassword());
        command.setPassword(request.getPassword());
        command.setRoles(request.getRoles());
        userService.addUser(command);
    }

    @Secured({BackofficePermissions.ADMIN})
    @PostMapping(path = "/api/bo/users/change-password")
    public void changePassword(@RequestBody ChangePasswordRequest request) {
        log.info("Changing password [{}]", request.getEmail());
        ChangePasswordCommand command = new ChangePasswordCommand();
        command.setEmail(request.getEmail());
        command.setTemporaryPassword(request.isTemporaryPassword());
        command.setPassword(request.getPassword());
        userService.changePassword(command);
    }

    @PostMapping(path = "/api/bo/users/change-my-password")
    public void changeMyPassword(@AuthenticationPrincipal BackofficeUser user, @RequestBody ChangeMyPasswordRequest request) {
        log.info("Changing my password [{}]", user.getUsername());
        ChangePasswordCommand command = new ChangePasswordCommand();
        command.setEmail(user.getUsername());
        command.setTemporaryPassword(false);
        command.setPassword(request.getPassword());
        userService.changePassword(command);
    }
}
