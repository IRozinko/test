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
@Table(name = "alfa_daily_penalty_strategy", schema = Entities.SCHEMA)
@DynamicUpdate
public class AlfaDailyPenaltyStrategyEntity extends BaseEntity {

    @Column(nullable = false)
    private Long calculationStrategyId;

    @Column(nullable = false)
    private BigDecimal penaltyRate;
}
