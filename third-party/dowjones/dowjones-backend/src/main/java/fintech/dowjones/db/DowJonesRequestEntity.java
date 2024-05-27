package fintech.dowjones.db;

import fintech.db.BaseEntity;
import fintech.dowjones.DowJonesRequest;
import fintech.dowjones.DowJonesResponseStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true, of = {"clientId"})
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "request", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_dowjones_client_id"),
})
public class DowJonesRequestEntity extends BaseEntity {

    @Column(nullable = false)
    private Long clientId;

    @Enumerated(EnumType.STRING)
    private DowJonesResponseStatus status;

    private String requestUrl;

    private String requestBody;

    private String responseBody;

    private int responseStatusCode;

    private String error;

    public DowJonesRequest toValueObject() {
        DowJonesRequest vo = new DowJonesRequest();
        vo.setId(this.id);
        vo.setClientId(this.clientId);
        vo.setRequestBody(this.requestBody);
        vo.setResponseStatusCode(responseStatusCode);
        vo.setResponseBody(this.responseBody);
        vo.setStatus(this.status);
        vo.setUrl(this.requestUrl);
        vo.setError(this.error);
        return vo;
    }
}
