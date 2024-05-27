package fintech.settings.db;

import fintech.settings.Property;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = PropertyEntity.class)
@DiscriminatorValue("TEXT")
public class TextPropertyEntity extends PropertyEntity {

    String textValue;

    @Override
    public Property toValueObject() {
        return Property.builder().name(this.name).textValue(textValue).build();
    }
}
