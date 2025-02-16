/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.rule.tables.records;


import fintech.bo.db.jooq.rule.tables.RuleSetLog;

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
public class RuleSetLogRecord extends UpdatableRecordImpl<RuleSetLogRecord> implements Record14<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, Long, String, LocalDateTime, Long, String, String, String> {

    private static final long serialVersionUID = -296440375;

    /**
     * Setter for <code>rule.rule_set_log.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>rule.rule_set_log.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>rule.rule_set_log.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>rule.rule_set_log.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>rule.rule_set_log.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>rule.rule_set_log.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>rule.rule_set_log.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>rule.rule_set_log.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>rule.rule_set_log.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>rule.rule_set_log.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>rule.rule_set_log.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>rule.rule_set_log.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>rule.rule_set_log.application_id</code>.
     */
    public void setApplicationId(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>rule.rule_set_log.application_id</code>.
     */
    public Long getApplicationId() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>rule.rule_set_log.client_id</code>.
     */
    public void setClientId(Long value) {
        set(7, value);
    }

    /**
     * Getter for <code>rule.rule_set_log.client_id</code>.
     */
    public Long getClientId() {
        return (Long) get(7);
    }

    /**
     * Setter for <code>rule.rule_set_log.decision</code>.
     */
    public void setDecision(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>rule.rule_set_log.decision</code>.
     */
    public String getDecision() {
        return (String) get(8);
    }

    /**
     * Setter for <code>rule.rule_set_log.executed_at</code>.
     */
    public void setExecutedAt(LocalDateTime value) {
        set(9, value);
    }

    /**
     * Getter for <code>rule.rule_set_log.executed_at</code>.
     */
    public LocalDateTime getExecutedAt() {
        return (LocalDateTime) get(9);
    }

    /**
     * Setter for <code>rule.rule_set_log.loan_id</code>.
     */
    public void setLoanId(Long value) {
        set(10, value);
    }

    /**
     * Getter for <code>rule.rule_set_log.loan_id</code>.
     */
    public Long getLoanId() {
        return (Long) get(10);
    }

    /**
     * Setter for <code>rule.rule_set_log.reject_reason</code>.
     */
    public void setRejectReason(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>rule.rule_set_log.reject_reason</code>.
     */
    public String getRejectReason() {
        return (String) get(11);
    }

    /**
     * Setter for <code>rule.rule_set_log.reject_reason_details</code>.
     */
    public void setRejectReasonDetails(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>rule.rule_set_log.reject_reason_details</code>.
     */
    public String getRejectReasonDetails() {
        return (String) get(12);
    }

    /**
     * Setter for <code>rule.rule_set_log.rule_set</code>.
     */
    public void setRuleSet(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>rule.rule_set_log.rule_set</code>.
     */
    public String getRuleSet() {
        return (String) get(13);
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
    public Row14<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, Long, String, LocalDateTime, Long, String, String, String> fieldsRow() {
        return (Row14) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row14<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, Long, String, LocalDateTime, Long, String, String, String> valuesRow() {
        return (Row14) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return RuleSetLog.RULE_SET_LOG.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return RuleSetLog.RULE_SET_LOG.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return RuleSetLog.RULE_SET_LOG.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return RuleSetLog.RULE_SET_LOG.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return RuleSetLog.RULE_SET_LOG.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return RuleSetLog.RULE_SET_LOG.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field7() {
        return RuleSetLog.RULE_SET_LOG.APPLICATION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field8() {
        return RuleSetLog.RULE_SET_LOG.CLIENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return RuleSetLog.RULE_SET_LOG.DECISION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field10() {
        return RuleSetLog.RULE_SET_LOG.EXECUTED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field11() {
        return RuleSetLog.RULE_SET_LOG.LOAN_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return RuleSetLog.RULE_SET_LOG.REJECT_REASON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return RuleSetLog.RULE_SET_LOG.REJECT_REASON_DETAILS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field14() {
        return RuleSetLog.RULE_SET_LOG.RULE_SET;
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
        return getApplicationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value8() {
        return getClientId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getDecision();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value10() {
        return getExecutedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value11() {
        return getLoanId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getRejectReason();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getRejectReasonDetails();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value14() {
        return getRuleSet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleSetLogRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleSetLogRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleSetLogRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleSetLogRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleSetLogRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleSetLogRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleSetLogRecord value7(Long value) {
        setApplicationId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleSetLogRecord value8(Long value) {
        setClientId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleSetLogRecord value9(String value) {
        setDecision(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleSetLogRecord value10(LocalDateTime value) {
        setExecutedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleSetLogRecord value11(Long value) {
        setLoanId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleSetLogRecord value12(String value) {
        setRejectReason(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleSetLogRecord value13(String value) {
        setRejectReasonDetails(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleSetLogRecord value14(String value) {
        setRuleSet(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RuleSetLogRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, Long value7, Long value8, String value9, LocalDateTime value10, Long value11, String value12, String value13, String value14) {
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
     * Create a detached RuleSetLogRecord
     */
    public RuleSetLogRecord() {
        super(RuleSetLog.RULE_SET_LOG);
    }

    /**
     * Create a detached, initialised RuleSetLogRecord
     */
    public RuleSetLogRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, Long applicationId, Long clientId, String decision, LocalDateTime executedAt, Long loanId, String rejectReason, String rejectReasonDetails, String ruleSet) {
        super(RuleSetLog.RULE_SET_LOG);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, applicationId);
        set(7, clientId);
        set(8, decision);
        set(9, executedAt);
        set(10, loanId);
        set(11, rejectReason);
        set(12, rejectReasonDetails);
        set(13, ruleSet);
    }
}
