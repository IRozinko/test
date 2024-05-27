package fintech.spain.consents.db;

import fintech.db.BaseEntity;
import fintech.spain.consents.model.Terms;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "terms", schema = Entities.SCHEMA)
public class TermsEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime changedAt;

    public Terms toValueObject() {
        Terms terms = new Terms();
        terms.setName(this.name);
        terms.setText(this.text);
        terms.setVersion(this.version);
        terms.setChangedAt(this.changedAt);
        return terms;
    }

}
