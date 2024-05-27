/*
 * This file is generated by jOOQ.
*/
package fintech.bo.spain.alfa.db.jooq.alfa.tables.records;


import fintech.bo.spain.alfa.db.jooq.alfa.tables.Popup;

import java.time.LocalDateTime;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Row11;
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
public class PopupRecord extends UpdatableRecordImpl<PopupRecord> implements Record11<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, String, String, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1388573025;

    /**
     * Setter for <code>alfa.popup.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>alfa.popup.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>alfa.popup.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>alfa.popup.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>alfa.popup.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>alfa.popup.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>alfa.popup.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>alfa.popup.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>alfa.popup.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>alfa.popup.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>alfa.popup.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>alfa.popup.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>alfa.popup.client_id</code>.
     */
    public void setClientId(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>alfa.popup.client_id</code>.
     */
    public Long getClientId() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>alfa.popup.popup_type</code>.
     */
    public void setPopupType(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>alfa.popup.popup_type</code>.
     */
    public String getPopupType() {
        return (String) get(7);
    }

    /**
     * Setter for <code>alfa.popup.resolution</code>.
     */
    public void setResolution(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>alfa.popup.resolution</code>.
     */
    public String getResolution() {
        return (String) get(8);
    }

    /**
     * Setter for <code>alfa.popup.resolved_at</code>.
     */
    public void setResolvedAt(LocalDateTime value) {
        set(9, value);
    }

    /**
     * Getter for <code>alfa.popup.resolved_at</code>.
     */
    public LocalDateTime getResolvedAt() {
        return (LocalDateTime) get(9);
    }

    /**
     * Setter for <code>alfa.popup.valid_until</code>.
     */
    public void setValidUntil(LocalDateTime value) {
        set(10, value);
    }

    /**
     * Getter for <code>alfa.popup.valid_until</code>.
     */
    public LocalDateTime getValidUntil() {
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
    public Row11<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row11<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, String, String, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row11) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Popup.POPUP.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return Popup.POPUP.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Popup.POPUP.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return Popup.POPUP.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return Popup.POPUP.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Popup.POPUP.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field7() {
        return Popup.POPUP.CLIENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return Popup.POPUP.POPUP_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Popup.POPUP.RESOLUTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field10() {
        return Popup.POPUP.RESOLVED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field11() {
        return Popup.POPUP.VALID_UNTIL;
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
    public String value8() {
        return getPopupType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getResolution();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value10() {
        return getResolvedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value11() {
        return getValidUntil();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PopupRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PopupRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PopupRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PopupRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PopupRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PopupRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PopupRecord value7(Long value) {
        setClientId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PopupRecord value8(String value) {
        setPopupType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PopupRecord value9(String value) {
        setResolution(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PopupRecord value10(LocalDateTime value) {
        setResolvedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PopupRecord value11(LocalDateTime value) {
        setValidUntil(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PopupRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, Long value7, String value8, String value9, LocalDateTime value10, LocalDateTime value11) {
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
     * Create a detached PopupRecord
     */
    public PopupRecord() {
        super(Popup.POPUP);
    }

    /**
     * Create a detached, initialised PopupRecord
     */
    public PopupRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, Long clientId, String popupType, String resolution, LocalDateTime resolvedAt, LocalDateTime validUntil) {
        super(Popup.POPUP);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, clientId);
        set(7, popupType);
        set(8, resolution);
        set(9, resolvedAt);
        set(10, validUntil);
    }
}
