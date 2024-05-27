package fintech.settings.db;

import fintech.settings.Property;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = PropertyEntity.class)
@DiscriminatorValue("DATE")
public class DatePropertyEntity extends PropertyEntity {

    @Column(columnDefinition = "DATE")
    LocalDate dateValue;

    @Override
    public Property toValueObject() {
        return Property.builder().name(this.name).dateValue(dateValue).build();
    }
}
