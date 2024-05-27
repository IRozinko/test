package fintech.payments.db;

import com.google.common.collect.ImmutableMap;
import fintech.db.BaseEntity;
import fintech.payments.model.StatementRow;
import fintech.payments.model.StatementRowStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OptimisticLock;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "statement_row", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "statement_id", name = "idx_statement_row_statement_id"),
    @Index(columnList = "key", name = "idx_statement_row_key"),
    @Index(columnList = "paymentId", name = "idx_statement_row_payment_id"),
})
public class StatementRowEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "statement_id")
    private StatementEntity statement;

    private Long paymentId;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate date;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate valueDate;

    @Column(nullable = false)
    private String accountNumber;

    private String transactionCode;
    private String counterpartyName;
    private String counterpartyAccount;
    private String counterpartyAddress;

    @Column(nullable = false)
    private String description;
    private String reference;

    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatementRowStatus status;

    private String statusMessage;

    private String key;

    private String suggestedTransactionSubType;

    private String sourceJson;

    @OptimisticLock(excluded = true)
    @ElementCollection
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @CollectionTable(name = "statement_row_attributes", joinColumns = @JoinColumn(name = "statement_row_id"), schema = Entities.SCHEMA)
    private Map<String, String> attributes = new HashMap<>();

    public StatementRow toValueObject() {
        StatementRow row = new StatementRow();
        row.setDate(this.getDate());
        row.setAccountNumber(this.getAccountNumber());
        row.setValueDate(this.getValueDate());
        row.setTransactionCode(this.getTransactionCode());
        row.setCounterpartyName(this.getCounterpartyName());
        row.setCounterpartyAccount(this.getCounterpartyAccount());
        row.setCounterpartyAddress(this.getCounterpartyAddress());
        row.setDescription(this.getDescription());
        row.setReference(this.getReference());
        row.setAmount(this.getAmount());
        row.setCurrency(this.getCurrency());
        row.setStatus(this.getStatus());
        row.setUniqueKey(this.getKey());
        row.setPaymentId(this.getPaymentId());
        row.setBalance(this.getBalance());
        row.setSuggestedTransactionSubType(this.getSuggestedTransactionSubType());
        row.setSourceJson(this.getSourceJson());
        row.setAttributes(ImmutableMap.copyOf(this.getAttributes()));
        return row;
    }

}
