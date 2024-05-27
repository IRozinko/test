package fintech.settings.db;

import fintech.settings.Property;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;


@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = PropertyEntity.class)
@DiscriminatorValue("DECIMAL")
public class DecimalPropertyEntity extends PropertyEntity {

    BigDecimal decimalValue;

    @Override
    public Property toValueObject() {
        return Property.builder().name(this.name).decimalValue(decimalValue).build();
    }
}
