/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.lending.tables.records;


import fintech.bo.db.jooq.lending.tables.PromoCodeClient;

import java.time.LocalDateTime;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
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
public class PromoCodeClientRecord extends UpdatableRecordImpl<PromoCodeClientRecord> implements Record8<Long, Long, String, Long, LocalDateTime, String, LocalDateTime, String> {

    private static final long serialVersionUID = -1683740105;

    /**
     * Setter for <code>lending.promo_code_client.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>lending.promo_code_client.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>lending.promo_code_client.promo_code_id</code>.
     */
    public void setPromoCodeId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>lending.promo_code_client.promo_code_id</code>.
     */
    public Long getPromoCodeId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>lending.promo_code_client.client_number</code>.
     */
    public void setClientNumber(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>lending.promo_code_client.client_number</code>.
     */
    public String getClientNumber() {
        return (String) get(2);
    }

    /**
     * Setter for <code>lending.promo_code_client.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>lending.promo_code_client.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>lending.promo_code_client.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>lending.promo_code_client.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>lending.promo_code_client.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>lending.promo_code_client.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>lending.promo_code_client.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>lending.promo_code_client.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>lending.promo_code_client.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>lending.promo_code_client.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(7);
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
    // Record8 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<Long, Long, String, Long, LocalDateTime, String, LocalDateTime, String> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<Long, Long, String, Long, LocalDateTime, String, LocalDateTime, String> valuesRow() {
        return (Row8) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return PromoCodeClient.PROMO_CODE_CLIENT.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field2() {
        return PromoCodeClient.PROMO_CODE_CLIENT.PROMO_CODE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return PromoCodeClient.PROMO_CODE_CLIENT.CLIENT_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return PromoCodeClient.PROMO_CODE_CLIENT.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return PromoCodeClient.PROMO_CODE_CLIENT.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return PromoCodeClient.PROMO_CODE_CLIENT.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field7() {
        return PromoCodeClient.PROMO_CODE_CLIENT.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return PromoCodeClient.PROMO_CODE_CLIENT.UPDATED_BY;
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
        return getPromoCodeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getClientNumber();
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
        return getCreatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getCreatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value7() {
        return getUpdatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getUpdatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PromoCodeClientRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PromoCodeClientRecord value2(Long value) {
        setPromoCodeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PromoCodeClientRecord value3(String value) {
        setClientNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PromoCodeClientRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PromoCodeClientRecord value5(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PromoCodeClientRecord value6(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PromoCodeClientRecord value7(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PromoCodeClientRecord value8(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PromoCodeClientRecord values(Long value1, Long value2, String value3, Long value4, LocalDateTime value5, String value6, LocalDateTime value7, String value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PromoCodeClientRecord
     */
    public PromoCodeClientRecord() {
        super(PromoCodeClient.PROMO_CODE_CLIENT);
    }

    /**
     * Create a detached, initialised PromoCodeClientRecord
     */
    public PromoCodeClientRecord(Long id, Long promoCodeId, String clientNumber, Long entityVersion, LocalDateTime createdAt, String createdBy, LocalDateTime updatedAt, String updatedBy) {
        super(PromoCodeClient.PROMO_CODE_CLIENT);

        set(0, id);
        set(1, promoCodeId);
        set(2, clientNumber);
        set(3, entityVersion);
        set(4, createdAt);
        set(5, createdBy);
        set(6, updatedAt);
        set(7, updatedBy);
    }
}
