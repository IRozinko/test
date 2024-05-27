/*
 * This file is generated by jOOQ.
*/
package fintech.bo.spain.alfa.db.jooq.alfa.tables.records;


import fintech.bo.spain.alfa.db.jooq.alfa.tables.LocBatch;

import java.time.LocalDateTime;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record12;
import org.jooq.Row12;
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
public class LocBatchRecord extends UpdatableRecordImpl<LocBatchRecord> implements Record12<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, Long, String, String, Long, String> {

    private static final long serialVersionUID = -580899783;

    /**
     * Setter for <code>alfa.loc_batch.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>alfa.loc_batch.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>alfa.loc_batch.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>alfa.loc_batch.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>alfa.loc_batch.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>alfa.loc_batch.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>alfa.loc_batch.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>alfa.loc_batch.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>alfa.loc_batch.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>alfa.loc_batch.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>alfa.loc_batch.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>alfa.loc_batch.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>alfa.loc_batch.client_id</code>.
     */
    public void setClientId(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>alfa.loc_batch.client_id</code>.
     */
    public Long getClientId() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>alfa.loc_batch.batch_number</code>.
     */
    public void setBatchNumber(Long value) {
        set(7, value);
    }

    /**
     * Getter for <code>alfa.loc_batch.batch_number</code>.
     */
    public Long getBatchNumber() {
        return (Long) get(7);
    }

    /**
     * Setter for <code>alfa.loc_batch.status</code>.
     */
    public void setStatus(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>alfa.loc_batch.status</code>.
     */
    public String getStatus() {
        return (String) get(8);
    }

    /**
     * Setter for <code>alfa.loc_batch.client_number</code>.
     */
    public void setClientNumber(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>alfa.loc_batch.client_number</code>.
     */
    public String getClientNumber() {
        return (String) get(9);
    }

    /**
     * Setter for <code>alfa.loc_batch.application_id</code>.
     */
    public void setApplicationId(Long value) {
        set(10, value);
    }

    /**
     * Getter for <code>alfa.loc_batch.application_id</code>.
     */
    public Long getApplicationId() {
        return (Long) get(10);
    }

    /**
     * Setter for <code>alfa.loc_batch.status_detail</code>.
     */
    public void setStatusDetail(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>alfa.loc_batch.status_detail</code>.
     */
    public String getStatusDetail() {
        return (String) get(11);
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
    // Record12 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row12<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, Long, String, String, Long, String> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row12<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, Long, String, String, Long, String> valuesRow() {
        return (Row12) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return LocBatch.LOC_BATCH.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return LocBatch.LOC_BATCH.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return LocBatch.LOC_BATCH.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return LocBatch.LOC_BATCH.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return LocBatch.LOC_BATCH.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return LocBatch.LOC_BATCH.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field7() {
        return LocBatch.LOC_BATCH.CLIENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field8() {
        return LocBatch.LOC_BATCH.BATCH_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return LocBatch.LOC_BATCH.STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return LocBatch.LOC_BATCH.CLIENT_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field11() {
        return LocBatch.LOC_BATCH.APPLICATION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return LocBatch.LOC_BATCH.STATUS_DETAIL;
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
        return getClientId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value8() {
        return getBatchNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getClientNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value11() {
        return getApplicationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getStatusDetail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocBatchRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocBatchRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocBatchRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocBatchRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocBatchRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocBatchRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocBatchRecord value7(Long value) {
        setClientId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocBatchRecord value8(Long value) {
        setBatchNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocBatchRecord value9(String value) {
        setStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocBatchRecord value10(String value) {
        setClientNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocBatchRecord value11(Long value) {
        setApplicationId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocBatchRecord value12(String value) {
        setStatusDetail(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocBatchRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, Long value7, Long value8, String value9, String value10, Long value11, String value12) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LocBatchRecord
     */
    public LocBatchRecord() {
        super(LocBatch.LOC_BATCH);
    }

    /**
     * Create a detached, initialised LocBatchRecord
     */
    public LocBatchRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, Long clientId, Long batchNumber, String status, String clientNumber, Long applicationId, String statusDetail) {
        super(LocBatch.LOC_BATCH);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, clientId);
        set(7, batchNumber);
        set(8, status);
        set(9, clientNumber);
        set(10, applicationId);
        set(11, statusDetail);
    }
}
