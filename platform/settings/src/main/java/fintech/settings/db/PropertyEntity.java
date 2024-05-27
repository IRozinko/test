package fintech.settings.db;

import fintech.db.BaseEntity;
import fintech.settings.Property;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "property", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "name", name = "idx_property_name"),
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 10)
public abstract class PropertyEntity<T extends Property> extends BaseEntity {

    @Column(nullable = false)
    protected String name;

    private String description;

    public abstract T toValueObject();

}
