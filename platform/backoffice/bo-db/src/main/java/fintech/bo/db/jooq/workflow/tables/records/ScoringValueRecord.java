/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.workflow.tables.records;


import fintech.bo.db.jooq.workflow.tables.ScoringValue;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;
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
public class ScoringValueRecord extends UpdatableRecordImpl<ScoringValueRecord> implements Record10<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, String, String, String> {

    private static final long serialVersionUID = -1507167349;

    /**
     * Setter for <code>workflow.scoring_value.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>workflow.scoring_value.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>workflow.scoring_value.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>workflow.scoring_value.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>workflow.scoring_value.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>workflow.scoring_value.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>workflow.scoring_value.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>workflow.scoring_value.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>workflow.scoring_value.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>workflow.scoring_value.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>workflow.scoring_value.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>workflow.scoring_value.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>workflow.scoring_value.workflow_id</code>.
     */
    public void setWorkflowId(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>workflow.scoring_value.workflow_id</code>.
     */
    public Long getWorkflowId() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>workflow.scoring_value.key</code>.
     */
    public void setKey(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>workflow.scoring_value.key</code>.
     */
    public String getKey() {
        return (String) get(7);
    }

    /**
     * Setter for <code>workflow.scoring_value.value</code>.
     */
    public void setValue(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>workflow.scoring_value.value</code>.
     */
    public String getValue() {
        return (String) get(8);
    }

    /**
     * Setter for <code>workflow.scoring_value.type</code>.
     */
    public void setType(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>workflow.scoring_value.type</code>.
     */
    public String getType() {
        return (String) get(9);
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
    // Record10 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, String, String, String> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, String, String, String> valuesRow() {
        return (Row10) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return ScoringValue.SCORING_VALUE.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return ScoringValue.SCORING_VALUE.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return ScoringValue.SCORING_VALUE.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return ScoringValue.SCORING_VALUE.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return ScoringValue.SCORING_VALUE.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return ScoringValue.SCORING_VALUE.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field7() {
        return ScoringValue.SCORING_VALUE.WORKFLOW_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return ScoringValue.SCORING_VALUE.KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return ScoringValue.SCORING_VALUE.VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return ScoringValue.SCORING_VALUE.TYPE;
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
        return getWorkflowId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoringValueRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoringValueRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoringValueRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoringValueRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoringValueRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoringValueRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoringValueRecord value7(Long value) {
        setWorkflowId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoringValueRecord value8(String value) {
        setKey(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoringValueRecord value9(String value) {
        setValue(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoringValueRecord value10(String value) {
        setType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoringValueRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, Long value7, String value8, String value9, String value10) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ScoringValueRecord
     */
    public ScoringValueRecord() {
        super(ScoringValue.SCORING_VALUE);
    }

    /**
     * Create a detached, initialised ScoringValueRecord
     */
    public ScoringValueRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, Long workflowId, String key, String value, String type) {
        super(ScoringValue.SCORING_VALUE);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, workflowId);
        set(7, key);
        set(8, value);
        set(9, type);
    }
}
