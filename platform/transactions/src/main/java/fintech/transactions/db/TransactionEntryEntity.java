package fintech.transactions.db;

import fintech.db.BaseEntity;
import fintech.transactions.TransactionEntry;
import fintech.transactions.TransactionEntryType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

import static fintech.BigDecimalUtils.amount;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "transaction_entry", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "transaction_id", name = "idx_transaction_entry_transaction_id"),
})
@DynamicUpdate
public class TransactionEntryEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private TransactionEntryType type;

    private String subType;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amountApplied = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amountPaid = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amountWrittenOff = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amountInvoiced = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amountScheduled = amount(0);

    @ManyToOne(optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionEntity transaction;

    public TransactionEntry toValueObject() {
        TransactionEntry val = new TransactionEntry();
        val.setType(type);
        val.setSubType(subType);
        val.setAmountApplied(amountApplied);
        val.setAmountPaid(amountPaid);
        val.setAmountWrittenOff(amountWrittenOff);
        val.setAmountInvoiced(amountInvoiced);
        val.setAmountScheduled(amountScheduled);
        return val;
    }

    public void negate() {
        this.amountApplied = amountApplied.negate();
        this.amountPaid = amountPaid.negate();
        this.amountWrittenOff = amountWrittenOff.negate();
        this.amountInvoiced = amountInvoiced.negate();
        this.amountScheduled = amountScheduled.negate();
    }
}
