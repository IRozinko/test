package fintech.security.user;

import java.util.Optional;

public interface UserService {

    Optional<User> findUserByEmail(String email);

    Long addUser(AddUserCommand command);

    Long updateUser(UpdateUserCommand command);

    void removeUser(RemoveUserCommand command);

    void changePassword(ChangePasswordCommand command);

    Long saveRole(SaveRoleCommand command);

    Long savePermission(String permission);

    void deleteRole(String role);

    Long userCount();
}
