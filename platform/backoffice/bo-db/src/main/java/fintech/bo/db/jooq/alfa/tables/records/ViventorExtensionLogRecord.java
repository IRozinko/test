/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.alfa.tables.records;


import fintech.bo.db.jooq.alfa.tables.ViventorExtensionLog;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record14;
import org.jooq.Row14;
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
public class ViventorExtensionLogRecord extends UpdatableRecordImpl<ViventorExtensionLogRecord> implements Record14<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, String, LocalDate, LocalDate, Long, BigDecimal, BigDecimal, LocalDate> {

    private static final long serialVersionUID = -193028432;

    /**
     * Setter for <code>alfa.viventor_extension_log.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>alfa.viventor_extension_log.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>alfa.viventor_extension_log.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>alfa.viventor_extension_log.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>alfa.viventor_extension_log.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>alfa.viventor_extension_log.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>alfa.viventor_extension_log.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>alfa.viventor_extension_log.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>alfa.viventor_extension_log.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>alfa.viventor_extension_log.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>alfa.viventor_extension_log.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>alfa.viventor_extension_log.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>alfa.viventor_extension_log.loan_id</code>.
     */
    public void setLoanId(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>alfa.viventor_extension_log.loan_id</code>.
     */
    public Long getLoanId() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>alfa.viventor_extension_log.viventor_loan_id</code>.
     */
    public void setViventorLoanId(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>alfa.viventor_extension_log.viventor_loan_id</code>.
     */
    public String getViventorLoanId() {
        return (String) get(7);
    }

    /**
     * Setter for <code>alfa.viventor_extension_log.local_maturity_date</code>.
     */
    public void setLocalMaturityDate(LocalDate value) {
        set(8, value);
    }

    /**
     * Getter for <code>alfa.viventor_extension_log.local_maturity_date</code>.
     */
    public LocalDate getLocalMaturityDate() {
        return (LocalDate) get(8);
    }

    /**
     * Setter for <code>alfa.viventor_extension_log.viventor_maturity_date</code>.
     */
    public void setViventorMaturityDate(LocalDate value) {
        set(9, value);
    }

    /**
     * Getter for <code>alfa.viventor_extension_log.viventor_maturity_date</code>.
     */
    public LocalDate getViventorMaturityDate() {
        return (LocalDate) get(9);
    }

    /**
     * Setter for <code>alfa.viventor_extension_log.extension_term_days</code>.
     */
    public void setExtensionTermDays(Long value) {
        set(10, value);
    }

    /**
     * Getter for <code>alfa.viventor_extension_log.extension_term_days</code>.
     */
    public Long getExtensionTermDays() {
        return (Long) get(10);
    }

    /**
     * Setter for <code>alfa.viventor_extension_log.principal</code>.
     */
    public void setPrincipal(BigDecimal value) {
        set(11, value);
    }

    /**
     * Getter for <code>alfa.viventor_extension_log.principal</code>.
     */
    public BigDecimal getPrincipal() {
        return (BigDecimal) get(11);
    }

    /**
     * Setter for <code>alfa.viventor_extension_log.interest_rate</code>.
     */
    public void setInterestRate(BigDecimal value) {
        set(12, value);
    }

    /**
     * Getter for <code>alfa.viventor_extension_log.interest_rate</code>.
     */
    public BigDecimal getInterestRate() {
        return (BigDecimal) get(12);
    }

    /**
     * Setter for <code>alfa.viventor_extension_log.start_date</code>.
     */
    public void setStartDate(LocalDate value) {
        set(13, value);
    }

    /**
     * Getter for <code>alfa.viventor_extension_log.start_date</code>.
     */
    public LocalDate getStartDate() {
        return (LocalDate) get(13);
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
    // Record14 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row14<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, String, LocalDate, LocalDate, Long, BigDecimal, BigDecimal, LocalDate> fieldsRow() {
        return (Row14) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row14<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, String, LocalDate, LocalDate, Long, BigDecimal, BigDecimal, LocalDate> valuesRow() {
        return (Row14) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return ViventorExtensionLog.VIVENTOR_EXTENSION_LOG.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return ViventorExtensionLog.VIVENTOR_EXTENSION_LOG.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return ViventorExtensionLog.VIVENTOR_EXTENSION_LOG.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return ViventorExtensionLog.VIVENTOR_EXTENSION_LOG.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return ViventorExtensionLog.VIVENTOR_EXTENSION_LOG.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return ViventorExtensionLog.VIVENTOR_EXTENSION_LOG.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field7() {
        return ViventorExtensionLog.VIVENTOR_EXTENSION_LOG.LOAN_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return ViventorExtensionLog.VIVENTOR_EXTENSION_LOG.VIVENTOR_LOAN_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDate> field9() {
        return ViventorExtensionLog.VIVENTOR_EXTENSION_LOG.LOCAL_MATURITY_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDate> field10() {
        return ViventorExtensionLog.VIVENTOR_EXTENSION_LOG.VIVENTOR_MATURITY_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field11() {
        return ViventorExtensionLog.VIVENTOR_EXTENSION_LOG.EXTENSION_TERM_DAYS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field12() {
        return ViventorExtensionLog.VIVENTOR_EXTENSION_LOG.PRINCIPAL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field13() {
        return ViventorExtensionLog.VIVENTOR_EXTENSION_LOG.INTEREST_RATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDate> field14() {
        return ViventorExtensionLog.VIVENTOR_EXTENSION_LOG.START_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value2() {
        return getCreatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getCreatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value4() {
        return getEntityVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value5() {
        return getUpdatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getUpdatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value7() {
        return getLoanId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getViventorLoanId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate value9() {
        return getLocalMaturityDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate value10() {
        return getViventorMaturityDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value11() {
        return getExtensionTermDays();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value12() {
        return getPrincipal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value13() {
        return getInterestRate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate value14() {
        return getStartDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViventorExtensionLogRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViventorExtensionLogRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViventorExtensionLogRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViventorExtensionLogRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViventorExtensionLogRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViventorExtensionLogRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViventorExtensionLogRecord value7(Long value) {
        setLoanId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViventorExtensionLogRecord value8(String value) {
        setViventorLoanId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViventorExtensionLogRecord value9(LocalDate value) {
        setLocalMaturityDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViventorExtensionLogRecord value10(LocalDate value) {
        setViventorMaturityDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViventorExtensionLogRecord value11(Long value) {
        setExtensionTermDays(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViventorExtensionLogRecord value12(BigDecimal value) {
        setPrincipal(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViventorExtensionLogRecord value13(BigDecimal value) {
        setInterestRate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViventorExtensionLogRecord value14(LocalDate value) {
        setStartDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViventorExtensionLogRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, Long value7, String value8, LocalDate value9, LocalDate value10, Long value11, BigDecimal value12, BigDecimal value13, LocalDate value14) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ViventorExtensionLogRecord
     */
    public ViventorExtensionLogRecord() {
        super(ViventorExtensionLog.VIVENTOR_EXTENSION_LOG);
    }

    /**
     * Create a detached, initialised ViventorExtensionLogRecord
     */
    public ViventorExtensionLogRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, Long loanId, String viventorLoanId, LocalDate localMaturityDate, LocalDate viventorMaturityDate, Long extensionTermDays, BigDecimal principal, BigDecimal interestRate, LocalDate startDate) {
        super(ViventorExtensionLog.VIVENTOR_EXTENSION_LOG);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, loanId);
        set(7, viventorLoanId);
        set(8, localMaturityDate);
        set(9, viventorMaturityDate);
        set(10, extensionTermDays);
        set(11, principal);
        set(12, interestRate);
        set(13, startDate);
    }
}
