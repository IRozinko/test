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

@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@Entity
@DynamicUpdate
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "promo_code_client", schema = Entities.SCHEMA)
public class PromoCodeClientEntity extends BaseEntity {

    @Column(nullable = false)
    private Long promoCodeId;

    @Column(nullable = false)
    private String clientNumber;

}
