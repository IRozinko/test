package fintech.lending.core.creditlimit.db;

import fintech.db.BaseEntity;
import fintech.lending.core.creditlimit.CreditLimit;
import fintech.lending.core.db.Entities;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "credit_limit", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_credit_limit_client_id"),
})
public class CreditLimitEntity extends BaseEntity {

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false, name = "credit_limit")
    private BigDecimal limit;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate activeFrom;

    @Column(nullable = false)
    private String reason;

    public CreditLimit toValueObject() {
        CreditLimit val = new CreditLimit();
        val.setClientId(this.clientId);
        val.setLimit(this.limit);
        val.setReason(this.reason);
        val.setActiveFrom(this.activeFrom);
        return val;
    }
}
