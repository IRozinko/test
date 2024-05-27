package fintech.crm.marketing.model;

import fintech.crm.db.Entities;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
@Table(name = "marketing_consent_log", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_marketing_consent_log_client_id")
})
@Accessors(chain = true)
public class MarketingConsentLogEntity extends BaseEntity {

    private Long clientId;
    private LocalDateTime timestamp;
    private boolean value;
    private String source;
    private String note;

}
