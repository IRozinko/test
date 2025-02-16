/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.lending.tables.records;


import fintech.bo.db.jooq.lending.tables.LoanDailySnapshot;

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
public class LoanDailySnapshotRecord extends UpdatableRecordImpl<LoanDailySnapshotRecord> {

    private static final long serialVersionUID = -18877999;

    /**
     * Setter for <code>lending.loan_daily_snapshot.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.cash_in</code>.
     */
    public void setCashIn(BigDecimal value) {
        set(6, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.cash_in</code>.
     */
    public BigDecimal getCashIn() {
        return (BigDecimal) get(6);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.cash_out</code>.
     */
    public void setCashOut(BigDecimal value) {
        set(7, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.cash_out</code>.
     */
    public BigDecimal getCashOut() {
        return (BigDecimal) get(7);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.client_id</code>.
     */
    public void setClientId(Long value) {
        set(8, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.client_id</code>.
     */
    public Long getClientId() {
        return (Long) get(8);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.close_date</code>.
     */
    public void setCloseDate(LocalDate value) {
        set(9, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.close_date</code>.
     */
    public LocalDate getCloseDate() {
        return (LocalDate) get(9);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.credit_limit</code>.
     */
    public void setCreditLimit(BigDecimal value) {
        set(10, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.credit_limit</code>.
     */
    public BigDecimal getCreditLimit() {
        return (BigDecimal) get(10);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.effective_from</code>.
     */
    public void setEffectiveFrom(LocalDate value) {
        set(11, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.effective_from</code>.
     */
    public LocalDate getEffectiveFrom() {
        return (LocalDate) get(11);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.effective_to</code>.
     */
    public void setEffectiveTo(LocalDate value) {
        set(12, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.effective_to</code>.
     */
    public LocalDate getEffectiveTo() {
        return (LocalDate) get(12);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.fee_applied</code>.
     */
    public void setFeeApplied(BigDecimal value) {
        set(13, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.fee_applied</code>.
     */
    public BigDecimal getFeeApplied() {
        return (BigDecimal) get(13);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.fee_due</code>.
     */
    public void setFeeDue(BigDecimal value) {
        set(14, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.fee_due</code>.
     */
    public BigDecimal getFeeDue() {
        return (BigDecimal) get(14);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.fee_outstanding</code>.
     */
    public void setFeeOutstanding(BigDecimal value) {
        set(15, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.fee_outstanding</code>.
     */
    public BigDecimal getFeeOutstanding() {
        return (BigDecimal) get(15);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.fee_paid</code>.
     */
    public void setFeePaid(BigDecimal value) {
        set(16, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.fee_paid</code>.
     */
    public BigDecimal getFeePaid() {
        return (BigDecimal) get(16);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.fee_written_off</code>.
     */
    public void setFeeWrittenOff(BigDecimal value) {
        set(17, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.fee_written_off</code>.
     */
    public BigDecimal getFeeWrittenOff() {
        return (BigDecimal) get(17);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.interest_applied</code>.
     */
    public void setInterestApplied(BigDecimal value) {
        set(18, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.interest_applied</code>.
     */
    public BigDecimal getInterestApplied() {
        return (BigDecimal) get(18);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.interest_due</code>.
     */
    public void setInterestDue(BigDecimal value) {
        set(19, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.interest_due</code>.
     */
    public BigDecimal getInterestDue() {
        return (BigDecimal) get(19);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.interest_outstanding</code>.
     */
    public void setInterestOutstanding(BigDecimal value) {
        set(20, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.interest_outstanding</code>.
     */
    public BigDecimal getInterestOutstanding() {
        return (BigDecimal) get(20);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.interest_paid</code>.
     */
    public void setInterestPaid(BigDecimal value) {
        set(21, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.interest_paid</code>.
     */
    public BigDecimal getInterestPaid() {
        return (BigDecimal) get(21);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.interest_written_off</code>.
     */
    public void setInterestWrittenOff(BigDecimal value) {
        set(22, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.interest_written_off</code>.
     */
    public BigDecimal getInterestWrittenOff() {
        return (BigDecimal) get(22);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.invoice_payment_day</code>.
     */
    public void setInvoicePaymentDay(Integer value) {
        set(23, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.invoice_payment_day</code>.
     */
    public Integer getInvoicePaymentDay() {
        return (Integer) get(23);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.issue_date</code>.
     */
    public void setIssueDate(LocalDate value) {
        set(24, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.issue_date</code>.
     */
    public LocalDate getIssueDate() {
        return (LocalDate) get(24);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.latest</code>.
     */
    public void setLatest(Boolean value) {
        set(25, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.latest</code>.
     */
    public Boolean getLatest() {
        return (Boolean) get(25);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.loan_application_id</code>.
     */
    public void setLoanApplicationId(Long value) {
        set(26, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.loan_application_id</code>.
     */
    public Long getLoanApplicationId() {
        return (Long) get(26);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.loan_id</code>.
     */
    public void setLoanId(Long value) {
        set(27, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.loan_id</code>.
     */
    public Long getLoanId() {
        return (Long) get(27);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.loans_paid</code>.
     */
    public void setLoansPaid(Long value) {
        set(28, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.loans_paid</code>.
     */
    public Long getLoansPaid() {
        return (Long) get(28);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.maturity_date</code>.
     */
    public void setMaturityDate(LocalDate value) {
        set(29, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.maturity_date</code>.
     */
    public LocalDate getMaturityDate() {
        return (LocalDate) get(29);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.max_overdue_days</code>.
     */
    public void setMaxOverdueDays(Integer value) {
        set(30, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.max_overdue_days</code>.
     */
    public Integer getMaxOverdueDays() {
        return (Integer) get(30);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.loan_number</code>.
     */
    public void setLoanNumber(String value) {
        set(31, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.loan_number</code>.
     */
    public String getLoanNumber() {
        return (String) get(31);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.overdue_days</code>.
     */
    public void setOverdueDays(Integer value) {
        set(32, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.overdue_days</code>.
     */
    public Integer getOverdueDays() {
        return (Integer) get(32);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.payment_due_date</code>.
     */
    public void setPaymentDueDate(LocalDate value) {
        set(33, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.payment_due_date</code>.
     */
    public LocalDate getPaymentDueDate() {
        return (LocalDate) get(33);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.penalty_applied</code>.
     */
    public void setPenaltyApplied(BigDecimal value) {
        set(34, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.penalty_applied</code>.
     */
    public BigDecimal getPenaltyApplied() {
        return (BigDecimal) get(34);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.penalty_due</code>.
     */
    public void setPenaltyDue(BigDecimal value) {
        set(35, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.penalty_due</code>.
     */
    public BigDecimal getPenaltyDue() {
        return (BigDecimal) get(35);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.penalty_outstanding</code>.
     */
    public void setPenaltyOutstanding(BigDecimal value) {
        set(36, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.penalty_outstanding</code>.
     */
    public BigDecimal getPenaltyOutstanding() {
        return (BigDecimal) get(36);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.penalty_paid</code>.
     */
    public void setPenaltyPaid(BigDecimal value) {
        set(37, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.penalty_paid</code>.
     */
    public BigDecimal getPenaltyPaid() {
        return (BigDecimal) get(37);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.penalty_written_off</code>.
     */
    public void setPenaltyWrittenOff(BigDecimal value) {
        set(38, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.penalty_written_off</code>.
     */
    public BigDecimal getPenaltyWrittenOff() {
        return (BigDecimal) get(38);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.principal_disbursed</code>.
     */
    public void setPrincipalDisbursed(BigDecimal value) {
        set(39, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.principal_disbursed</code>.
     */
    public BigDecimal getPrincipalDisbursed() {
        return (BigDecimal) get(39);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.principal_due</code>.
     */
    public void setPrincipalDue(BigDecimal value) {
        set(40, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.principal_due</code>.
     */
    public BigDecimal getPrincipalDue() {
        return (BigDecimal) get(40);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.principal_outstanding</code>.
     */
    public void setPrincipalOutstanding(BigDecimal value) {
        set(41, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.principal_outstanding</code>.
     */
    public BigDecimal getPrincipalOutstanding() {
        return (BigDecimal) get(41);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.principal_paid</code>.
     */
    public void setPrincipalPaid(BigDecimal value) {
        set(42, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.principal_paid</code>.
     */
    public BigDecimal getPrincipalPaid() {
        return (BigDecimal) get(42);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.principal_written_off</code>.
     */
    public void setPrincipalWrittenOff(BigDecimal value) {
        set(43, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.principal_written_off</code>.
     */
    public BigDecimal getPrincipalWrittenOff() {
        return (BigDecimal) get(43);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.product_id</code>.
     */
    public void setProductId(Long value) {
        set(44, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.product_id</code>.
     */
    public Long getProductId() {
        return (Long) get(44);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.status</code>.
     */
    public void setStatus(String value) {
        set(45, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.status</code>.
     */
    public String getStatus() {
        return (String) get(45);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.status_detail</code>.
     */
    public void setStatusDetail(String value) {
        set(46, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.status_detail</code>.
     */
    public String getStatusDetail() {
        return (String) get(46);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.total_due</code>.
     */
    public void setTotalDue(BigDecimal value) {
        set(47, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.total_due</code>.
     */
    public BigDecimal getTotalDue() {
        return (BigDecimal) get(47);
    }

    /**
     * Setter for <code>lending.loan_daily_snapshot.total_outstanding</code>.
     */
    public void setTotalOutstanding(BigDecimal value) {
        set(48, value);
    }

    /**
     * Getter for <code>lending.loan_daily_snapshot.total_outstanding</code>.
     */
    public BigDecimal getTotalOutstanding() {
        return (BigDecimal) get(48);
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
     * Create a detached LoanDailySnapshotRecord
     */
    public LoanDailySnapshotRecord() {
        super(LoanDailySnapshot.LOAN_DAILY_SNAPSHOT);
    }

    /**
     * Create a detached, initialised LoanDailySnapshotRecord
     */
    public LoanDailySnapshotRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, BigDecimal cashIn, BigDecimal cashOut, Long clientId, LocalDate closeDate, BigDecimal creditLimit, LocalDate effectiveFrom, LocalDate effectiveTo, BigDecimal feeApplied, BigDecimal feeDue, BigDecimal feeOutstanding, BigDecimal feePaid, BigDecimal feeWrittenOff, BigDecimal interestApplied, BigDecimal interestDue, BigDecimal interestOutstanding, BigDecimal interestPaid, BigDecimal interestWrittenOff, Integer invoicePaymentDay, LocalDate issueDate, Boolean latest, Long loanApplicationId, Long loanId, Long loansPaid, LocalDate maturityDate, Integer maxOverdueDays, String loanNumber, Integer overdueDays, LocalDate paymentDueDate, BigDecimal penaltyApplied, BigDecimal penaltyDue, BigDecimal penaltyOutstanding, BigDecimal penaltyPaid, BigDecimal penaltyWrittenOff, BigDecimal principalDisbursed, BigDecimal principalDue, BigDecimal principalOutstanding, BigDecimal principalPaid, BigDecimal principalWrittenOff, Long productId, String status, String statusDetail, BigDecimal totalDue, BigDecimal totalOutstanding) {
        super(LoanDailySnapshot.LOAN_DAILY_SNAPSHOT);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, cashIn);
        set(7, cashOut);
        set(8, clientId);
        set(9, closeDate);
        set(10, creditLimit);
        set(11, effectiveFrom);
        set(12, effectiveTo);
        set(13, feeApplied);
        set(14, feeDue);
        set(15, feeOutstanding);
        set(16, feePaid);
        set(17, feeWrittenOff);
        set(18, interestApplied);
        set(19, interestDue);
        set(20, interestOutstanding);
        set(21, interestPaid);
        set(22, interestWrittenOff);
        set(23, invoicePaymentDay);
        set(24, issueDate);
        set(25, latest);
        set(26, loanApplicationId);
        set(27, loanId);
        set(28, loansPaid);
        set(29, maturityDate);
        set(30, maxOverdueDays);
        set(31, loanNumber);
        set(32, overdueDays);
        set(33, paymentDueDate);
        set(34, penaltyApplied);
        set(35, penaltyDue);
        set(36, penaltyOutstanding);
        set(37, penaltyPaid);
        set(38, penaltyWrittenOff);
        set(39, principalDisbursed);
        set(40, principalDue);
        set(41, principalOutstanding);
        set(42, principalPaid);
        set(43, principalWrittenOff);
        set(44, productId);
        set(45, status);
        set(46, statusDetail);
        set(47, totalDue);
        set(48, totalOutstanding);
    }
}
