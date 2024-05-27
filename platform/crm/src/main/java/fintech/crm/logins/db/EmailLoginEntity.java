package fintech.crm.logins.db;

import fintech.crm.client.db.ClientEntity;
import fintech.crm.db.Entities;
import fintech.crm.logins.EmailLogin;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "email_login", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "email", name = "idx_email_login_email", unique = true),
    @Index(columnList = "client_id", name = "idx_email_login_client_id", unique = true),
})
public class EmailLoginEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean temporaryPassword;

    public EmailLogin toValueObject() {
        EmailLogin value = new EmailLogin();
        value.setId(getId());
        value.setClientId(client.getId());
        value.setClientNumber(client.getNumber());
        value.setEmail(email);
        value.setPassword(password);
        value.setTemporaryPassword(temporaryPassword);
        return value;
    }

}
