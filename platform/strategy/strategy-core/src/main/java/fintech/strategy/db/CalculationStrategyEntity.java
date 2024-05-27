package fintech.strategy.db;

import fintech.db.BaseEntity;
import fintech.strategy.CalculationStrategy;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "calculation_strategy", schema = Entities.SCHEMA)
public class CalculationStrategyEntity extends BaseEntity {

    @Column(nullable = false)
    private String strategyType;

    @Column(nullable = false)
    private String calculationType;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private boolean enabled;

    @Column
    private Boolean isDefault;

    public CalculationStrategy toValueObject() {
        CalculationStrategy val = new CalculationStrategy();
        val.setId(this.id);
        val.setStrategyType(this.strategyType);
        val.setCalculationType(this.calculationType);
        val.setVersion(this.version);
        val.setEnabled(this.enabled);
        val.setIsDefault(this.isDefault);
        return val;
    }
}
