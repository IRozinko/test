/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.email.tables.records;


import fintech.bo.db.jooq.email.tables.Log;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record21;
import org.jooq.Row21;
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
public class LogRecord extends UpdatableRecordImpl<LogRecord> implements Record21<Long, LocalDateTime, String, Long, LocalDateTime, String, String, Integer, Integer, String, String, String, Integer, LocalDateTime, String, String, String, String, String, String, String> {

    private static final long serialVersionUID = -1502214123;

    /**
     * Setter for <code>email.log.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>email.log.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>email.log.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>email.log.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>email.log.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>email.log.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>email.log.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>email.log.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>email.log.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>email.log.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>email.log.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>email.log.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>email.log.attachment_file_ids</code>.
     */
    public void setAttachmentFileIds(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>email.log.attachment_file_ids</code>.
     */
    public String getAttachmentFileIds() {
        return (String) get(6);
    }

    /**
     * Setter for <code>email.log.attempt_timeout_in_seconds</code>.
     */
    public void setAttemptTimeoutInSeconds(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>email.log.attempt_timeout_in_seconds</code>.
     */
    public Integer getAttemptTimeoutInSeconds() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>email.log.attempts</code>.
     */
    public void setAttempts(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>email.log.attempts</code>.
     */
    public Integer getAttempts() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>email.log.body</code>.
     */
    public void setBody(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>email.log.body</code>.
     */
    public String getBody() {
        return (String) get(9);
    }

    /**
     * Setter for <code>email.log.error</code>.
     */
    public void setError(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>email.log.error</code>.
     */
    public String getError() {
        return (String) get(10);
    }

    /**
     * Setter for <code>email.log.send_from</code>.
     */
    public void setSendFrom(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>email.log.send_from</code>.
     */
    public String getSendFrom() {
        return (String) get(11);
    }

    /**
     * Setter for <code>email.log.max_attempts</code>.
     */
    public void setMaxAttempts(Integer value) {
        set(12, value);
    }

    /**
     * Getter for <code>email.log.max_attempts</code>.
     */
    public Integer getMaxAttempts() {
        return (Integer) get(12);
    }

    /**
     * Setter for <code>email.log.next_attempt_at</code>.
     */
    public void setNextAttemptAt(LocalDateTime value) {
        set(13, value);
    }

    /**
     * Getter for <code>email.log.next_attempt_at</code>.
     */
    public LocalDateTime getNextAttemptAt() {
        return (LocalDateTime) get(13);
    }

    /**
     * Setter for <code>email.log.provider</code>.
     */
    public void setProvider(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>email.log.provider</code>.
     */
    public String getProvider() {
        return (String) get(14);
    }

    /**
     * Setter for <code>email.log.provider_id</code>.
     */
    public void setProviderId(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>email.log.provider_id</code>.
     */
    public String getProviderId() {
        return (String) get(15);
    }

    /**
     * Setter for <code>email.log.provider_message</code>.
     */
    public void setProviderMessage(String value) {
        set(16, value);
    }

    /**
     * Getter for <code>email.log.provider_message</code>.
     */
    public String getProviderMessage() {
        return (String) get(16);
    }

    /**
     * Setter for <code>email.log.sending_status</code>.
     */
    public void setSendingStatus(String value) {
        set(17, value);
    }

    /**
     * Getter for <code>email.log.sending_status</code>.
     */
    public String getSendingStatus() {
        return (String) get(17);
    }

    /**
     * Setter for <code>email.log.subject</code>.
     */
    public void setSubject(String value) {
        set(18, value);
    }

    /**
     * Getter for <code>email.log.subject</code>.
     */
    public String getSubject() {
        return (String) get(18);
    }

    /**
     * Setter for <code>email.log.send_to</code>.
     */
    public void setSendTo(String value) {
        set(19, value);
    }

    /**
     * Getter for <code>email.log.send_to</code>.
     */
    public String getSendTo() {
        return (String) get(19);
    }

    /**
     * Setter for <code>email.log.send_from_name</code>.
     */
    public void setSendFromName(String value) {
        set(20, value);
    }

    /**
     * Getter for <code>email.log.send_from_name</code>.
     */
    public String getSendFromName() {
        return (String) get(20);
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
    // Record21 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row21<Long, LocalDateTime, String, Long, LocalDateTime, String, String, Integer, Integer, String, String, String, Integer, LocalDateTime, String, String, String, String, String, String, String> fieldsRow() {
        return (Row21) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row21<Long, LocalDateTime, String, Long, LocalDateTime, String, String, Integer, Integer, String, String, String, Integer, LocalDateTime, String, String, String, String, String, String, String> valuesRow() {
        return (Row21) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Log.LOG.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return Log.LOG.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Log.LOG.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return Log.LOG.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return Log.LOG.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Log.LOG.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Log.LOG.ATTACHMENT_FILE_IDS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field8() {
        return Log.LOG.ATTEMPT_TIMEOUT_IN_SECONDS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field9() {
        return Log.LOG.ATTEMPTS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return Log.LOG.BODY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return Log.LOG.ERROR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return Log.LOG.SEND_FROM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field13() {
        return Log.LOG.MAX_ATTEMPTS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field14() {
        return Log.LOG.NEXT_ATTEMPT_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field15() {
        return Log.LOG.PROVIDER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field16() {
        return Log.LOG.PROVIDER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field17() {
        return Log.LOG.PROVIDER_MESSAGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field18() {
        return Log.LOG.SENDING_STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field19() {
        return Log.LOG.SUBJECT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field20() {
        return Log.LOG.SEND_TO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field21() {
        return Log.LOG.SEND_FROM_NAME;
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
        return getAttachmentFileIds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value8() {
        return getAttemptTimeoutInSeconds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value9() {
        return getAttempts();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getBody();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getError();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getSendFrom();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value13() {
        return getMaxAttempts();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value14() {
        return getNextAttemptAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value15() {
        return getProvider();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value16() {
        return getProviderId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value17() {
        return getProviderMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value18() {
        return getSendingStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value19() {
        return getSubject();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value20() {
        return getSendTo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value21() {
        return getSendFromName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value7(String value) {
        setAttachmentFileIds(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value8(Integer value) {
        setAttemptTimeoutInSeconds(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value9(Integer value) {
        setAttempts(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value10(String value) {
        setBody(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value11(String value) {
        setError(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value12(String value) {
        setSendFrom(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value13(Integer value) {
        setMaxAttempts(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value14(LocalDateTime value) {
        setNextAttemptAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value15(String value) {
        setProvider(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value16(String value) {
        setProviderId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value17(String value) {
        setProviderMessage(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value18(String value) {
        setSendingStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value19(String value) {
        setSubject(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value20(String value) {
        setSendTo(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord value21(String value) {
        setSendFromName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LogRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, String value7, Integer value8, Integer value9, String value10, String value11, String value12, Integer value13, LocalDateTime value14, String value15, String value16, String value17, String value18, String value19, String value20, String value21) {
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
        value18(value18);
        value19(value19);
        value20(value20);
        value21(value21);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LogRecord
     */
    public LogRecord() {
        super(Log.LOG);
    }

    /**
     * Create a detached, initialised LogRecord
     */
    public LogRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, String attachmentFileIds, Integer attemptTimeoutInSeconds, Integer attempts, String body, String error, String sendFrom, Integer maxAttempts, LocalDateTime nextAttemptAt, String provider, String providerId, String providerMessage, String sendingStatus, String subject, String sendTo, String sendFromName) {
        super(Log.LOG);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, attachmentFileIds);
        set(7, attemptTimeoutInSeconds);
        set(8, attempts);
        set(9, body);
        set(10, error);
        set(11, sendFrom);
        set(12, maxAttempts);
        set(13, nextAttemptAt);
        set(14, provider);
        set(15, providerId);
        set(16, providerMessage);
        set(17, sendingStatus);
        set(18, subject);
        set(19, sendTo);
        set(20, sendFromName);
    }
}
