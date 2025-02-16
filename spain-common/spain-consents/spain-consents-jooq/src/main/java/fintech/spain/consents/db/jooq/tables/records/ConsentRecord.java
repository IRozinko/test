/*
 * This file is generated by jOOQ.
*/
package fintech.spain.consents.db.jooq.tables.records;


import fintech.spain.consents.db.jooq.tables.Consent;
import org.jooq.Field;
import org.jooq.Record12;
import org.jooq.Row12;
import org.jooq.impl.TableRecordImpl;

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
public class ConsentRecord extends TableRecordImpl<ConsentRecord> implements Record12<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, String, String, Boolean, String, LocalDateTime> {

    private static final long serialVersionUID = -255650812;

    /**
     * Setter for <code>spain_consents.consent.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>spain_consents.consent.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>spain_consents.consent.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>spain_consents.consent.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>spain_consents.consent.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>spain_consents.consent.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>spain_consents.consent.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>spain_consents.consent.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>spain_consents.consent.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>spain_consents.consent.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>spain_consents.consent.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>spain_consents.consent.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>spain_consents.consent.client_id</code>.
     */
    public void setClientId(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>spain_consents.consent.client_id</code>.
     */
    public Long getClientId() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>spain_consents.consent.name</code>.
     */
    public void setName(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>spain_consents.consent.name</code>.
     */
    public String getName() {
        return (String) get(7);
    }

    /**
     * Setter for <code>spain_consents.consent.version</code>.
     */
    public void setVersion(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>spain_consents.consent.version</code>.
     */
    public String getVersion() {
        return (String) get(8);
    }

    /**
     * Setter for <code>spain_consents.consent.accepted</code>.
     */
    public void setAccepted(Boolean value) {
        set(9, value);
    }

    /**
     * Getter for <code>spain_consents.consent.accepted</code>.
     */
    public Boolean getAccepted() {
        return (Boolean) get(9);
    }

    /**
     * Setter for <code>spain_consents.consent.source</code>.
     */
    public void setSource(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>spain_consents.consent.source</code>.
     */
    public String getSource() {
        return (String) get(10);
    }

    /**
     * Setter for <code>spain_consents.consent.changed_at</code>.
     */
    public void setChangedAt(LocalDateTime value) {
        set(11, value);
    }

    /**
     * Getter for <code>spain_consents.consent.changed_at</code>.
     */
    public LocalDateTime getChangedAt() {
        return (LocalDateTime) get(11);
    }

    // -------------------------------------------------------------------------
    // Record12 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row12<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, String, String, Boolean, String, LocalDateTime> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row12<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, String, String, Boolean, String, LocalDateTime> valuesRow() {
        return (Row12) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Consent.CONSENT.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return Consent.CONSENT.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Consent.CONSENT.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return Consent.CONSENT.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return Consent.CONSENT.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Consent.CONSENT.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field7() {
        return Consent.CONSENT.CLIENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return Consent.CONSENT.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Consent.CONSENT.VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field10() {
        return Consent.CONSENT.ACCEPTED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return Consent.CONSENT.SOURCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field12() {
        return Consent.CONSENT.CHANGED_AT;
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
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value10() {
        return getAccepted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getSource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value12() {
        return getChangedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsentRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsentRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsentRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsentRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsentRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsentRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsentRecord value7(Long value) {
        setClientId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsentRecord value8(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsentRecord value9(String value) {
        setVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsentRecord value10(Boolean value) {
        setAccepted(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsentRecord value11(String value) {
        setSource(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsentRecord value12(LocalDateTime value) {
        setChangedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConsentRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, Long value7, String value8, String value9, Boolean value10, String value11, LocalDateTime value12) {
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
     * Create a detached ConsentRecord
     */
    public ConsentRecord() {
        super(Consent.CONSENT);
    }

    /**
     * Create a detached, initialised ConsentRecord
     */
    public ConsentRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, Long clientId, String name, String version, Boolean accepted, String source, LocalDateTime changedAt) {
        super(Consent.CONSENT);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, clientId);
        set(7, name);
        set(8, version);
        set(9, accepted);
        set(10, source);
        set(11, changedAt);
    }
}
