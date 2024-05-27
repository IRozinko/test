package fintech.lending.core.discount.db;

import fintech.db.BaseEntity;
import fintech.lending.core.db.Entities;
import fintech.lending.core.discount.Discount;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@Entity
@DynamicUpdate
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "discount", schema = Entities.SCHEMA)
public class DiscountEntity extends BaseEntity {

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private BigDecimal rateInPercent;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    @Column(nullable = false)
    private Integer maxTimesToApply;

    public Discount toValueObject() {
        return new Discount().setId(id).setRateInPercent(rateInPercent);
    }
}
