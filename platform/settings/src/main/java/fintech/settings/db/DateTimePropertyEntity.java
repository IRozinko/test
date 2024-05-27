package fintech.settings.db;

import fintech.settings.Property;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = PropertyEntity.class)
@DiscriminatorValue("DATETIME")
public class DateTimePropertyEntity extends PropertyEntity {

    LocalDateTime dateTimeValue;

    @Override
    public Property toValueObject() {
        return Property.builder().name(this.name).dateTimeValue(dateTimeValue).build();
    }
}
