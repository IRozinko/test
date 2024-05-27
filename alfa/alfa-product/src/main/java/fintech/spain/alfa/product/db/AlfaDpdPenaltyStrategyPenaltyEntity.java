package fintech.spain.alfa.product.db;

import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@DynamicUpdate
@Table(name = "alfa_dpd_penalty_strategy_penalty", schema = Entities.SCHEMA)
public class AlfaDpdPenaltyStrategyPenaltyEntity extends BaseEntity {

    @Column(nullable = false)
    private Long dpdPenaltyStrategyId;

    @Column(nullable = false)
    private Integer daysFrom;

    @Column(nullable = false)
    private BigDecimal penaltyRate;

}
