/*
 * This file is generated by jOOQ.
*/
package fintech.spain.unnax.db.jooq.tables.records;


import fintech.spain.unnax.db.jooq.tables.CardChargeRequest;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record14;
import org.jooq.Row14;
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
public class CardChargeRequestRecord extends UpdatableRecordImpl<CardChargeRequestRecord> implements Record14<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, String, Integer, String, String, String, String, String> {

    private static final long serialVersionUID = -1701895108;

    /**
     * Setter for <code>spain_unnax.card_charge_request.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>spain_unnax.card_charge_request.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>spain_unnax.card_charge_request.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>spain_unnax.card_charge_request.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>spain_unnax.card_charge_request.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>spain_unnax.card_charge_request.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>spain_unnax.card_charge_request.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>spain_unnax.card_charge_request.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>spain_unnax.card_charge_request.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>spain_unnax.card_charge_request.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>spain_unnax.card_charge_request.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>spain_unnax.card_charge_request.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>spain_unnax.card_charge_request.client_id</code>.
     */
    public void setClientId(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>spain_unnax.card_charge_request.client_id</code>.
     */
    public Long getClientId() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>spain_unnax.card_charge_request.order_code</code>.
     */
    public void setOrderCode(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>spain_unnax.card_charge_request.order_code</code>.
     */
    public String getOrderCode() {
        return (String) get(7);
    }

    /**
     * Setter for <code>spain_unnax.card_charge_request.amount</code>.
     */
    public void setAmount(Integer value) {
        set(8, value);
    }

    /**
     * Getter for <code>spain_unnax.card_charge_request.amount</code>.
     */
    public Integer getAmount() {
        return (Integer) get(8);
    }

    /**
     * Setter for <code>spain_unnax.card_charge_request.concept</code>.
     */
    public void setConcept(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>spain_unnax.card_charge_request.concept</code>.
     */
    public String getConcept() {
        return (String) get(9);
    }

    /**
     * Setter for <code>spain_unnax.card_charge_request.card_hash</code>.
     */
    public void setCardHash(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>spain_unnax.card_charge_request.card_hash</code>.
     */
    public String getCardHash() {
        return (String) get(10);
    }

    /**
     * Setter for <code>spain_unnax.card_charge_request.card_hash_reference</code>.
     */
    public void setCardHashReference(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>spain_unnax.card_charge_request.card_hash_reference</code>.
     */
    public String getCardHashReference() {
        return (String) get(11);
    }

    /**
     * Setter for <code>spain_unnax.card_charge_request.status</code>.
     */
    public void setStatus(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>spain_unnax.card_charge_request.status</code>.
     */
    public String getStatus() {
        return (String) get(12);
    }

    /**
     * Setter for <code>spain_unnax.card_charge_request.error</code>.
     */
    public void setError(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>spain_unnax.card_charge_request.error</code>.
     */
    public String getError() {
        return (String) get(13);
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
    // Record14 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row14<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, String, Integer, String, String, String, String, String> fieldsRow() {
        return (Row14) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row14<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, String, Integer, String, String, String, String, String> valuesRow() {
        return (Row14) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return CardChargeRequest.CARD_CHARGE_REQUEST.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return CardChargeRequest.CARD_CHARGE_REQUEST.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return CardChargeRequest.CARD_CHARGE_REQUEST.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return CardChargeRequest.CARD_CHARGE_REQUEST.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return CardChargeRequest.CARD_CHARGE_REQUEST.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return CardChargeRequest.CARD_CHARGE_REQUEST.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field7() {
        return CardChargeRequest.CARD_CHARGE_REQUEST.CLIENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return CardChargeRequest.CARD_CHARGE_REQUEST.ORDER_CODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field9() {
        return CardChargeRequest.CARD_CHARGE_REQUEST.AMOUNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return CardChargeRequest.CARD_CHARGE_REQUEST.CONCEPT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return CardChargeRequest.CARD_CHARGE_REQUEST.CARD_HASH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return CardChargeRequest.CARD_CHARGE_REQUEST.CARD_HASH_REFERENCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return CardChargeRequest.CARD_CHARGE_REQUEST.STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field14() {
        return CardChargeRequest.CARD_CHARGE_REQUEST.ERROR;
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
        return getOrderCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value9() {
        return getAmount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getConcept();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getCardHash();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getCardHashReference();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value14() {
        return getError();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardChargeRequestRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardChargeRequestRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardChargeRequestRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardChargeRequestRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardChargeRequestRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardChargeRequestRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardChargeRequestRecord value7(Long value) {
        setClientId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardChargeRequestRecord value8(String value) {
        setOrderCode(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardChargeRequestRecord value9(Integer value) {
        setAmount(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardChargeRequestRecord value10(String value) {
        setConcept(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardChargeRequestRecord value11(String value) {
        setCardHash(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardChargeRequestRecord value12(String value) {
        setCardHashReference(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardChargeRequestRecord value13(String value) {
        setStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardChargeRequestRecord value14(String value) {
        setError(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CardChargeRequestRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, Long value7, String value8, Integer value9, String value10, String value11, String value12, String value13, String value14) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CardChargeRequestRecord
     */
    public CardChargeRequestRecord() {
        super(CardChargeRequest.CARD_CHARGE_REQUEST);
    }

    /**
     * Create a detached, initialised CardChargeRequestRecord
     */
    public CardChargeRequestRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, Long clientId, String orderCode, Integer amount, String concept, String cardHash, String cardHashReference, String status, String error) {
        super(CardChargeRequest.CARD_CHARGE_REQUEST);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, clientId);
        set(7, orderCode);
        set(8, amount);
        set(9, concept);
        set(10, cardHash);
        set(11, cardHashReference);
        set(12, status);
        set(13, error);
    }
}
