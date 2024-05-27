package fintech.cms.db;


import fintech.cms.spi.CmsItem;
import fintech.cms.spi.CmsItemType;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "item", schema = Entities.SCHEMA)
public class CmsItemEntity extends BaseEntity {

    @Column(nullable = false, name = "item_key")
    private String key;

    @Column(nullable = false)
    private String locale;

    private String description;

    @Column(nullable = false)
    private String scope;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CmsItemType itemType;

    private String emailSubjectTemplate;

    private String emailBodyTemplate;

    private String smsTextTemplate;

    private String contentTemplate;

    private String titleTemplate;

    private String headerTemplate;

    private String footerTemplate;

    public CmsItem toValueObject() {
        CmsItem item = new CmsItem();
        item.setKey(this.key);
        item.setLocale(this.locale);
        item.setDescription(this.description);
        item.setScope(this.scope);
        item.setItemType(this.itemType);
        item.setEmailSubjectTemplate(this.emailSubjectTemplate);
        item.setEmailBodyTemplate(this.emailBodyTemplate);
        item.setSmsTextTemplate(this.smsTextTemplate);
        item.setContentTemplate(this.contentTemplate);
        item.setTitleTemplate(this.titleTemplate);
        item.setHeaderTemplate(this.headerTemplate);
        item.setFooterTemplate(this.footerTemplate);
        return item;
    }
}
