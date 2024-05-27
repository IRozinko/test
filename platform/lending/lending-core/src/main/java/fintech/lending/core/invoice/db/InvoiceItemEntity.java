package fintech.lending.core.invoice.db;

import fintech.db.BaseEntity;
import fintech.lending.core.db.Entities;
import fintech.lending.core.invoice.InvoiceItem;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

import static fintech.BigDecimalUtils.amount;

@Getter
@Setter
@ToString(callSuper = true, exclude = "invoice")
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "invoice_item", schema = Entities.SCHEMA)
public class InvoiceItemEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "invoice_id")
    private InvoiceEntity invoice;

    @Column(nullable = false)
    private Long loanId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private InvoiceItemType type;

    private String subType;

    @Column(nullable = false)
    private BigDecimal amount = amount(0);

    @Column(nullable = false)
    private BigDecimal amountPaid = amount(0);

    @Column(nullable = false)
    private boolean correction = false;

    public InvoiceItem toValueObject() {
        InvoiceItem val = new InvoiceItem();
        val.setType(type);
        val.setSubType(subType);
        val.setAmount(amount);
        val.setAmountPaid(amountPaid);
        val.setAmountOutstanding(amount.subtract(amountPaid));
        return val;
    }

}
