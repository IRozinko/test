package fintech.crm.attachments.db;

import fintech.crm.attachments.Attachment;
import fintech.crm.attachments.AttachmentSubType;
import fintech.crm.attachments.spi.AttachmentDefinition;
import fintech.crm.client.db.ClientEntity;
import fintech.crm.db.Entities;
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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "client_attachment", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "client_id", name = "idx_client_attachment_client_id"),
})
public class ClientAttachmentEntity extends BaseEntity {

    @Column(nullable = false)
    private Long fileId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @Column(nullable = false, name = "attachment_type")
    private String type;

    @Column(name = "attachment_sub_type")
    @Enumerated(EnumType.STRING)
    private AttachmentSubType subType;

    @Column(nullable = false, name = "attachment_group")
    private String group;

    private String name;

    private String status;

    private String statusDetail;

    private Long loanId;

    private Long applicationId;

    private Long transactionId;

    private boolean autoApprove;

    private Integer autoApproveTerm;

    public Attachment toValueObject(AttachmentDefinition definition) {
        Attachment val = new Attachment();
        val.setId(this.getId());
        val.setType(this.getType());
        val.setSubType(this.getSubType());
        val.setFileId(this.getFileId());
        val.setStatus(this.getStatus());
        val.setStatusDetail(this.getStatusDetail());
        val.setLoanId(this.getLoanId());
        val.setDefinition(definition);
        val.setName(this.getName());
        val.setClientId(this.getClient().getId());
        val.setApplicationId(this.getApplicationId());
        val.setTransactionId(this.getTransactionId());
        val.setCreatedAt(createdAt);
        val.setAutoApprove(autoApprove);
        val.setAutoApproveTerm(autoApproveTerm);
        return val;
    }
}
