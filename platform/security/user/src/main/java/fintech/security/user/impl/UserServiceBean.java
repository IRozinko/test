package fintech.security.user.impl;

import fintech.Validate;
import fintech.security.user.*;
import fintech.security.user.db.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserServiceBean implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    @Override
    public Optional<User> findUserByEmail(String email) {
        Validate.notBlank(email, "Blank user email");
        String normalizedEmail = email.toLowerCase().trim();
        return userRepository.getOptional(Entities.user.email.eq(normalizedEmail)).map(UserEntity::toValueObject);
    }

    @Transactional
    @Override
    public Long userCount() {
        return userRepository.count();
    }

    @Transactional
    @Override
    public Long addUser(AddUserCommand command) {
        log.info("Adding user: [{}]", command);
        Validate.notBlank(command.getEmail(), "Blank user email");
        Validate.notBlank(command.getPassword(), "Empty password");
        UserEntity entity = new UserEntity();
        entity.setEmail(command.getEmail().trim().toLowerCase());
        String passwordHash = PasswordHash.createHash(command.getPassword());
        entity.setPassword(passwordHash);
        entity.setTemporaryPassword(command.isTemporaryPassword());
        addRoles(entity, command.getRoles());
        return userRepository.saveAndFlush(entity).getId();
    }

    @Transactional
    @Override
    public Long updateUser(UpdateUserCommand command) {
        Validate.notBlank(command.getEmail(), "Blank user email");
        UserEntity entity = userRepository.findOne(Entities.user.email.eq(command.getEmail().trim().toLowerCase()));
        Validate.notNull(entity, "User not found");
        addRoles(entity, command.getRoles());
        return entity.getId();
    }

    private void addRoles(UserEntity user, Set<String> roles) {
        List<RoleEntity> roleEntities = roles.stream().map((role) -> roleRepository.findOne(Entities.role.name.eq(role.trim().toUpperCase()))).collect(Collectors.toList());
        user.getRoles().clear();
        user.getRoles().addAll(roleEntities);
    }

    @Transactional
    @Override
    public void removeUser(RemoveUserCommand command) {
        Validate.notBlank(command.getEmail(), "Blank user email");
        UserEntity entity = userRepository.findOne(Entities.user.email.eq(command.getEmail().trim().toLowerCase()));
        Validate.notNull(entity, "User not found");
        userRepository.delete(entity);
    }

    @Transactional
    @Override
    public void changePassword(ChangePasswordCommand command) {
        Validate.notBlank(command.getEmail(), "Blank user email");
        Validate.notBlank(command.getPassword(), "Empty password");
        UserEntity entity = userRepository.findOne(Entities.user.email.eq(command.getEmail().trim().toLowerCase()));
        Validate.notNull(entity, "User not found");
        String passwordHash = PasswordHash.createHash(command.getPassword());
        entity.setPassword(passwordHash);
        entity.setTemporaryPassword(command.isTemporaryPassword());
    }

    @Transactional
    @Override
    public Long saveRole(SaveRoleCommand command) {
        log.info("Saving role [{}]", command);
        String roleName = command.getName().trim().toUpperCase();
        RoleEntity entity = roleRepository.getOptional(Entities.role.name.eq(roleName)).orElseGet(RoleEntity::new);
        List<PermissionEntity> permissionEntities = command.getPermissions().stream().map(this::savePermission).map((id) -> permissionRepository.getRequired(id)).collect(Collectors.toList());
        entity.setName(roleName);
        entity.getPermissions().clear();
        entity.getPermissions().addAll(permissionEntities);
        entity = roleRepository.saveAndFlush(entity);
        return entity.getId();
    }

    @Transactional
    @Override
    public Long savePermission(@NonNull String permission) {
        permission = permission.trim().toUpperCase();
        PermissionEntity entity = permissionRepository.getOptional(Entities.permission.name.eq(permission)).orElseGet(PermissionEntity::new);
        if (entity.getId() == null) {
            log.info("Adding permission [{}]", permission);
            entity.setName(permission);
            entity = permissionRepository.saveAndFlush(entity);
        }
        return entity.getId();
    }

    @Transactional
    @Override
    public void deleteRole(String roleName) {
        log.info("Deleting role [{}]", roleName);
        Optional<RoleEntity> role = roleRepository.getOptional(Entities.role.name.eq(roleName));
        Validate.isTrue(role.isPresent(), "Role [%s] not found", role);
        List<UserEntity> users = userRepository.findAll(Entities.user.roles.contains(role.get()));
        users.forEach((user) -> user.getRoles().remove(role.get()));
        roleRepository.delete(role.get());
    }
}
