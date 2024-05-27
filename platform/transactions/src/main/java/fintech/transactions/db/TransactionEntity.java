package fintech.transactions.db;

import fintech.db.BaseEntity;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static fintech.BigDecimalUtils.amount;
import static java.util.stream.Collectors.toList;

@Getter
@Setter
@ToString(callSuper = true, exclude = {"entries"})
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "transaction", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "valueDate", name = "idx_transaction_value_date"),
    @Index(columnList = "bookingDate", name = "idx_transaction_booking_date"),
    @Index(columnList = "clientId", name = "idx_transaction_client_id"),
    @Index(columnList = "loanId", name = "idx_transaction_loan_id"),
    @Index(columnList = "paymentId", name = "idx_transaction_payment_id"),
    @Index(columnList = "invoiceId", name = "idx_transaction_invoice_id"),
    @Index(columnList = "disbursementId", name = "idx_transaction_disbursement_id"),
}
)
@DynamicUpdate
public class TransactionEntity extends BaseEntity {

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate postDate;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate valueDate;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate bookingDate;

    @Column(columnDefinition = "DATE")
    private LocalDate voidedDate;

    @Column(nullable = false)
    private boolean voided;

    private Long voidsTransactionId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private String transactionSubType;

    private Long paymentId;

    private Long institutionId;

    private Long institutionAccountId;

    private Long clientId;

    private Long loanId;

    private Long applicationId;

    private Long productId;

    private Long disbursementId;

    private Long invoiceId;

    private Long scheduleId;

    private Long installmentId;

    private Long contractId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalDisbursed = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalPaid = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalWrittenOff = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalInvoiced = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestApplied = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestPaid = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestWrittenOff = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestInvoiced = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyApplied = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyPaid = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyWrittenOff = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal penaltyInvoiced = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feeApplied = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feePaid = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feeWrittenOff = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal feeInvoiced = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal overpaymentReceived = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal overpaymentUsed = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal overpaymentRefunded = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal creditLimit = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal creditLimitAvailable = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal cashIn = amount(0);

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal cashOut = amount(0);

    @Column(nullable = false)
    private Long extension = 0L;

    @Column(nullable = false)
    private Long extensionDays = 0L;

    private Integer dpd;

    private String comments;

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionEntryEntity> entries = newArrayList();

    public Transaction toValueObject() {
        Transaction val = new Transaction();
        val.setId(this.id);
        val.setTransactionType(this.transactionType);
        val.setTransactionSubType(this.transactionSubType);
        val.setPostDate(this.postDate);
        val.setBookingDate(this.bookingDate);
        val.setValueDate(this.valueDate);
        val.setClientId(this.clientId);
        val.setPaymentId(this.paymentId);
        val.setLoanId(this.loanId);
        val.setInstitutionId(this.institutionId);
        val.setInstitutionAccountId(this.institutionAccountId);
        val.setProductId(this.productId);
        val.setDisbursementId(this.disbursementId);
        val.setInvoiceId(this.invoiceId);
        val.setScheduleId(this.scheduleId);
        val.setInstallmentId(this.installmentId);
        val.setContractId(this.contractId);
        val.setPrincipalDisbursed(this.principalDisbursed);
        val.setPrincipalPaid(this.principalPaid);
        val.setPrincipalWrittenOff(this.principalWrittenOff);
        val.setPrincipalInvoiced(this.principalInvoiced);
        val.setInterestApplied(this.interestApplied);
        val.setInterestPaid(this.interestPaid);
        val.setInterestWrittenOff(this.interestWrittenOff);
        val.setInterestInvoiced(this.interestInvoiced);
        val.setPenaltyApplied(this.penaltyApplied);
        val.setPenaltyPaid(this.penaltyPaid);
        val.setPenaltyWrittenOff(this.penaltyWrittenOff);
        val.setPenaltyInvoiced(this.penaltyInvoiced);
        val.setFeeApplied(this.feeApplied);
        val.setFeePaid(this.feePaid);
        val.setFeeWrittenOff(this.feeWrittenOff);
        val.setFeeInvoiced(this.feeInvoiced);
        val.setCashIn(this.cashIn);
        val.setCashOut(this.cashOut);
        val.setOverpaymentReceived(this.overpaymentReceived);
        val.setOverpaymentUsed(this.overpaymentUsed);
        val.setOverpaymentRefunded(this.overpaymentRefunded);
        val.setCreditLimit(this.creditLimit);
        val.setCreditLimitAvailable(this.creditLimitAvailable);
        val.setComments(this.comments);
        val.setVoidsTransactionId(this.voidsTransactionId);
        val.setVoided(this.voided);
        val.setVoidedDate(this.voidedDate);
        val.setExtension(this.extension);
        val.setExtensionDays(this.extensionDays);
        val.setCreatedAt(this.createdAt);
        val.setCreatedBy(this.createdBy);
        val.setEntries(entries.stream().sorted(Comparator.comparingLong(BaseEntity::getId)).map(TransactionEntryEntity::toValueObject).collect(toList()));
        val.setDpd(this.dpd);
        return val;
    }

    public void negate() {
        this.principalDisbursed = principalDisbursed.negate();
        this.principalPaid = principalPaid.negate();
        this.principalWrittenOff = principalWrittenOff.negate();
        this.principalInvoiced = principalInvoiced.negate();
        this.interestApplied = interestApplied.negate();
        this.interestPaid = interestPaid.negate();
        this.interestWrittenOff = interestWrittenOff.negate();
        this.interestInvoiced = interestInvoiced.negate();
        this.penaltyApplied = penaltyApplied.negate();
        this.penaltyPaid = penaltyPaid.negate();
        this.penaltyWrittenOff = penaltyWrittenOff.negate();
        this.penaltyInvoiced = penaltyInvoiced.negate();
        this.feeApplied = feeApplied.negate();
        this.feePaid = feePaid.negate();
        this.feeWrittenOff = feeWrittenOff.negate();
        this.feeInvoiced = feeInvoiced.negate();
        this.cashIn = cashIn.negate();
        this.cashOut = cashOut.negate();
        this.overpaymentReceived = this.overpaymentReceived.negate();
        this.overpaymentRefunded = this.overpaymentRefunded.negate();
        this.overpaymentUsed = this.overpaymentUsed.negate();
        this.extensionDays = -this.extensionDays;
        this.extension = -this.extension;
    }

    public void addEntry(TransactionEntryEntity entryEntity) {
        entries.add(entryEntity);
    }

}
