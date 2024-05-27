package fintech.spain.alfa.product.db;

import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static fintech.BigDecimalUtils.amount;

@Getter
@Setter
@ToString(callSuper = true, exclude = "categories")
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "wealthiness", schema = Entities.SCHEMA)
@DynamicUpdate
public class WealthinessEntity extends BaseEntity {

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private Long instantorResponseId;

    @Column(nullable = false)
    private Long nordigenLogId;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate periodFrom;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate periodTo;

    @Column(nullable = false)
    private int months;

    @Column(nullable = false)
    private BigDecimal nordigenWeightedWealthiness = amount(0);

    @Column(nullable = false)
    private BigDecimal manualWeightedWealthiness = amount(0);

    @OneToMany(mappedBy="wealthiness", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WealthinessCategoryEntity> categories = new ArrayList<>();

    public double nordigenWealthinessOf(List<String> categoryNames) {
        return categories.stream()
            .filter(c -> categoryNames.contains(c.getCategory()))
            .map(c -> c.getNordigenWeightedWealthiness().doubleValue())
            .mapToDouble(Double::doubleValue).sum();
    }
}
