package fintech.security.user.db;


import fintech.db.BaseEntity;
import fintech.security.user.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;


@Getter
@Setter
@ToString(callSuper = true, exclude = "password")
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "user", schema = Entities.SCHEMA)
public class UserEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean temporaryPassword;

    @ManyToMany
    @JoinTable(
        name = "user_role",
        schema = "security",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<RoleEntity> roles = new ArrayList<>();

    public User toValueObject() {
        User value = new User();
        value.setId(this.id);
        value.setEmail(this.email);
        value.setPassword(this.password);
        value.setTemporaryPassword(this.temporaryPassword);
        value.setRoles(roles.stream().map(RoleEntity::getName).collect(Collectors.toSet()));
        value.setPermissions(roles.stream().flatMap((role) -> role.getPermissions().stream()).map(PermissionEntity::getName).collect(Collectors.toSet()));
        return value;
    }
}
