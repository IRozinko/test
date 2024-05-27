package fintech.marketing.db;


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
@Table(name = "marketing_template", schema = Entities.SCHEMA)
public class MarketingTemplateEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String emailBody;

    @Column(nullable = false)
    private String htmlTemplate;

    @Column(nullable = false)
    private Long imageFileId;

}
