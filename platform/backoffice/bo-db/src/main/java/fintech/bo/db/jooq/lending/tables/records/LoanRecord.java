/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.lending.tables.records;


import fintech.bo.db.jooq.lending.tables.Loan;
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
public class LoanRecord extends UpdatableRecordImpl<LoanRecord> {

    private static final long serialVersionUID = -1463398359;

    /**
     * Setter for <code>lending.loan.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>lending.loan.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>lending.loan.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>lending.loan.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>lending.loan.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>lending.loan.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>lending.loan.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>lending.loan.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>lending.loan.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>lending.loan.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>lending.loan.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>lending.loan.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>lending.loan.cash_in</code>.
     */
    public void setCashIn(BigDecimal value) {
        set(6, value);
    }

    /**
     * Getter for <code>lending.loan.cash_in</code>.
     */
    public BigDecimal getCashIn() {
        return (BigDecimal) get(6);
    }

    /**
     * Setter for <code>lending.loan.cash_out</code>.
     */
    public void setCashOut(BigDecimal value) {
        set(7, value);
    }

    /**
     * Getter for <code>lending.loan.cash_out</code>.
     */
    public BigDecimal getCashOut() {
        return (BigDecimal) get(7);
    }

    /**
     * Setter for <code>lending.loan.client_id</code>.
     */
    public void setClientId(Long value) {
        set(8, value);
    }

    /**
     * Getter for <code>lending.loan.client_id</code>.
     */
    public Long getClientId() {
        return (Long) get(8);
    }

    /**
     * Setter for <code>lending.loan.close_date</code>.
     */
    public void setCloseDate(LocalDate value) {
        set(9, value);
    }

    /**
     * Getter for <code>lending.loan.close_date</code>.
     */
    public LocalDate getCloseDate() {
        return (LocalDate) get(9);
    }

    /**
     * Setter for <code>lending.loan.credit_limit</code>.
     */
    public void setCreditLimit(BigDecimal value) {
        set(10, value);
    }

    /**
     * Getter for <code>lending.loan.credit_limit</code>.
     */
    public BigDecimal getCreditLimit() {
        return (BigDecimal) get(10);
    }

    /**
     * Setter for <code>lending.loan.fee_applied</code>.
     */
    public void setFeeApplied(BigDecimal value) {
        set(11, value);
    }

    /**
     * Getter for <code>lending.loan.fee_applied</code>.
     */
    public BigDecimal getFeeApplied() {
        return (BigDecimal) get(11);
    }

    /**
     * Setter for <code>lending.loan.fee_due</code>.
     */
    public void setFeeDue(BigDecimal value) {
        set(12, value);
    }

    /**
     * Getter for <code>lending.loan.fee_due</code>.
     */
    public BigDecimal getFeeDue() {
        return (BigDecimal) get(12);
    }

    /**
     * Setter for <code>lending.loan.fee_outstanding</code>.
     */
    public void setFeeOutstanding(BigDecimal value) {
        set(13, value);
    }

    /**
     * Getter for <code>lending.loan.fee_outstanding</code>.
     */
    public BigDecimal getFeeOutstanding() {
        return (BigDecimal) get(13);
    }

    /**
     * Setter for <code>lending.loan.fee_paid</code>.
     */
    public void setFeePaid(BigDecimal value) {
        set(14, value);
    }

    /**
     * Getter for <code>lending.loan.fee_paid</code>.
     */
    public BigDecimal getFeePaid() {
        return (BigDecimal) get(14);
    }

    /**
     * Setter for <code>lending.loan.fee_written_off</code>.
     */
    public void setFeeWrittenOff(BigDecimal value) {
        set(15, value);
    }

    /**
     * Getter for <code>lending.loan.fee_written_off</code>.
     */
    public BigDecimal getFeeWrittenOff() {
        return (BigDecimal) get(15);
    }

    /**
     * Setter for <code>lending.loan.interest_applied</code>.
     */
    public void setInterestApplied(BigDecimal value) {
        set(16, value);
    }

    /**
     * Getter for <code>lending.loan.interest_applied</code>.
     */
    public BigDecimal getInterestApplied() {
        return (BigDecimal) get(16);
    }

    /**
     * Setter for <code>lending.loan.interest_due</code>.
     */
    public void setInterestDue(BigDecimal value) {
        set(17, value);
    }

    /**
     * Getter for <code>lending.loan.interest_due</code>.
     */
    public BigDecimal getInterestDue() {
        return (BigDecimal) get(17);
    }

    /**
     * Setter for <code>lending.loan.interest_outstanding</code>.
     */
    public void setInterestOutstanding(BigDecimal value) {
        set(18, value);
    }

    /**
     * Getter for <code>lending.loan.interest_outstanding</code>.
     */
    public BigDecimal getInterestOutstanding() {
        return (BigDecimal) get(18);
    }

    /**
     * Setter for <code>lending.loan.interest_paid</code>.
     */
    public void setInterestPaid(BigDecimal value) {
        set(19, value);
    }

    /**
     * Getter for <code>lending.loan.interest_paid</code>.
     */
    public BigDecimal getInterestPaid() {
        return (BigDecimal) get(19);
    }

    /**
     * Setter for <code>lending.loan.interest_written_off</code>.
     */
    public void setInterestWrittenOff(BigDecimal value) {
        set(20, value);
    }

    /**
     * Getter for <code>lending.loan.interest_written_off</code>.
     */
    public BigDecimal getInterestWrittenOff() {
        return (BigDecimal) get(20);
    }

    /**
     * Setter for <code>lending.loan.invoice_payment_day</code>.
     */
    public void setInvoicePaymentDay(Integer value) {
        set(21, value);
    }

    /**
     * Getter for <code>lending.loan.invoice_payment_day</code>.
     */
    public Integer getInvoicePaymentDay() {
        return (Integer) get(21);
    }

    /**
     * Setter for <code>lending.loan.issue_date</code>.
     */
    public void setIssueDate(LocalDate value) {
        set(22, value);
    }

    /**
     * Getter for <code>lending.loan.issue_date</code>.
     */
    public LocalDate getIssueDate() {
        return (LocalDate) get(22);
    }

    /**
     * Setter for <code>lending.loan.loan_application_id</code>.
     */
    public void setLoanApplicationId(Long value) {
        set(23, value);
    }

    /**
     * Getter for <code>lending.loan.loan_application_id</code>.
     */
    public Long getLoanApplicationId() {
        return (Long) get(23);
    }

    /**
     * Setter for <code>lending.loan.loans_paid</code>.
     */
    public void setLoansPaid(Long value) {
        set(24, value);
    }

    /**
     * Getter for <code>lending.loan.loans_paid</code>.
     */
    public Long getLoansPaid() {
        return (Long) get(24);
    }

    /**
     * Setter for <code>lending.loan.maturity_date</code>.
     */
    public void setMaturityDate(LocalDate value) {
        set(25, value);
    }

    /**
     * Getter for <code>lending.loan.maturity_date</code>.
     */
    public LocalDate getMaturityDate() {
        return (LocalDate) get(25);
    }

    /**
     * Setter for <code>lending.loan.max_overdue_days</code>.
     */
    public void setMaxOverdueDays(Integer value) {
        set(26, value);
    }

    /**
     * Getter for <code>lending.loan.max_overdue_days</code>.
     */
    public Integer getMaxOverdueDays() {
        return (Integer) get(26);
    }

    /**
     * Setter for <code>lending.loan.loan_number</code>.
     */
    public void setLoanNumber(String value) {
        set(27, value);
    }

    /**
     * Getter for <code>lending.loan.loan_number</code>.
     */
    public String getLoanNumber() {
        return (String) get(27);
    }

    /**
     * Setter for <code>lending.loan.overdue_days</code>.
     */
    public void setOverdueDays(Integer value) {
        set(28, value);
    }

    /**
     * Getter for <code>lending.loan.overdue_days</code>.
     */
    public Integer getOverdueDays() {
        return (Integer) get(28);
    }

    /**
     * Setter for <code>lending.loan.payment_due_date</code>.
     */
    public void setPaymentDueDate(LocalDate value) {
        set(29, value);
    }

    /**
     * Getter for <code>lending.loan.payment_due_date</code>.
     */
    public LocalDate getPaymentDueDate() {
        return (LocalDate) get(29);
    }

    /**
     * Setter for <code>lending.loan.penalty_applied</code>.
     */
    public void setPenaltyApplied(BigDecimal value) {
        set(30, value);
    }

    /**
     * Getter for <code>lending.loan.penalty_applied</code>.
     */
    public BigDecimal getPenaltyApplied() {
        return (BigDecimal) get(30);
    }

    /**
     * Setter for <code>lending.loan.penalty_due</code>.
     */
    public void setPenaltyDue(BigDecimal value) {
        set(31, value);
    }

    /**
     * Getter for <code>lending.loan.penalty_due</code>.
     */
    public BigDecimal getPenaltyDue() {
        return (BigDecimal) get(31);
    }

    /**
     * Setter for <code>lending.loan.penalty_outstanding</code>.
     */
    public void setPenaltyOutstanding(BigDecimal value) {
        set(32, value);
    }

    /**
     * Getter for <code>lending.loan.penalty_outstanding</code>.
     */
    public BigDecimal getPenaltyOutstanding() {
        return (BigDecimal) get(32);
    }

    /**
     * Setter for <code>lending.loan.penalty_paid</code>.
     */
    public void setPenaltyPaid(BigDecimal value) {
        set(33, value);
    }

    /**
     * Getter for <code>lending.loan.penalty_paid</code>.
     */
    public BigDecimal getPenaltyPaid() {
        return (BigDecimal) get(33);
    }

    /**
     * Setter for <code>lending.loan.penalty_written_off</code>.
     */
    public void setPenaltyWrittenOff(BigDecimal value) {
        set(34, value);
    }

    /**
     * Getter for <code>lending.loan.penalty_written_off</code>.
     */
    public BigDecimal getPenaltyWrittenOff() {
        return (BigDecimal) get(34);
    }

    /**
     * Setter for <code>lending.loan.principal_disbursed</code>.
     */
    public void setPrincipalDisbursed(BigDecimal value) {
        set(35, value);
    }

    /**
     * Getter for <code>lending.loan.principal_disbursed</code>.
     */
    public BigDecimal getPrincipalDisbursed() {
        return (BigDecimal) get(35);
    }

    /**
     * Setter for <code>lending.loan.principal_due</code>.
     */
    public void setPrincipalDue(BigDecimal value) {
        set(36, value);
    }

    /**
     * Getter for <code>lending.loan.principal_due</code>.
     */
    public BigDecimal getPrincipalDue() {
        return (BigDecimal) get(36);
    }

    /**
     * Setter for <code>lending.loan.principal_outstanding</code>.
     */
    public void setPrincipalOutstanding(BigDecimal value) {
        set(37, value);
    }

    /**
     * Getter for <code>lending.loan.principal_outstanding</code>.
     */
    public BigDecimal getPrincipalOutstanding() {
        return (BigDecimal) get(37);
    }

    /**
     * Setter for <code>lending.loan.principal_paid</code>.
     */
    public void setPrincipalPaid(BigDecimal value) {
        set(38, value);
    }

    /**
     * Getter for <code>lending.loan.principal_paid</code>.
     */
    public BigDecimal getPrincipalPaid() {
        return (BigDecimal) get(38);
    }

    /**
     * Setter for <code>lending.loan.principal_written_off</code>.
     */
    public void setPrincipalWrittenOff(BigDecimal value) {
        set(39, value);
    }

    /**
     * Getter for <code>lending.loan.principal_written_off</code>.
     */
    public BigDecimal getPrincipalWrittenOff() {
        return (BigDecimal) get(39);
    }

    /**
     * Setter for <code>lending.loan.product_id</code>.
     */
    public void setProductId(Long value) {
        set(40, value);
    }

    /**
     * Getter for <code>lending.loan.product_id</code>.
     */
    public Long getProductId() {
        return (Long) get(40);
    }

    /**
     * Setter for <code>lending.loan.status</code>.
     */
    public void setStatus(String value) {
        set(41, value);
    }

    /**
     * Getter for <code>lending.loan.status</code>.
     */
    public String getStatus() {
        return (String) get(41);
    }

    /**
     * Setter for <code>lending.loan.status_detail</code>.
     */
    public void setStatusDetail(String value) {
        set(42, value);
    }

    /**
     * Getter for <code>lending.loan.status_detail</code>.
     */
    public String getStatusDetail() {
        return (String) get(42);
    }

    /**
     * Setter for <code>lending.loan.total_due</code>.
     */
    public void setTotalDue(BigDecimal value) {
        set(43, value);
    }

    /**
     * Getter for <code>lending.loan.total_due</code>.
     */
    public BigDecimal getTotalDue() {
        return (BigDecimal) get(43);
    }

    /**
     * Setter for <code>lending.loan.total_outstanding</code>.
     */
    public void setTotalOutstanding(BigDecimal value) {
        set(44, value);
    }

    /**
     * Getter for <code>lending.loan.total_outstanding</code>.
     */
    public BigDecimal getTotalOutstanding() {
        return (BigDecimal) get(44);
    }

    /**
     * Setter for <code>lending.loan.overpayment_received</code>.
     */
    public void setOverpaymentReceived(BigDecimal value) {
        set(45, value);
    }

    /**
     * Getter for <code>lending.loan.overpayment_received</code>.
     */
    public BigDecimal getOverpaymentReceived() {
        return (BigDecimal) get(45);
    }

    /**
     * Setter for <code>lending.loan.overpayment_refunded</code>.
     */
    public void setOverpaymentRefunded(BigDecimal value) {
        set(46, value);
    }

    /**
     * Getter for <code>lending.loan.overpayment_refunded</code>.
     */
    public BigDecimal getOverpaymentRefunded() {
        return (BigDecimal) get(46);
    }

    /**
     * Setter for <code>lending.loan.overpayment_used</code>.
     */
    public void setOverpaymentUsed(BigDecimal value) {
        set(47, value);
    }

    /**
     * Getter for <code>lending.loan.overpayment_used</code>.
     */
    public BigDecimal getOverpaymentUsed() {
        return (BigDecimal) get(47);
    }

    /**
     * Setter for <code>lending.loan.overpayment_available</code>.
     */
    public void setOverpaymentAvailable(BigDecimal value) {
        set(48, value);
    }

    /**
     * Getter for <code>lending.loan.overpayment_available</code>.
     */
    public BigDecimal getOverpaymentAvailable() {
        return (BigDecimal) get(48);
    }

    /**
     * Setter for <code>lending.loan.broken_date</code>.
     */
    public void setBrokenDate(LocalDate value) {
        set(49, value);
    }

    /**
     * Getter for <code>lending.loan.broken_date</code>.
     */
    public LocalDate getBrokenDate() {
        return (LocalDate) get(49);
    }

    /**
     * Setter for <code>lending.loan.rescheduled_date</code>.
     */
    public void setRescheduledDate(LocalDate value) {
        set(50, value);
    }

    /**
     * Getter for <code>lending.loan.rescheduled_date</code>.
     */
    public LocalDate getRescheduledDate() {
        return (LocalDate) get(50);
    }

    /**
     * Setter for <code>lending.loan.reschedule_broken_date</code>.
     */
    public void setRescheduleBrokenDate(LocalDate value) {
        set(51, value);
    }

    /**
     * Getter for <code>lending.loan.reschedule_broken_date</code>.
     */
    public LocalDate getRescheduleBrokenDate() {
        return (LocalDate) get(51);
    }

    /**
     * Setter for <code>lending.loan.moved_to_legal_date</code>.
     */
    public void setMovedToLegalDate(LocalDate value) {
        set(52, value);
    }

    /**
     * Getter for <code>lending.loan.moved_to_legal_date</code>.
     */
    public LocalDate getMovedToLegalDate() {
        return (LocalDate) get(52);
    }

    /**
     * Setter for <code>lending.loan.extensions</code>.
     */
    public void setExtensions(Integer value) {
        set(53, value);
    }

    /**
     * Getter for <code>lending.loan.extensions</code>.
     */
    public Integer getExtensions() {
        return (Integer) get(53);
    }

    /**
     * Setter for <code>lending.loan.extended_by_days</code>.
     */
    public void setExtendedByDays(Integer value) {
        set(54, value);
    }

    /**
     * Getter for <code>lending.loan.extended_by_days</code>.
     */
    public Integer getExtendedByDays() {
        return (Integer) get(54);
    }

    /**
     * Setter for <code>lending.loan.period_count</code>.
     */
    public void setPeriodCount(Long value) {
        set(55, value);
    }

    /**
     * Getter for <code>lending.loan.period_count</code>.
     */
    public Long getPeriodCount() {
        return (Long) get(55);
    }

    /**
     * Setter for <code>lending.loan.period_unit</code>.
     */
    public void setPeriodUnit(String value) {
        set(56, value);
    }

    /**
     * Getter for <code>lending.loan.period_unit</code>.
     */
    public String getPeriodUnit() {
        return (String) get(56);
    }

    /**
     * Setter for <code>lending.loan.interest_discount_percent</code>.
     */
    public void setInterestDiscountPercent(BigDecimal value) {
        set(57, value);
    }

    /**
     * Getter for <code>lending.loan.interest_discount_percent</code>.
     */
    public BigDecimal getInterestDiscountPercent() {
        return (BigDecimal) get(57);
    }

    /**
     * Setter for <code>lending.loan.interest_discount_amount</code>.
     */
    public void setInterestDiscountAmount(BigDecimal value) {
        set(58, value);
    }

    /**
     * Getter for <code>lending.loan.interest_discount_amount</code>.
     */
    public BigDecimal getInterestDiscountAmount() {
        return (BigDecimal) get(58);
    }

    /**
     * Setter for <code>lending.loan.penalty_suspended</code>.
     */
    public void setPenaltySuspended(Boolean value) {
        set(59, value);
    }

    /**
     * Getter for <code>lending.loan.penalty_suspended</code>.
     */
    public Boolean getPenaltySuspended() {
        return (Boolean) get(59);
    }

    /**
     * Setter for <code>lending.loan.discount_id</code>.
     */
    public void setDiscountId(Long value) {
        set(60, value);
    }

    /**
     * Getter for <code>lending.loan.discount_id</code>.
     */
    public Long getDiscountId() {
        return (Long) get(60);
    }

    /**
     * Setter for <code>lending.loan.principal_granted</code>.
     */
    public void setPrincipalGranted(BigDecimal value) {
        set(61, value);
    }

    /**
     * Getter for <code>lending.loan.principal_granted</code>.
     */
    public BigDecimal getPrincipalGranted() {
        return (BigDecimal) get(61);
    }

    /**
     * Setter for <code>lending.loan.credit_limit_available</code>.
     */
    public void setCreditLimitAvailable(BigDecimal value) {
        set(62, value);
    }

    /**
     * Getter for <code>lending.loan.credit_limit_available</code>.
     */
    public BigDecimal getCreditLimitAvailable() {
        return (BigDecimal) get(62);
    }

    /**
     * Setter for <code>lending.loan.credit_limit_awarded</code>.
     */
    public void setCreditLimitAwarded(BigDecimal value) {
        set(63, value);
    }

    /**
     * Getter for <code>lending.loan.credit_limit_awarded</code>.
     */
    public BigDecimal getCreditLimitAwarded() {
        return (BigDecimal) get(63);
    }

    /**
     * Setter for <code>lending.loan.first_disbursement_date</code>.
     */
    public void setFirstDisbursementDate(LocalDate value) {
        set(64, value);
    }

    /**
     * Getter for <code>lending.loan.first_disbursement_date</code>.
     */
    public LocalDate getFirstDisbursementDate() {
        return (LocalDate) get(64);
    }

    /**
     * Setter for <code>lending.loan.interest_strategy_id</code>.
     */
    public void setInterestStrategyId(Long value) {
        set(65, value);
    }

    /**
     * Getter for <code>lending.loan.interest_strategy_id</code>.
     */
    public Long getInterestStrategyId() {
        return (Long) get(65);
    }

    /**
     * Setter for <code>lending.loan.penalty_strategy_id</code>.
     */
    public void setPenaltyStrategyId(Long value) {
        set(66, value);
    }

    /**
     * Getter for <code>lending.loan.penalty_strategy_id</code>.
     */
    public Long getPenaltyStrategyId() {
        return (Long) get(66);
    }

    /**
     * Setter for <code>lending.loan.extension_strategy_id</code>.
     */
    public void setExtensionStrategyId(Long value) {
        set(67, value);
    }

    /**
     * Getter for <code>lending.loan.extension_strategy_id</code>.
     */
    public Long getExtensionStrategyId() {
        return (Long) get(67);
    }

    /**
     * Setter for <code>lending.loan.fee_strategy_id</code>.
     */
    public void setFeeStrategyId(Long value) {
        set(68, value);
    }

    /**
     * Getter for <code>lending.loan.fee_strategy_id</code>.
     */
    public Long getFeeStrategyId() {
        return (Long) get(68);
    }

    /**
     * Setter for <code>lending.loan.promo_code_id</code>.
     */
    public void setPromoCodeId(Long value) {
        set(69, value);
    }

    /**
     * Getter for <code>lending.loan.promo_code_id</code>.
     */
    public Long getPromoCodeId() {
        return (Long) get(69);
    }

    /**
     * Setter for <code>lending.loan.compliant_with_aemip</code>.
     */
    public void setCompliantWithAemip(Boolean value) {
        set(70, value);
    }

    /**
     * Getter for <code>lending.loan.compliant_with_aemip</code>.
     */
    public Boolean getCompliantWithAemip() {
        return (Boolean) get(70);
    }

    /**
     * Setter for <code>lending.loan.reason_for_break</code>.
     */
    public void setReasonForBreak(String value) {
        set(71, value);
    }

    /**
     * Getter for <code>lending.loan.reason_for_break</code>.
     */
    public String getReasonForBreak() {
        return (String) get(71);
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
     * Create a detached LoanRecord
     */
    public LoanRecord() {
        super(Loan.LOAN);
    }

    /**
     * Create a detached, initialised LoanRecord
     */
    public LoanRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, BigDecimal cashIn, BigDecimal cashOut, Long clientId, LocalDate closeDate, BigDecimal creditLimit, BigDecimal feeApplied, BigDecimal feeDue, BigDecimal feeOutstanding, BigDecimal feePaid, BigDecimal feeWrittenOff, BigDecimal interestApplied, BigDecimal interestDue, BigDecimal interestOutstanding, BigDecimal interestPaid, BigDecimal interestWrittenOff, Integer invoicePaymentDay, LocalDate issueDate, Long loanApplicationId, Long loansPaid, LocalDate maturityDate, Integer maxOverdueDays, String loanNumber, Integer overdueDays, LocalDate paymentDueDate, BigDecimal penaltyApplied, BigDecimal penaltyDue, BigDecimal penaltyOutstanding, BigDecimal penaltyPaid, BigDecimal penaltyWrittenOff, BigDecimal principalDisbursed, BigDecimal principalDue, BigDecimal principalOutstanding, BigDecimal principalPaid, BigDecimal principalWrittenOff, Long productId, String status, String statusDetail, BigDecimal totalDue, BigDecimal totalOutstanding, BigDecimal overpaymentReceived, BigDecimal overpaymentRefunded, BigDecimal overpaymentUsed, BigDecimal overpaymentAvailable, LocalDate brokenDate, LocalDate rescheduledDate, LocalDate rescheduleBrokenDate, LocalDate movedToLegalDate, Integer extensions, Integer extendedByDays, Long periodCount, String periodUnit, BigDecimal interestDiscountPercent, BigDecimal interestDiscountAmount, Boolean penaltySuspended, Long discountId, BigDecimal principalGranted, BigDecimal creditLimitAvailable, BigDecimal creditLimitAwarded, LocalDate firstDisbursementDate, Long interestStrategyId, Long penaltyStrategyId, Long extensionStrategyId, Long feeStrategyId, Long promoCodeId, Boolean compliantWithAemip, String reasonForBreak) {
        super(Loan.LOAN);

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
        set(11, feeApplied);
        set(12, feeDue);
        set(13, feeOutstanding);
        set(14, feePaid);
        set(15, feeWrittenOff);
        set(16, interestApplied);
        set(17, interestDue);
        set(18, interestOutstanding);
        set(19, interestPaid);
        set(20, interestWrittenOff);
        set(21, invoicePaymentDay);
        set(22, issueDate);
        set(23, loanApplicationId);
        set(24, loansPaid);
        set(25, maturityDate);
        set(26, maxOverdueDays);
        set(27, loanNumber);
        set(28, overdueDays);
        set(29, paymentDueDate);
        set(30, penaltyApplied);
        set(31, penaltyDue);
        set(32, penaltyOutstanding);
        set(33, penaltyPaid);
        set(34, penaltyWrittenOff);
        set(35, principalDisbursed);
        set(36, principalDue);
        set(37, principalOutstanding);
        set(38, principalPaid);
        set(39, principalWrittenOff);
        set(40, productId);
        set(41, status);
        set(42, statusDetail);
        set(43, totalDue);
        set(44, totalOutstanding);
        set(45, overpaymentReceived);
        set(46, overpaymentRefunded);
        set(47, overpaymentUsed);
        set(48, overpaymentAvailable);
        set(49, brokenDate);
        set(50, rescheduledDate);
        set(51, rescheduleBrokenDate);
        set(52, movedToLegalDate);
        set(53, extensions);
        set(54, extendedByDays);
        set(55, periodCount);
        set(56, periodUnit);
        set(57, interestDiscountPercent);
        set(58, interestDiscountAmount);
        set(59, penaltySuspended);
        set(60, discountId);
        set(61, principalGranted);
        set(62, creditLimitAvailable);
        set(63, creditLimitAwarded);
        set(64, firstDisbursementDate);
        set(65, interestStrategyId);
        set(66, penaltyStrategyId);
        set(67, extensionStrategyId);
        set(68, feeStrategyId);
        set(69, promoCodeId);
        set(70, compliantWithAemip);
        set(71, reasonForBreak);
    }
}
