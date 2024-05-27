package fintech.spain.alfa.product.db;

import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

import static fintech.BigDecimalUtils.amount;

@Getter
@Setter
@ToString(callSuper = true, exclude = "wealthiness")
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "wealthiness_category", schema = Entities.SCHEMA)
@DynamicUpdate
public class WealthinessCategoryEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "wealthiness_id")
    private WealthinessEntity wealthiness;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private BigDecimal nordigenWeightedWealthiness = amount(0);

    @Column(nullable = false)
    private BigDecimal manualWeightedWealthiness = amount(0);

    private String nordigenCategories;

    @Column(nullable = false)
    private BigDecimal weightInPrecent;

}
