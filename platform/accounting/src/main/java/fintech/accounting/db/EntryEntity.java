package fintech.accounting.db;

import fintech.accounting.EntryType;
import fintech.db.BaseEntity;
import fintech.transactions.TransactionType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import static fintech.BigDecimalUtils.amount;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "entry", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "account_id", name = "idx_entry_account_id"),
    @Index(columnList = "transactionId", name = "idx_entry_transaction_id"),
    @Index(columnList = "loanId", name = "idx_entry_loan_id"),
    @Index(columnList = "clientId", name = "idx_entry_client_id"),
    @Index(columnList = "postDate", name = "idx_entry_post_date"),
    @Index(columnList = "valueDate", name = "idx_entry_value_date"),
})
public class EntryEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryType entryType;

    @Column(nullable = false)
    private Long transactionId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate postDate;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate valueDate;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate bookingDate;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal credit = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal debit = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount = amount(0);

    private Long clientId;
    private Long paymentId;
    private Long loanId;
    private Long institutionId;
    private Long institutionAccountId;
    private Long productId;
    private Long disbursementId;
    private Long invoiceId;
}
