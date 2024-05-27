package fintech.crm.logins.db;

import fintech.crm.client.db.ClientEntity;
import fintech.crm.db.Entities;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "reset_password_token", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "client_id", name = "idx_reset_password_token_client_id"),
})
public class ResetPasswordTokenEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_used", nullable = false)
    private boolean used;
}
