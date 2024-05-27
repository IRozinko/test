package fintech.spain.alfa.product.db;

import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "alfa_fee_strategy", schema = Entities.SCHEMA)
@DynamicUpdate
public class AlfaFeeStrategyEntity extends BaseEntity {

    @Column(nullable = false)
    private Long calculationStrategyId;

    @Column(nullable = false)
    private BigDecimal feeRate;

    @Column(nullable = false)
    private String company;
}
