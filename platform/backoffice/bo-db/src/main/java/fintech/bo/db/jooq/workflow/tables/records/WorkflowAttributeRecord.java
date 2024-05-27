/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.workflow.tables.records;


import fintech.bo.db.jooq.workflow.tables.WorkflowAttribute;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Row3;
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
public class WorkflowAttributeRecord extends UpdatableRecordImpl<WorkflowAttributeRecord> implements Record3<Long, String, String> {

    private static final long serialVersionUID = -1031818224;

    /**
     * Setter for <code>workflow.workflow_attribute.workflow_id</code>.
     */
    public void setWorkflowId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>workflow.workflow_attribute.workflow_id</code>.
     */
    public Long getWorkflowId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>workflow.workflow_attribute.value</code>.
     */
    public void setValue(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>workflow.workflow_attribute.value</code>.
     */
    public String getValue() {
        return (String) get(1);
    }

    /**
     * Setter for <code>workflow.workflow_attribute.key</code>.
     */
    public void setKey(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>workflow.workflow_attribute.key</code>.
     */
    public String getKey() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record2<Long, String> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Long, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Long, String, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return WorkflowAttribute.WORKFLOW_ATTRIBUTE.WORKFLOW_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return WorkflowAttribute.WORKFLOW_ATTRIBUTE.VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return WorkflowAttribute.WORKFLOW_ATTRIBUTE.KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getWorkflowId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowAttributeRecord value1(Long value) {
        setWorkflowId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowAttributeRecord value2(String value) {
        setValue(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowAttributeRecord value3(String value) {
        setKey(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowAttributeRecord values(Long value1, String value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached WorkflowAttributeRecord
     */
    public WorkflowAttributeRecord() {
        super(WorkflowAttribute.WORKFLOW_ATTRIBUTE);
    }

    /**
     * Create a detached, initialised WorkflowAttributeRecord
     */
    public WorkflowAttributeRecord(Long workflowId, String value, String key) {
        super(WorkflowAttribute.WORKFLOW_ATTRIBUTE);

        set(0, workflowId);
        set(1, value);
        set(2, key);
    }
}
