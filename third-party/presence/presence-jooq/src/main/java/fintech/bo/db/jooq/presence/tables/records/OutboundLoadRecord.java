/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.presence.tables.records;


import fintech.bo.db.jooq.presence.tables.OutboundLoad;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Row11;
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
public class OutboundLoadRecord extends UpdatableRecordImpl<OutboundLoadRecord> implements Record11<Long, Long, LocalDateTime, LocalDateTime, String, String, Long, Long, String, String, LocalDateTime> {

    private static final long serialVersionUID = 1877281344;

    /**
     * Setter for <code>presence.outbound_load.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>presence.outbound_load.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>presence.outbound_load.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>presence.outbound_load.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>presence.outbound_load.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(2, value);
    }

    /**
     * Getter for <code>presence.outbound_load.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(2);
    }

    /**
     * Setter for <code>presence.outbound_load.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>presence.outbound_load.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(3);
    }

    /**
     * Setter for <code>presence.outbound_load.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>presence.outbound_load.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(4);
    }

    /**
     * Setter for <code>presence.outbound_load.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>presence.outbound_load.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>presence.outbound_load.load_id</code>.
     */
    public void setLoadId(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>presence.outbound_load.load_id</code>.
     */
    public Long getLoadId() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>presence.outbound_load.service_id</code>.
     */
    public void setServiceId(Long value) {
        set(7, value);
    }

    /**
     * Getter for <code>presence.outbound_load.service_id</code>.
     */
    public Long getServiceId() {
        return (Long) get(7);
    }

    /**
     * Setter for <code>presence.outbound_load.status</code>.
     */
    public void setStatus(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>presence.outbound_load.status</code>.
     */
    public String getStatus() {
        return (String) get(8);
    }

    /**
     * Setter for <code>presence.outbound_load.description</code>.
     */
    public void setDescription(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>presence.outbound_load.description</code>.
     */
    public String getDescription() {
        return (String) get(9);
    }

    /**
     * Setter for <code>presence.outbound_load.added_at</code>.
     */
    public void setAddedAt(LocalDateTime value) {
        set(10, value);
    }

    /**
     * Getter for <code>presence.outbound_load.added_at</code>.
     */
    public LocalDateTime getAddedAt() {
        return (LocalDateTime) get(10);
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
    // Record11 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row11<Long, Long, LocalDateTime, LocalDateTime, String, String, Long, Long, String, String, LocalDateTime> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row11<Long, Long, LocalDateTime, LocalDateTime, String, String, Long, Long, String, String, LocalDateTime> valuesRow() {
        return (Row11) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return OutboundLoad.OUTBOUND_LOAD.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field2() {
        return OutboundLoad.OUTBOUND_LOAD.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field3() {
        return OutboundLoad.OUTBOUND_LOAD.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field4() {
        return OutboundLoad.OUTBOUND_LOAD.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return OutboundLoad.OUTBOUND_LOAD.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return OutboundLoad.OUTBOUND_LOAD.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field7() {
        return OutboundLoad.OUTBOUND_LOAD.LOAD_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field8() {
        return OutboundLoad.OUTBOUND_LOAD.SERVICE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return OutboundLoad.OUTBOUND_LOAD.STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return OutboundLoad.OUTBOUND_LOAD.DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field11() {
        return OutboundLoad.OUTBOUND_LOAD.ADDED_AT;
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
    public Long value2() {
        return getEntityVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value3() {
        return getCreatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value4() {
        return getUpdatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getCreatedBy();
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
        return getLoadId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value8() {
        return getServiceId();
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
        return getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value11() {
        return getAddedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutboundLoadRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutboundLoadRecord value2(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutboundLoadRecord value3(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutboundLoadRecord value4(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutboundLoadRecord value5(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutboundLoadRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutboundLoadRecord value7(Long value) {
        setLoadId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutboundLoadRecord value8(Long value) {
        setServiceId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutboundLoadRecord value9(String value) {
        setStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutboundLoadRecord value10(String value) {
        setDescription(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutboundLoadRecord value11(LocalDateTime value) {
        setAddedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutboundLoadRecord values(Long value1, Long value2, LocalDateTime value3, LocalDateTime value4, String value5, String value6, Long value7, Long value8, String value9, String value10, LocalDateTime value11) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached OutboundLoadRecord
     */
    public OutboundLoadRecord() {
        super(OutboundLoad.OUTBOUND_LOAD);
    }

    /**
     * Create a detached, initialised OutboundLoadRecord
     */
    public OutboundLoadRecord(Long id, Long entityVersion, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy, Long loadId, Long serviceId, String status, String description, LocalDateTime addedAt) {
        super(OutboundLoad.OUTBOUND_LOAD);

        set(0, id);
        set(1, entityVersion);
        set(2, createdAt);
        set(3, updatedAt);
        set(4, createdBy);
        set(5, updatedBy);
        set(6, loadId);
        set(7, serviceId);
        set(8, status);
        set(9, description);
        set(10, addedAt);
    }
}
