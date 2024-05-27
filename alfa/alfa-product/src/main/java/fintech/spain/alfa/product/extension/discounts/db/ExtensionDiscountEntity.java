package fintech.spain.alfa.product.extension.discounts.db;

import fintech.db.BaseEntity;
import fintech.lending.core.loan.db.LoanEntity;
import fintech.spain.alfa.product.db.Entities;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString(callSuper = true)
@Accessors(chain = true)
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Entity
@Table(name = "extension_discount", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "loan_id", name = "idx_extension_discount_loan_id"),
})
public class ExtensionDiscountEntity extends BaseEntity {

    @Column(nullable = false)
    private LocalDate effectiveFrom;

    @Column(nullable = false)
    private LocalDate effectiveTo;

    @Column(nullable = false)
    private BigDecimal rateInPercent;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(optional = false)
    @JoinColumn(name = "loan_id")
    private LoanEntity loan;

    public ExtensionDiscountEntity toValueObject() {
        ExtensionDiscountEntity item = new ExtensionDiscountEntity();
        item.setLoan(this.loan);
        item.setActive(this.active);
        item.setEffectiveFrom(this.effectiveFrom);
        item.setEffectiveTo(this.effectiveTo);
        item.setRateInPercent(this.rateInPercent);
        return item;
    }
}
