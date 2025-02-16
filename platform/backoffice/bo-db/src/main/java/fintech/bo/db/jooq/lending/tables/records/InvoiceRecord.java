/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.lending.tables.records;


import fintech.bo.db.jooq.lending.tables.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.annotation.Generated;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


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
public class InvoiceRecord extends UpdatableRecordImpl<InvoiceRecord> {

    private static final long serialVersionUID = 825485647;

    /**
     * Setter for <code>lending.invoice.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>lending.invoice.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>lending.invoice.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>lending.invoice.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>lending.invoice.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>lending.invoice.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>lending.invoice.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>lending.invoice.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>lending.invoice.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>lending.invoice.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>lending.invoice.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>lending.invoice.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>lending.invoice.client_id</code>.
     */
    public void setClientId(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>lending.invoice.client_id</code>.
     */
    public Long getClientId() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>lending.invoice.close_date</code>.
     */
    public void setCloseDate(LocalDateTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>lending.invoice.close_date</code>.
     */
    public LocalDateTime getCloseDate() {
        return (LocalDateTime) get(7);
    }

    /**
     * Setter for <code>lending.invoice.close_reason</code>.
     */
    public void setCloseReason(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>lending.invoice.close_reason</code>.
     */
    public String getCloseReason() {
        return (String) get(8);
    }

    /**
     * Setter for <code>lending.invoice.corrections</code>.
     */
    public void setCorrections(Integer value) {
        set(9, value);
    }

    /**
     * Getter for <code>lending.invoice.corrections</code>.
     */
    public Integer getCorrections() {
        return (Integer) get(9);
    }

    /**
     * Setter for <code>lending.invoice.due_date</code>.
     */
    public void setDueDate(LocalDate value) {
        set(10, value);
    }

    /**
     * Getter for <code>lending.invoice.due_date</code>.
     */
    public LocalDate getDueDate() {
        return (LocalDate) get(10);
    }

    /**
     * Setter for <code>lending.invoice.file_id</code>.
     */
    public void setFileId(Long value) {
        set(11, value);
    }

    /**
     * Getter for <code>lending.invoice.file_id</code>.
     */
    public Long getFileId() {
        return (Long) get(11);
    }

    /**
     * Setter for <code>lending.invoice.file_name</code>.
     */
    public void setFileName(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>lending.invoice.file_name</code>.
     */
    public String getFileName() {
        return (String) get(12);
    }

    /**
     * Setter for <code>lending.invoice.invoice_date</code>.
     */
    public void setInvoiceDate(LocalDate value) {
        set(13, value);
    }

    /**
     * Getter for <code>lending.invoice.invoice_date</code>.
     */
    public LocalDate getInvoiceDate() {
        return (LocalDate) get(13);
    }

    /**
     * Setter for <code>lending.invoice.loan_id</code>.
     */
    public void setLoanId(Long value) {
        set(14, value);
    }

    /**
     * Getter for <code>lending.invoice.loan_id</code>.
     */
    public Long getLoanId() {
        return (Long) get(14);
    }

    /**
     * Setter for <code>lending.invoice.number</code>.
     */
    public void setNumber(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>lending.invoice.number</code>.
     */
    public String getNumber() {
        return (String) get(15);
    }

    /**
     * Setter for <code>lending.invoice.period_from</code>.
     */
    public void setPeriodFrom(LocalDate value) {
        set(16, value);
    }

    /**
     * Getter for <code>lending.invoice.period_from</code>.
     */
    public LocalDate getPeriodFrom() {
        return (LocalDate) get(16);
    }

    /**
     * Setter for <code>lending.invoice.period_to</code>.
     */
    public void setPeriodTo(LocalDate value) {
        set(17, value);
    }

    /**
     * Getter for <code>lending.invoice.period_to</code>.
     */
    public LocalDate getPeriodTo() {
        return (LocalDate) get(17);
    }

    /**
     * Setter for <code>lending.invoice.product_id</code>.
     */
    public void setProductId(Long value) {
        set(18, value);
    }

    /**
     * Getter for <code>lending.invoice.product_id</code>.
     */
    public Long getProductId() {
        return (Long) get(18);
    }

    /**
     * Setter for <code>lending.invoice.status</code>.
     */
    public void setStatus(String value) {
        set(19, value);
    }

    /**
     * Getter for <code>lending.invoice.status</code>.
     */
    public String getStatus() {
        return (String) get(19);
    }

    /**
     * Setter for <code>lending.invoice.status_detail</code>.
     */
    public void setStatusDetail(String value) {
        set(20, value);
    }

    /**
     * Getter for <code>lending.invoice.status_detail</code>.
     */
    public String getStatusDetail() {
        return (String) get(20);
    }

    /**
     * Setter for <code>lending.invoice.total</code>.
     */
    public void setTotal(BigDecimal value) {
        set(21, value);
    }

    /**
     * Getter for <code>lending.invoice.total</code>.
     */
    public BigDecimal getTotal() {
        return (BigDecimal) get(21);
    }

    /**
     * Setter for <code>lending.invoice.total_paid</code>.
     */
    public void setTotalPaid(BigDecimal value) {
        set(22, value);
    }

    /**
     * Getter for <code>lending.invoice.total_paid</code>.
     */
    public BigDecimal getTotalPaid() {
        return (BigDecimal) get(22);
    }

    /**
     * Setter for <code>lending.invoice.voided</code>.
     */
    public void setVoided(Boolean value) {
        set(23, value);
    }

    /**
     * Getter for <code>lending.invoice.voided</code>.
     */
    public Boolean getVoided() {
        return (Boolean) get(23);
    }

    /**
     * Setter for <code>lending.invoice.sent_at</code>.
     */
    public void setSentAt(LocalDateTime value) {
        set(24, value);
    }

    /**
     * Getter for <code>lending.invoice.sent_at</code>.
     */
    public LocalDateTime getSentAt() {
        return (LocalDateTime) get(24);
    }

    /**
     * Setter for <code>lending.invoice.generate_file</code>.
     */
    public void setGenerateFile(Boolean value) {
        set(25, value);
    }

    /**
     * Getter for <code>lending.invoice.generate_file</code>.
     */
    public Boolean getGenerateFile() {
        return (Boolean) get(25);
    }

    /**
     * Setter for <code>lending.invoice.send_file</code>.
     */
    public void setSendFile(Boolean value) {
        set(26, value);
    }

    /**
     * Getter for <code>lending.invoice.send_file</code>.
     */
    public Boolean getSendFile() {
        return (Boolean) get(26);
    }

    /**
     * Setter for <code>lending.invoice.membership_level_changed</code>.
     */
    public void setMembershipLevelChanged(Boolean value) {
        set(27, value);
    }

    /**
     * Getter for <code>lending.invoice.membership_level_changed</code>.
     */
    public Boolean getMembershipLevelChanged() {
        return (Boolean) get(27);
    }

    /**
     * Setter for <code>lending.invoice.manual</code>.
     */
    public void setManual(Boolean value) {
        set(28, value);
    }

    /**
     * Getter for <code>lending.invoice.manual</code>.
     */
    public Boolean getManual() {
        return (Boolean) get(28);
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
     * Create a detached InvoiceRecord
     */
    public InvoiceRecord() {
        super(Invoice.INVOICE);
    }

    /**
     * Create a detached, initialised InvoiceRecord
     */
    public InvoiceRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, Long clientId, LocalDateTime closeDate, String closeReason, Integer corrections, LocalDate dueDate, Long fileId, String fileName, LocalDate invoiceDate, Long loanId, String number, LocalDate periodFrom, LocalDate periodTo, Long productId, String status, String statusDetail, BigDecimal total, BigDecimal totalPaid, Boolean voided, LocalDateTime sentAt, Boolean generateFile, Boolean sendFile, Boolean membershipLevelChanged, Boolean manual) {
        super(Invoice.INVOICE);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, clientId);
        set(7, closeDate);
        set(8, closeReason);
        set(9, corrections);
        set(10, dueDate);
        set(11, fileId);
        set(12, fileName);
        set(13, invoiceDate);
        set(14, loanId);
        set(15, number);
        set(16, periodFrom);
        set(17, periodTo);
        set(18, productId);
        set(19, status);
        set(20, statusDetail);
        set(21, total);
        set(22, totalPaid);
        set(23, voided);
        set(24, sentAt);
        set(25, generateFile);
        set(26, sendFile);
        set(27, membershipLevelChanged);
        set(28, manual);
    }
}
