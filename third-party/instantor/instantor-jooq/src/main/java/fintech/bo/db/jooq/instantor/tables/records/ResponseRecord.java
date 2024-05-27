/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.instantor.tables.records;


import fintech.bo.db.jooq.instantor.tables.Response;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record22;
import org.jooq.Row22;
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
public class ResponseRecord extends UpdatableRecordImpl<ResponseRecord> implements Record22<Long, LocalDateTime, String, Long, LocalDateTime, String, String, Long, String, Boolean, String, String, String, String, String, String, String, String, String, String, String, String> {

    private static final long serialVersionUID = 1494940010;

    /**
     * Setter for <code>instantor.response.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>instantor.response.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>instantor.response.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>instantor.response.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>instantor.response.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>instantor.response.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>instantor.response.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>instantor.response.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>instantor.response.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>instantor.response.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>instantor.response.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>instantor.response.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>instantor.response.account_numbers</code>.
     */
    public void setAccountNumbers(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>instantor.response.account_numbers</code>.
     */
    public String getAccountNumbers() {
        return (String) get(6);
    }

    /**
     * Setter for <code>instantor.response.client_id</code>.
     */
    public void setClientId(Long value) {
        set(7, value);
    }

    /**
     * Getter for <code>instantor.response.client_id</code>.
     */
    public Long getClientId() {
        return (Long) get(7);
    }

    /**
     * Setter for <code>instantor.response.error</code>.
     */
    public void setError(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>instantor.response.error</code>.
     */
    public String getError() {
        return (String) get(8);
    }

    /**
     * Setter for <code>instantor.response.latest</code>.
     */
    public void setLatest(Boolean value) {
        set(9, value);
    }

    /**
     * Getter for <code>instantor.response.latest</code>.
     */
    public Boolean getLatest() {
        return (Boolean) get(9);
    }

    /**
     * Setter for <code>instantor.response.name_for_verification</code>.
     */
    public void setNameForVerification(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>instantor.response.name_for_verification</code>.
     */
    public String getNameForVerification() {
        return (String) get(10);
    }

    /**
     * Setter for <code>instantor.response.param_action</code>.
     */
    public void setParamAction(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>instantor.response.param_action</code>.
     */
    public String getParamAction() {
        return (String) get(11);
    }

    /**
     * Setter for <code>instantor.response.param_encryption</code>.
     */
    public void setParamEncryption(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>instantor.response.param_encryption</code>.
     */
    public String getParamEncryption() {
        return (String) get(12);
    }

    /**
     * Setter for <code>instantor.response.param_hash</code>.
     */
    public void setParamHash(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>instantor.response.param_hash</code>.
     */
    public String getParamHash() {
        return (String) get(13);
    }

    /**
     * Setter for <code>instantor.response.param_message_id</code>.
     */
    public void setParamMessageId(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>instantor.response.param_message_id</code>.
     */
    public String getParamMessageId() {
        return (String) get(14);
    }

    /**
     * Setter for <code>instantor.response.param_payload</code>.
     */
    public void setParamPayload(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>instantor.response.param_payload</code>.
     */
    public String getParamPayload() {
        return (String) get(15);
    }

    /**
     * Setter for <code>instantor.response.param_source</code>.
     */
    public void setParamSource(String value) {
        set(16, value);
    }

    /**
     * Getter for <code>instantor.response.param_source</code>.
     */
    public String getParamSource() {
        return (String) get(16);
    }

    /**
     * Setter for <code>instantor.response.param_timestamp</code>.
     */
    public void setParamTimestamp(String value) {
        set(17, value);
    }

    /**
     * Getter for <code>instantor.response.param_timestamp</code>.
     */
    public String getParamTimestamp() {
        return (String) get(17);
    }

    /**
     * Setter for <code>instantor.response.payload_json</code>.
     */
    public void setPayloadJson(String value) {
        set(18, value);
    }

    /**
     * Getter for <code>instantor.response.payload_json</code>.
     */
    public String getPayloadJson() {
        return (String) get(18);
    }

    /**
     * Setter for <code>instantor.response.personal_number_for_verification</code>.
     */
    public void setPersonalNumberForVerification(String value) {
        set(19, value);
    }

    /**
     * Getter for <code>instantor.response.personal_number_for_verification</code>.
     */
    public String getPersonalNumberForVerification() {
        return (String) get(19);
    }

    /**
     * Setter for <code>instantor.response.status</code>.
     */
    public void setStatus(String value) {
        set(20, value);
    }

    /**
     * Getter for <code>instantor.response.status</code>.
     */
    public String getStatus() {
        return (String) get(20);
    }

    /**
     * Setter for <code>instantor.response.processing_status</code>.
     */
    public void setProcessingStatus(String value) {
        set(21, value);
    }

    /**
     * Getter for <code>instantor.response.processing_status</code>.
     */
    public String getProcessingStatus() {
        return (String) get(21);
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
    // Record22 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row22<Long, LocalDateTime, String, Long, LocalDateTime, String, String, Long, String, Boolean, String, String, String, String, String, String, String, String, String, String, String, String> fieldsRow() {
        return (Row22) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row22<Long, LocalDateTime, String, Long, LocalDateTime, String, String, Long, String, Boolean, String, String, String, String, String, String, String, String, String, String, String, String> valuesRow() {
        return (Row22) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Response.RESPONSE.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return Response.RESPONSE.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Response.RESPONSE.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return Response.RESPONSE.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return Response.RESPONSE.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Response.RESPONSE.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return Response.RESPONSE.ACCOUNT_NUMBERS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field8() {
        return Response.RESPONSE.CLIENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Response.RESPONSE.ERROR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field10() {
        return Response.RESPONSE.LATEST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return Response.RESPONSE.NAME_FOR_VERIFICATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return Response.RESPONSE.PARAM_ACTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return Response.RESPONSE.PARAM_ENCRYPTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field14() {
        return Response.RESPONSE.PARAM_HASH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field15() {
        return Response.RESPONSE.PARAM_MESSAGE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field16() {
        return Response.RESPONSE.PARAM_PAYLOAD;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field17() {
        return Response.RESPONSE.PARAM_SOURCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field18() {
        return Response.RESPONSE.PARAM_TIMESTAMP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field19() {
        return Response.RESPONSE.PAYLOAD_JSON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field20() {
        return Response.RESPONSE.PERSONAL_NUMBER_FOR_VERIFICATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field21() {
        return Response.RESPONSE.STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field22() {
        return Response.RESPONSE.PROCESSING_STATUS;
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
        return getAccountNumbers();
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
        return getError();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value10() {
        return getLatest();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getNameForVerification();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getParamAction();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getParamEncryption();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value14() {
        return getParamHash();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value15() {
        return getParamMessageId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value16() {
        return getParamPayload();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value17() {
        return getParamSource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value18() {
        return getParamTimestamp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value19() {
        return getPayloadJson();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value20() {
        return getPersonalNumberForVerification();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value21() {
        return getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value22() {
        return getProcessingStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value7(String value) {
        setAccountNumbers(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value8(Long value) {
        setClientId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value9(String value) {
        setError(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value10(Boolean value) {
        setLatest(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value11(String value) {
        setNameForVerification(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value12(String value) {
        setParamAction(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value13(String value) {
        setParamEncryption(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value14(String value) {
        setParamHash(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value15(String value) {
        setParamMessageId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value16(String value) {
        setParamPayload(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value17(String value) {
        setParamSource(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value18(String value) {
        setParamTimestamp(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value19(String value) {
        setPayloadJson(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value20(String value) {
        setPersonalNumberForVerification(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value21(String value) {
        setStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord value22(String value) {
        setProcessingStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, String value7, Long value8, String value9, Boolean value10, String value11, String value12, String value13, String value14, String value15, String value16, String value17, String value18, String value19, String value20, String value21, String value22) {
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
        value22(value22);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ResponseRecord
     */
    public ResponseRecord() {
        super(Response.RESPONSE);
    }

    /**
     * Create a detached, initialised ResponseRecord
     */
    public ResponseRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, String accountNumbers, Long clientId, String error, Boolean latest, String nameForVerification, String paramAction, String paramEncryption, String paramHash, String paramMessageId, String paramPayload, String paramSource, String paramTimestamp, String payloadJson, String personalNumberForVerification, String status, String processingStatus) {
        super(Response.RESPONSE);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, accountNumbers);
        set(7, clientId);
        set(8, error);
        set(9, latest);
        set(10, nameForVerification);
        set(11, paramAction);
        set(12, paramEncryption);
        set(13, paramHash);
        set(14, paramMessageId);
        set(15, paramPayload);
        set(16, paramSource);
        set(17, paramTimestamp);
        set(18, payloadJson);
        set(19, personalNumberForVerification);
        set(20, status);
        set(21, processingStatus);
    }
}
