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

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@DynamicUpdate
@Table(name = "alfa_dpd_penalty_strategy", schema = Entities.SCHEMA)
public class AlfaDpdPenaltyStrategyEntity extends BaseEntity {

    @Column(nullable = false)
    private Long calculationStrategyId;

}
