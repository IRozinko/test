/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.crm.tables.records;


import fintech.bo.db.jooq.crm.tables.VerifyEmailToken;

import java.time.LocalDateTime;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
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
public class VerifyEmailTokenRecord extends UpdatableRecordImpl<VerifyEmailTokenRecord> implements Record10<Long, LocalDateTime, String, Long, LocalDateTime, String, LocalDateTime, String, Boolean, Long> {

    private static final long serialVersionUID = -963202060;

    /**
     * Setter for <code>crm.verify_email_token.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>crm.verify_email_token.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>crm.verify_email_token.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>crm.verify_email_token.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>crm.verify_email_token.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>crm.verify_email_token.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>crm.verify_email_token.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>crm.verify_email_token.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>crm.verify_email_token.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>crm.verify_email_token.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>crm.verify_email_token.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>crm.verify_email_token.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>crm.verify_email_token.expires_at</code>.
     */
    public void setExpiresAt(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>crm.verify_email_token.expires_at</code>.
     */
    public LocalDateTime getExpiresAt() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>crm.verify_email_token.token</code>.
     */
    public void setToken(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>crm.verify_email_token.token</code>.
     */
    public String getToken() {
        return (String) get(7);
    }

    /**
     * Setter for <code>crm.verify_email_token.is_used</code>.
     */
    public void setIsUsed(Boolean value) {
        set(8, value);
    }

    /**
     * Getter for <code>crm.verify_email_token.is_used</code>.
     */
    public Boolean getIsUsed() {
        return (Boolean) get(8);
    }

    /**
     * Setter for <code>crm.verify_email_token.client_id</code>.
     */
    public void setClientId(Long value) {
        set(9, value);
    }

    /**
     * Getter for <code>crm.verify_email_token.client_id</code>.
     */
    public Long getClientId() {
        return (Long) get(9);
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
    public Row10<Long, LocalDateTime, String, Long, LocalDateTime, String, LocalDateTime, String, Boolean, Long> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<Long, LocalDateTime, String, Long, LocalDateTime, String, LocalDateTime, String, Boolean, Long> valuesRow() {
        return (Row10) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return VerifyEmailToken.VERIFY_EMAIL_TOKEN.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return VerifyEmailToken.VERIFY_EMAIL_TOKEN.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return VerifyEmailToken.VERIFY_EMAIL_TOKEN.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return VerifyEmailToken.VERIFY_EMAIL_TOKEN.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return VerifyEmailToken.VERIFY_EMAIL_TOKEN.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return VerifyEmailToken.VERIFY_EMAIL_TOKEN.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field7() {
        return VerifyEmailToken.VERIFY_EMAIL_TOKEN.EXPIRES_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return VerifyEmailToken.VERIFY_EMAIL_TOKEN.TOKEN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field9() {
        return VerifyEmailToken.VERIFY_EMAIL_TOKEN.IS_USED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field10() {
        return VerifyEmailToken.VERIFY_EMAIL_TOKEN.CLIENT_ID;
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
    public LocalDateTime value7() {
        return getExpiresAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getToken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value9() {
        return getIsUsed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value10() {
        return getClientId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VerifyEmailTokenRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VerifyEmailTokenRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VerifyEmailTokenRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VerifyEmailTokenRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VerifyEmailTokenRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VerifyEmailTokenRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VerifyEmailTokenRecord value7(LocalDateTime value) {
        setExpiresAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VerifyEmailTokenRecord value8(String value) {
        setToken(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VerifyEmailTokenRecord value9(Boolean value) {
        setIsUsed(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VerifyEmailTokenRecord value10(Long value) {
        setClientId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VerifyEmailTokenRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, LocalDateTime value7, String value8, Boolean value9, Long value10) {
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
     * Create a detached VerifyEmailTokenRecord
     */
    public VerifyEmailTokenRecord() {
        super(VerifyEmailToken.VERIFY_EMAIL_TOKEN);
    }

    /**
     * Create a detached, initialised VerifyEmailTokenRecord
     */
    public VerifyEmailTokenRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, LocalDateTime expiresAt, String token, Boolean isUsed, Long clientId) {
        super(VerifyEmailToken.VERIFY_EMAIL_TOKEN);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, expiresAt);
        set(7, token);
        set(8, isUsed);
        set(9, clientId);
    }
}
