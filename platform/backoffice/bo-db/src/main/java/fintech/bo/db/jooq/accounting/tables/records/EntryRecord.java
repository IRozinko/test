/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.accounting.tables.records;


import fintech.bo.db.jooq.accounting.tables.Entry;
import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EntryRecord extends UpdatableRecordImpl<EntryRecord> {

    private static final long serialVersionUID = -1288366493;

    /**
     * Setter for <code>accounting.entry.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>accounting.entry.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>accounting.entry.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>accounting.entry.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>accounting.entry.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>accounting.entry.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>accounting.entry.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>accounting.entry.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>accounting.entry.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>accounting.entry.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>accounting.entry.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>accounting.entry.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>accounting.entry.amount</code>.
     */
    public void setAmount(BigDecimal value) {
        set(6, value);
    }

    /**
     * Getter for <code>accounting.entry.amount</code>.
     */
    public BigDecimal getAmount() {
        return (BigDecimal) get(6);
    }

    /**
     * Setter for <code>accounting.entry.booking_date</code>.
     */
    public void setBookingDate(LocalDate value) {
        set(7, value);
    }

    /**
     * Getter for <code>accounting.entry.booking_date</code>.
     */
    public LocalDate getBookingDate() {
        return (LocalDate) get(7);
    }

    /**
     * Setter for <code>accounting.entry.client_id</code>.
     */
    public void setClientId(Long value) {
        set(8, value);
    }

    /**
     * Getter for <code>accounting.entry.client_id</code>.
     */
    public Long getClientId() {
        return (Long) get(8);
    }

    /**
     * Setter for <code>accounting.entry.credit</code>.
     */
    public void setCredit(BigDecimal value) {
        set(9, value);
    }

    /**
     * Getter for <code>accounting.entry.credit</code>.
     */
    public BigDecimal getCredit() {
        return (BigDecimal) get(9);
    }

    /**
     * Setter for <code>accounting.entry.debit</code>.
     */
    public void setDebit(BigDecimal value) {
        set(10, value);
    }

    /**
     * Getter for <code>accounting.entry.debit</code>.
     */
    public BigDecimal getDebit() {
        return (BigDecimal) get(10);
    }

    /**
     * Setter for <code>accounting.entry.disbursement_id</code>.
     */
    public void setDisbursementId(Long value) {
        set(11, value);
    }

    /**
     * Getter for <code>accounting.entry.disbursement_id</code>.
     */
    public Long getDisbursementId() {
        return (Long) get(11);
    }

    /**
     * Setter for <code>accounting.entry.entry_type</code>.
     */
    public void setEntryType(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>accounting.entry.entry_type</code>.
     */
    public String getEntryType() {
        return (String) get(12);
    }

    /**
     * Setter for <code>accounting.entry.institution_account_id</code>.
     */
    public void setInstitutionAccountId(Long value) {
        set(13, value);
    }

    /**
     * Getter for <code>accounting.entry.institution_account_id</code>.
     */
    public Long getInstitutionAccountId() {
        return (Long) get(13);
    }

    /**
     * Setter for <code>accounting.entry.institution_id</code>.
     */
    public void setInstitutionId(Long value) {
        set(14, value);
    }

    /**
     * Getter for <code>accounting.entry.institution_id</code>.
     */
    public Long getInstitutionId() {
        return (Long) get(14);
    }

    /**
     * Setter for <code>accounting.entry.invoice_id</code>.
     */
    public void setInvoiceId(Long value) {
        set(15, value);
    }

    /**
     * Getter for <code>accounting.entry.invoice_id</code>.
     */
    public Long getInvoiceId() {
        return (Long) get(15);
    }

    /**
     * Setter for <code>accounting.entry.loan_id</code>.
     */
    public void setLoanId(Long value) {
        set(16, value);
    }

    /**
     * Getter for <code>accounting.entry.loan_id</code>.
     */
    public Long getLoanId() {
        return (Long) get(16);
    }

    /**
     * Setter for <code>accounting.entry.payment_id</code>.
     */
    public void setPaymentId(Long value) {
        set(17, value);
    }

    /**
     * Getter for <code>accounting.entry.payment_id</code>.
     */
    public Long getPaymentId() {
        return (Long) get(17);
    }

    /**
     * Setter for <code>accounting.entry.post_date</code>.
     */
    public void setPostDate(LocalDate value) {
        set(18, value);
    }

    /**
     * Getter for <code>accounting.entry.post_date</code>.
     */
    public LocalDate getPostDate() {
        return (LocalDate) get(18);
    }

    /**
     * Setter for <code>accounting.entry.product_id</code>.
     */
    public void setProductId(Long value) {
        set(19, value);
    }

    /**
     * Getter for <code>accounting.entry.product_id</code>.
     */
    public Long getProductId() {
        return (Long) get(19);
    }

    /**
     * Setter for <code>accounting.entry.transaction_id</code>.
     */
    public void setTransactionId(Long value) {
        set(20, value);
    }

    /**
     * Getter for <code>accounting.entry.transaction_id</code>.
     */
    public Long getTransactionId() {
        return (Long) get(20);
    }

    /**
     * Setter for <code>accounting.entry.transaction_type</code>.
     */
    public void setTransactionType(String value) {
        set(21, value);
    }

    /**
     * Getter for <code>accounting.entry.transaction_type</code>.
     */
    public String getTransactionType() {
        return (String) get(21);
    }

    /**
     * Setter for <code>accounting.entry.value_date</code>.
     */
    public void setValueDate(LocalDate value) {
        set(22, value);
    }

    /**
     * Getter for <code>accounting.entry.value_date</code>.
     */
    public LocalDate getValueDate() {
        return (LocalDate) get(22);
    }

    /**
     * Setter for <code>accounting.entry.account_id</code>.
     */
    public void setAccountId(Long value) {
        set(23, value);
    }

    /**
     * Getter for <code>accounting.entry.account_id</code>.
     */
    public Long getAccountId() {
        return (Long) get(23);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached EntryRecord
     */
    public EntryRecord() {
        super(Entry.ENTRY);
    }

    /**
     * Create a detached, initialised EntryRecord
     */
    public EntryRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, BigDecimal amount, LocalDate bookingDate, Long clientId, BigDecimal credit, BigDecimal debit, Long disbursementId, String entryType, Long institutionAccountId, Long institutionId, Long invoiceId, Long loanId, Long paymentId, LocalDate postDate, Long productId, Long transactionId, String transactionType, LocalDate valueDate, Long accountId) {
        super(Entry.ENTRY);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, amount);
        set(7, bookingDate);
        set(8, clientId);
        set(9, credit);
        set(10, debit);
        set(11, disbursementId);
        set(12, entryType);
        set(13, institutionAccountId);
        set(14, institutionId);
        set(15, invoiceId);
        set(16, loanId);
        set(17, paymentId);
        set(18, postDate);
        set(19, productId);
        set(20, transactionId);
        set(21, transactionType);
        set(22, valueDate);
        set(23, accountId);
    }
}
