/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.strategy.tables.records;


import fintech.bo.db.jooq.strategy.tables.CalculationStrategy;

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
public class CalculationStrategyRecord extends UpdatableRecordImpl<CalculationStrategyRecord> implements Record11<Long, Long, LocalDateTime, String, LocalDateTime, String, String, String, String, Boolean, Boolean> {

    private static final long serialVersionUID = 853540993;

    /**
     * Setter for <code>strategy.calculation_strategy.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>strategy.calculation_strategy.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>strategy.calculation_strategy.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>strategy.calculation_strategy.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>strategy.calculation_strategy.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(2, value);
    }

    /**
     * Getter for <code>strategy.calculation_strategy.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(2);
    }

    /**
     * Setter for <code>strategy.calculation_strategy.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>strategy.calculation_strategy.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(3);
    }

    /**
     * Setter for <code>strategy.calculation_strategy.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>strategy.calculation_strategy.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>strategy.calculation_strategy.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>strategy.calculation_strategy.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>strategy.calculation_strategy.strategy_type</code>.
     */
    public void setStrategyType(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>strategy.calculation_strategy.strategy_type</code>.
     */
    public String getStrategyType() {
        return (String) get(6);
    }

    /**
     * Setter for <code>strategy.calculation_strategy.calculation_type</code>.
     */
    public void setCalculationType(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>strategy.calculation_strategy.calculation_type</code>.
     */
    public String getCalculationType() {
        return (String) get(7);
    }

    /**
     * Setter for <code>strategy.calculation_strategy.version</code>.
     */
    public void setVersion(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>strategy.calculation_strategy.version</code>.
     */
    public String getVersion() {
        return (String) get(8);
    }

    /**
     * Setter for <code>strategy.calculation_strategy.enabled</code>.
     */
    public void setEnabled(Boolean value) {
        set(9, value);
    }

    /**
     * Getter for <code>strategy.calculation_strategy.enabled</code>.
     */
    public Boolean getEnabled() {
        return (Boolean) get(9);
    }

    /**
     * Setter for <code>strategy.calculation_strategy.is_default</code>.
     */
    public void setIsDefault(Boolean value) {
        set(10, value);
    }

    /**
     * Getter for <code>strategy.calculation_strategy.is_default</code>.
     */
    public Boolean getIsDefault() {
        return (Boolean) get(10);
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
    public Row11<Long, Long, LocalDateTime, String, LocalDateTime, String, String, String, String, Boolean, Boolean> fieldsRow() {
        return (Row11) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row11<Long, Long, LocalDateTime, String, LocalDateTime, String, String, String, String, Boolean, Boolean> valuesRow() {
        return (Row11) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return CalculationStrategy.CALCULATION_STRATEGY.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field2() {
        return CalculationStrategy.CALCULATION_STRATEGY.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field3() {
        return CalculationStrategy.CALCULATION_STRATEGY.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return CalculationStrategy.CALCULATION_STRATEGY.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return CalculationStrategy.CALCULATION_STRATEGY.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return CalculationStrategy.CALCULATION_STRATEGY.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return CalculationStrategy.CALCULATION_STRATEGY.STRATEGY_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return CalculationStrategy.CALCULATION_STRATEGY.CALCULATION_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return CalculationStrategy.CALCULATION_STRATEGY.VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field10() {
        return CalculationStrategy.CALCULATION_STRATEGY.ENABLED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field11() {
        return CalculationStrategy.CALCULATION_STRATEGY.IS_DEFAULT;
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
    public String value4() {
        return getCreatedBy();
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
        return getStrategyType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getCalculationType();
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
        return getEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value11() {
        return getIsDefault();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationStrategyRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationStrategyRecord value2(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationStrategyRecord value3(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationStrategyRecord value4(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationStrategyRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationStrategyRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationStrategyRecord value7(String value) {
        setStrategyType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationStrategyRecord value8(String value) {
        setCalculationType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationStrategyRecord value9(String value) {
        setVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationStrategyRecord value10(Boolean value) {
        setEnabled(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationStrategyRecord value11(Boolean value) {
        setIsDefault(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationStrategyRecord values(Long value1, Long value2, LocalDateTime value3, String value4, LocalDateTime value5, String value6, String value7, String value8, String value9, Boolean value10, Boolean value11) {
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
     * Create a detached CalculationStrategyRecord
     */
    public CalculationStrategyRecord() {
        super(CalculationStrategy.CALCULATION_STRATEGY);
    }

    /**
     * Create a detached, initialised CalculationStrategyRecord
     */
    public CalculationStrategyRecord(Long id, Long entityVersion, LocalDateTime createdAt, String createdBy, LocalDateTime updatedAt, String updatedBy, String strategyType, String calculationType, String version, Boolean enabled, Boolean isDefault) {
        super(CalculationStrategy.CALCULATION_STRATEGY);

        set(0, id);
        set(1, entityVersion);
        set(2, createdAt);
        set(3, createdBy);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, strategyType);
        set(7, calculationType);
        set(8, version);
        set(9, enabled);
        set(10, isDefault);
    }
}
