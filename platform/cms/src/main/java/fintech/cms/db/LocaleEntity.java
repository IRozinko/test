package fintech.cms.db;


import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@Table(name = "locale", schema = Entities.SCHEMA)
public class LocaleEntity extends BaseEntity {

    @Column(nullable = false)
    private String locale;

    @Column(nullable = false)
    private Boolean isDefault;

}
