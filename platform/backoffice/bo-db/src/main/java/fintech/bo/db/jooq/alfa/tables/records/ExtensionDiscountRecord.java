/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.alfa.tables.records;


import fintech.bo.db.jooq.alfa.tables.ExtensionDiscount;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class ExtensionDiscountRecord extends UpdatableRecordImpl<ExtensionDiscountRecord> implements Record11<Long, LocalDate, LocalDate, BigDecimal, Long, Boolean, Long, LocalDateTime, String, LocalDateTime, String> {

    private static final long serialVersionUID = 105593954;

    /**
     * Setter for <code>alfa.extension_discount.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>alfa.extension_discount.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>alfa.extension_discount.effective_from</code>.
     */
    public void setEffectiveFrom(LocalDate value) {
        set(1, value);
    }

    /**
     * Getter for <code>alfa.extension_discount.effective_from</code>.
     */
    public LocalDate getEffectiveFrom() {
        return (LocalDate) get(1);
    }

    /**
     * Setter for <code>alfa.extension_discount.effective_to</code>.
     */
    public void setEffectiveTo(LocalDate value) {
        set(2, value);
    }

    /**
     * Getter for <code>alfa.extension_discount.effective_to</code>.
     */
    public LocalDate getEffectiveTo() {
        return (LocalDate) get(2);
    }

    /**
     * Setter for <code>alfa.extension_discount.rate_in_percent</code>.
     */
    public void setRateInPercent(BigDecimal value) {
        set(3, value);
    }

    /**
     * Getter for <code>alfa.extension_discount.rate_in_percent</code>.
     */
    public BigDecimal getRateInPercent() {
        return (BigDecimal) get(3);
    }

    /**
     * Setter for <code>alfa.extension_discount.loan_id</code>.
     */
    public void setLoanId(Long value) {
        set(4, value);
    }

    /**
     * Getter for <code>alfa.extension_discount.loan_id</code>.
     */
    public Long getLoanId() {
        return (Long) get(4);
    }

    /**
     * Setter for <code>alfa.extension_discount.active</code>.
     */
    public void setActive(Boolean value) {
        set(5, value);
    }

    /**
     * Getter for <code>alfa.extension_discount.active</code>.
     */
    public Boolean getActive() {
        return (Boolean) get(5);
    }

    /**
     * Setter for <code>alfa.extension_discount.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>alfa.extension_discount.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>alfa.extension_discount.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>alfa.extension_discount.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(7);
    }

    /**
     * Setter for <code>alfa.extension_discount.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>alfa.extension_discount.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(8);
    }

    /**
     * Setter for <code>alfa.extension_discount.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(9, value);
    }

    /**
     * Getter for <code>alfa.extension_discount.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(9);
    }

    /**
     * Setter for <code>alfa.extension_discount.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>alfa.extension_discount.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(10);
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
    public Row11<Long, LocalDate, LocalDate, BigDecimal, Long, Boolean, Long, LocalDateTime, String, LocalDateTime, String> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row11<Long, LocalDate, LocalDate, BigDecimal, Long, Boolean, Long, LocalDateTime, String, LocalDateTime, String> valuesRow() {
        return (Row11) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return ExtensionDiscount.EXTENSION_DISCOUNT.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDate> field2() {
        return ExtensionDiscount.EXTENSION_DISCOUNT.EFFECTIVE_FROM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDate> field3() {
        return ExtensionDiscount.EXTENSION_DISCOUNT.EFFECTIVE_TO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field4() {
        return ExtensionDiscount.EXTENSION_DISCOUNT.RATE_IN_PERCENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field5() {
        return ExtensionDiscount.EXTENSION_DISCOUNT.LOAN_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field6() {
        return ExtensionDiscount.EXTENSION_DISCOUNT.ACTIVE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field7() {
        return ExtensionDiscount.EXTENSION_DISCOUNT.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field8() {
        return ExtensionDiscount.EXTENSION_DISCOUNT.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return ExtensionDiscount.EXTENSION_DISCOUNT.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field10() {
        return ExtensionDiscount.EXTENSION_DISCOUNT.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return ExtensionDiscount.EXTENSION_DISCOUNT.UPDATED_BY;
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
    public LocalDate value2() {
        return getEffectiveFrom();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate value3() {
        return getEffectiveTo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value4() {
        return getRateInPercent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value5() {
        return getLoanId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value6() {
        return getActive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value7() {
        return getEntityVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value8() {
        return getCreatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getCreatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value10() {
        return getUpdatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getUpdatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionDiscountRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionDiscountRecord value2(LocalDate value) {
        setEffectiveFrom(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionDiscountRecord value3(LocalDate value) {
        setEffectiveTo(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionDiscountRecord value4(BigDecimal value) {
        setRateInPercent(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionDiscountRecord value5(Long value) {
        setLoanId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionDiscountRecord value6(Boolean value) {
        setActive(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionDiscountRecord value7(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionDiscountRecord value8(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionDiscountRecord value9(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionDiscountRecord value10(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionDiscountRecord value11(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtensionDiscountRecord values(Long value1, LocalDate value2, LocalDate value3, BigDecimal value4, Long value5, Boolean value6, Long value7, LocalDateTime value8, String value9, LocalDateTime value10, String value11) {
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
     * Create a detached ExtensionDiscountRecord
     */
    public ExtensionDiscountRecord() {
        super(ExtensionDiscount.EXTENSION_DISCOUNT);
    }

    /**
     * Create a detached, initialised ExtensionDiscountRecord
     */
    public ExtensionDiscountRecord(Long id, LocalDate effectiveFrom, LocalDate effectiveTo, BigDecimal rateInPercent, Long loanId, Boolean active, Long entityVersion, LocalDateTime createdAt, String createdBy, LocalDateTime updatedAt, String updatedBy) {
        super(ExtensionDiscount.EXTENSION_DISCOUNT);

        set(0, id);
        set(1, effectiveFrom);
        set(2, effectiveTo);
        set(3, rateInPercent);
        set(4, loanId);
        set(5, active);
        set(6, entityVersion);
        set(7, createdAt);
        set(8, createdBy);
        set(9, updatedAt);
        set(10, updatedBy);
    }
}
