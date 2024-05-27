package fintech.lending.core.promocode.db;

import fintech.db.BaseEntity;
import fintech.lending.core.db.Entities;
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
@Table(name = "promo_code", schema = Entities.SCHEMA)
public class PromoCodeEntity extends BaseEntity {

    @Column(nullable = false)
    private String code;

    private String description;

    @Column(nullable = false)
    private LocalDate effectiveFrom;

    @Column(nullable = false)
    private LocalDate effectiveTo;

    @Column(nullable = false)
    private BigDecimal rateInPercent;

    @Column(nullable = false)
    private Long maxTimesToApply;

    @Column(nullable = false)
    private boolean newClientsOnly;

    @Column(nullable = false)
    private boolean active;

    public PromoCode toValueObject() {
        return new PromoCode()
            .setId(getId())
            .setActive(active)
            .setCode(code)
            .setDescription(description)
            .setEffectiveFrom(effectiveFrom)
            .setEffectiveTo(effectiveTo)
            .setMaxTimesToApply(maxTimesToApply)
            .setNewClientsOnly(newClientsOnly)
            .setRateInPercent(rateInPercent);
    }

}
