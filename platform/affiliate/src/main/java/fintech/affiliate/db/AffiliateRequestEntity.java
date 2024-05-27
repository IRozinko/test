package fintech.affiliate.db;


import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "affiliate_request", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "client_id", name = "idx_affiliate_request_client_id"),
    @Index(columnList = "application_id", name = "idx_affiliate_request_application_id"),
})
@TypeDef(
    name = "jsonb-node",
    typeClass = JsonNodeBinaryType.class
)
@DynamicUpdate
public class AffiliateRequestEntity extends BaseEntity {

    @Column(nullable = false)
    private String requestType;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "application_id")
    private Long applicationId;

    @Type(type = "jsonb-node")
    @Column(columnDefinition = "jsonb", name = "request")
    private JsonNode request;

    @Type(type = "jsonb-node")
    @Column(columnDefinition = "jsonb", name = "response")
    private JsonNode response;
}
