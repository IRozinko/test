package fintech.ga.db;

import fintech.db.BaseEntity;
import fintech.ga.GAClientData;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@ToString(callSuper = true)
@Table(name = "client_data", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_client_data_client_id", unique = true)
})
@DynamicUpdate
@Audited
@AuditOverride(forClass = BaseEntity.class)
public class GAClientDataEntity extends BaseEntity {

    private Long clientId;

    private String cookieUserId;

    private String userAgent;


    public GAClientData toValueObject() {
        GAClientData val = new GAClientData();
        val.setId(id);
        val.setClientId(clientId);
        val.setCookieUserId(cookieUserId);
        val.setUserAgent(userAgent);
        return val;
    }


}
