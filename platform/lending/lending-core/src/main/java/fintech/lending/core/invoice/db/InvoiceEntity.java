package fintech.lending.core.invoice.db;

import com.google.common.collect.Lists;
import fintech.db.BaseEntity;
import fintech.lending.core.db.Entities;
import fintech.lending.core.invoice.Invoice;
import fintech.lending.core.invoice.InvoiceStatus;
import fintech.lending.core.invoice.InvoiceStatusDetail;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
import java.time.LocalDateTime;
import java.util.List;

import static fintech.BigDecimalUtils.amount;
import static fintech.BigDecimalUtils.goe;
import static fintech.BigDecimalUtils.isPositive;
import static fintech.lending.core.invoice.db.InvoiceItemType.FEE;
import static fintech.lending.core.invoice.db.InvoiceItemType.INTEREST;
import static fintech.lending.core.invoice.db.InvoiceItemType.PENALTY;
import static fintech.lending.core.invoice.db.InvoiceItemType.PRINCIPAL;
import static java.util.stream.Collectors.toList;

@Getter
@Setter
@ToString(callSuper = true, exclude = "items")
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "invoice", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_invoice_client_id"),
    @Index(columnList = "loanId", name = "idx_invoice_loan_id"),
    @Index(columnList = "periodFrom", name = "idx_invoice_period_from"),
    @Index(columnList = "periodTo", name = "idx_invoice_period_to"),
    @Index(columnList = "invoiceDate", name = "idx_invoice_invoice_date"),
    @Index(columnList = "dueDate", name = "idx_invoice_due_date"),
})
public class InvoiceEntity extends BaseEntity {

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private Long loanId;

    private Long fileId;

    private String fileName;

    @Column(nullable = false)
    private String number;

    @Enumerated(EnumType.STRING)
    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Setter(AccessLevel.PRIVATE)
    private InvoiceStatusDetail statusDetail = InvoiceStatusDetail.PENDING;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate periodFrom;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate periodTo;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate invoiceDate;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate dueDate;

    private LocalDate closeDate;

    private String closeReason;

    @Column(nullable = false)
    private boolean voided = false;

    @Column(nullable = false)
    private int corrections = 0;

    @Column(nullable = false)
    private BigDecimal total = amount(0);

    @Column(nullable = false)
    private BigDecimal totalPaid = amount(0);

    @Column(nullable = false)
    private boolean generateFile;

    @Column(nullable = false)
    private boolean sendFile;

    private LocalDateTime sentAt;

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<InvoiceItemEntity> items = Lists.newArrayList();

    @Column
    private Boolean membershipLevelChanged;

    @Column(nullable = false)
    private boolean manual;

    public Invoice toValueObject() {
        Invoice invoice = new Invoice();
        invoice.setId(id);
        invoice.setProductId(productId);
        invoice.setClientId(clientId);
        invoice.setLoanId(loanId);
        invoice.setFileId(fileId);
        invoice.setFileName(fileName);
        invoice.setNumber(number);
        invoice.setStatus(status);
        invoice.setStatusDetail(statusDetail);
        invoice.setPeriodFrom(periodFrom);
        invoice.setPeriodTo(periodTo);
        invoice.setInvoiceDate(invoiceDate);
        invoice.setDueDate(dueDate);
        invoice.setCloseDate(closeDate);
        invoice.setCloseReason(closeReason);
        invoice.setTotal(total);
        invoice.setTotalPaid(totalPaid);
        invoice.setTotalDue(total.subtract(totalPaid));
        invoice.setPrincipalDue(getOutstandingAmount(PRINCIPAL));
        invoice.setInterestDue(getOutstandingAmount(INTEREST));
        invoice.setFeeDue(getOutstandingAmount(FEE));
        invoice.setPenaltyDue(getOutstandingAmount(PENALTY));
        invoice.setItems(items.stream().map(InvoiceItemEntity::toValueObject).collect(toList()));
        invoice.setVoided(voided);
        invoice.setGenerateFile(generateFile);
        invoice.setSendFile(sendFile);
        invoice.setSentAt(sentAt);
        invoice.setMembershipLevelChanged(membershipLevelChanged);
        invoice.setManual(manual);
        return invoice;
    }

    public boolean isAmountPaid() {
        return goe(totalPaid, total);
    }

    public boolean isAmountPartiallyPaid() {
        return !isAmountPaid() && isPositive(totalPaid);
    }

    public void open(InvoiceStatusDetail detail) {
        status = InvoiceStatus.OPEN;
        statusDetail = detail;
        closeDate = null;
    }

    public void close(InvoiceStatusDetail detail, LocalDate when) {
        close(detail, when, null);
    }

    public void close(InvoiceStatusDetail statusDetail, LocalDate when, String closeReason) {
        this.status = InvoiceStatus.CLOSED;
        this.statusDetail = statusDetail;
        this.closeDate = when;
        this.closeReason = closeReason;
        if (statusDetail == InvoiceStatusDetail.VOIDED) {
            this.voided = true;
        }
    }

    public void addItem(InvoiceItemEntity item) {
        items.add(item);
    }

    private BigDecimal getOutstandingAmount(InvoiceItemType type) {
        return items.stream()
            .filter(item -> item.getType() == type)
            .map(item -> item.getAmount().subtract(item.getAmountPaid()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isOpen() {
        return status == InvoiceStatus.OPEN;
    }

    public boolean isClosed() {
        return status == InvoiceStatus.CLOSED;
    }

    public void corrected() {
        corrections++;
    }
}
