package fintech.spain.platform.web.db;

import fintech.db.BaseEntity;
import fintech.spain.platform.web.SpecialLinkType;
import fintech.spain.platform.web.model.SpecialLink;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "special_link", schema = Entities.SCHEMA,
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"client_id", "special_link_type"})
    },
    indexes = {
        @Index(columnList = "token", name = "ux_special_link_token", unique = true),
        @Index(columnList = "client_id,special_link_type", name = "idx_special_link_client_id_special_link_type", unique = true),
    }
)
public class SpecialLinkEntity extends BaseEntity {

    @Column(nullable = false, name = "client_id")
    private long clientId;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false, name = "special_link_type")
    @Enumerated(EnumType.STRING)
    private SpecialLinkType type;

    @Column(nullable = false)
    private boolean reusable;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false, name = "auto_login_required")
    private boolean autoLoginRequired;

    public SpecialLink toValueObject() {
        return new SpecialLink()
            .setId(id)
            .setClientId(clientId)
            .setToken(token)
            .setType(type)
            .setReusable(reusable)
            .setExpiresAt(expiresAt)
            .setAutoLoginRequired(autoLoginRequired);
    }
}
