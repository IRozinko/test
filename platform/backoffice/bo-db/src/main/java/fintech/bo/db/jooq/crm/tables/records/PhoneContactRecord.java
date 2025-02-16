/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.crm.tables.records;


import fintech.bo.db.jooq.crm.tables.PhoneContact;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record17;
import org.jooq.Row17;
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
public class PhoneContactRecord extends UpdatableRecordImpl<PhoneContactRecord> implements Record17<Long, LocalDateTime, String, Long, LocalDateTime, String, String, String, String, Boolean, Boolean, LocalDateTime, Long, Boolean, LocalDate, String, Boolean> {

    private static final long serialVersionUID = -1978536726;

    /**
     * Setter for <code>crm.phone_contact.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>crm.phone_contact.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>crm.phone_contact.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>crm.phone_contact.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>crm.phone_contact.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>crm.phone_contact.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>crm.phone_contact.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>crm.phone_contact.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>crm.phone_contact.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>crm.phone_contact.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>crm.phone_contact.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>crm.phone_contact.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>crm.phone_contact.country_code</code>.
     */
    public void setCountryCode(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>crm.phone_contact.country_code</code>.
     */
    public String getCountryCode() {
        return (String) get(6);
    }

    /**
     * Setter for <code>crm.phone_contact.local_number</code>.
     */
    public void setLocalNumber(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>crm.phone_contact.local_number</code>.
     */
    public String getLocalNumber() {
        return (String) get(7);
    }

    /**
     * Setter for <code>crm.phone_contact.phone_type</code>.
     */
    public void setPhoneType(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>crm.phone_contact.phone_type</code>.
     */
    public String getPhoneType() {
        return (String) get(8);
    }

    /**
     * Setter for <code>crm.phone_contact.is_primary</code>.
     */
    public void setIsPrimary(Boolean value) {
        set(9, value);
    }

    /**
     * Getter for <code>crm.phone_contact.is_primary</code>.
     */
    public Boolean getIsPrimary() {
        return (Boolean) get(9);
    }

    /**
     * Setter for <code>crm.phone_contact.verified</code>.
     */
    public void setVerified(Boolean value) {
        set(10, value);
    }

    /**
     * Getter for <code>crm.phone_contact.verified</code>.
     */
    public Boolean getVerified() {
        return (Boolean) get(10);
    }

    /**
     * Setter for <code>crm.phone_contact.verified_at</code>.
     */
    public void setVerifiedAt(LocalDateTime value) {
        set(11, value);
    }

    /**
     * Getter for <code>crm.phone_contact.verified_at</code>.
     */
    public LocalDateTime getVerifiedAt() {
        return (LocalDateTime) get(11);
    }

    /**
     * Setter for <code>crm.phone_contact.client_id</code>.
     */
    public void setClientId(Long value) {
        set(12, value);
    }

    /**
     * Getter for <code>crm.phone_contact.client_id</code>.
     */
    public Long getClientId() {
        return (Long) get(12);
    }

    /**
     * Setter for <code>crm.phone_contact.active</code>.
     */
    public void setActive(Boolean value) {
        set(13, value);
    }

    /**
     * Getter for <code>crm.phone_contact.active</code>.
     */
    public Boolean getActive() {
        return (Boolean) get(13);
    }

    /**
     * Setter for <code>crm.phone_contact.active_till</code>.
     */
    public void setActiveTill(LocalDate value) {
        set(14, value);
    }

    /**
     * Getter for <code>crm.phone_contact.active_till</code>.
     */
    public LocalDate getActiveTill() {
        return (LocalDate) get(14);
    }

    /**
     * Setter for <code>crm.phone_contact.source</code>.
     */
    public void setSource(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>crm.phone_contact.source</code>.
     */
    public String getSource() {
        return (String) get(15);
    }

    /**
     * Setter for <code>crm.phone_contact.legal_consent</code>.
     */
    public void setLegalConsent(Boolean value) {
        set(16, value);
    }

    /**
     * Getter for <code>crm.phone_contact.legal_consent</code>.
     */
    public Boolean getLegalConsent() {
        return (Boolean) get(16);
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
    // Record17 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row17<Long, LocalDateTime, String, Long, LocalDateTime, String, String, String, String, Boolean, Boolean, LocalDateTime, Long, Boolean, LocalDate, String, Boolean> fieldsRow() {
        return (Row17) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row17<Long, LocalDateTime, String, Long, LocalDateTime, String, String, String, String, Boolean, Boolean, LocalDateTime, Long, Boolean, LocalDate, String, Boolean> valuesRow() {
        return (Row17) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return PhoneContact.PHONE_CONTACT.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return PhoneContact.PHONE_CONTACT.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return PhoneContact.PHONE_CONTACT.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return PhoneContact.PHONE_CONTACT.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return PhoneContact.PHONE_CONTACT.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return PhoneContact.PHONE_CONTACT.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return PhoneContact.PHONE_CONTACT.COUNTRY_CODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return PhoneContact.PHONE_CONTACT.LOCAL_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return PhoneContact.PHONE_CONTACT.PHONE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field10() {
        return PhoneContact.PHONE_CONTACT.IS_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field11() {
        return PhoneContact.PHONE_CONTACT.VERIFIED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field12() {
        return PhoneContact.PHONE_CONTACT.VERIFIED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field13() {
        return PhoneContact.PHONE_CONTACT.CLIENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field14() {
        return PhoneContact.PHONE_CONTACT.ACTIVE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDate> field15() {
        return PhoneContact.PHONE_CONTACT.ACTIVE_TILL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field16() {
        return PhoneContact.PHONE_CONTACT.SOURCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field17() {
        return PhoneContact.PHONE_CONTACT.LEGAL_CONSENT;
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
    public String value7() {
        return getCountryCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getLocalNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getPhoneType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value10() {
        return getIsPrimary();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value11() {
        return getVerified();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value12() {
        return getVerifiedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value13() {
        return getClientId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value14() {
        return getActive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate value15() {
        return getActiveTill();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value16() {
        return getSource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value17() {
        return getLegalConsent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value7(String value) {
        setCountryCode(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value8(String value) {
        setLocalNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value9(String value) {
        setPhoneType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value10(Boolean value) {
        setIsPrimary(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value11(Boolean value) {
        setVerified(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value12(LocalDateTime value) {
        setVerifiedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value13(Long value) {
        setClientId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value14(Boolean value) {
        setActive(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value15(LocalDate value) {
        setActiveTill(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value16(String value) {
        setSource(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord value17(Boolean value) {
        setLegalConsent(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneContactRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, String value7, String value8, String value9, Boolean value10, Boolean value11, LocalDateTime value12, Long value13, Boolean value14, LocalDate value15, String value16, Boolean value17) {
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
        value15(value15);
        value16(value16);
        value17(value17);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PhoneContactRecord
     */
    public PhoneContactRecord() {
        super(PhoneContact.PHONE_CONTACT);
    }

    /**
     * Create a detached, initialised PhoneContactRecord
     */
    public PhoneContactRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, String countryCode, String localNumber, String phoneType, Boolean isPrimary, Boolean verified, LocalDateTime verifiedAt, Long clientId, Boolean active, LocalDate activeTill, String source, Boolean legalConsent) {
        super(PhoneContact.PHONE_CONTACT);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, countryCode);
        set(7, localNumber);
        set(8, phoneType);
        set(9, isPrimary);
        set(10, verified);
        set(11, verifiedAt);
        set(12, clientId);
        set(13, active);
        set(14, activeTill);
        set(15, source);
        set(16, legalConsent);
    }
}
