/*
 * This file is generated by jOOQ.
*/
package fintech.bo.spain.alfa.db.jooq.alfa.tables.records;


import fintech.bo.spain.alfa.db.jooq.alfa.tables.WealthinessCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record12;
import org.jooq.Row12;
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
public class WealthinessCategoryRecord extends UpdatableRecordImpl<WealthinessCategoryRecord> implements Record12<Long, LocalDateTime, String, Long, LocalDateTime, String, String, BigDecimal, String, BigDecimal, BigDecimal, Long> {

    private static final long serialVersionUID = 490932600;

    /**
     * Setter for <code>alfa.wealthiness_category.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>alfa.wealthiness_category.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>alfa.wealthiness_category.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>alfa.wealthiness_category.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>alfa.wealthiness_category.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>alfa.wealthiness_category.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>alfa.wealthiness_category.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>alfa.wealthiness_category.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>alfa.wealthiness_category.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>alfa.wealthiness_category.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>alfa.wealthiness_category.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>alfa.wealthiness_category.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>alfa.wealthiness_category.category</code>.
     */
    public void setCategory(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>alfa.wealthiness_category.category</code>.
     */
    public String getCategory() {
        return (String) get(6);
    }

    /**
     * Setter for <code>alfa.wealthiness_category.manual_weighted_wealthiness</code>.
     */
    public void setManualWeightedWealthiness(BigDecimal value) {
        set(7, value);
    }

    /**
     * Getter for <code>alfa.wealthiness_category.manual_weighted_wealthiness</code>.
     */
    public BigDecimal getManualWeightedWealthiness() {
        return (BigDecimal) get(7);
    }

    /**
     * Setter for <code>alfa.wealthiness_category.nordigen_categories</code>.
     */
    public void setNordigenCategories(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>alfa.wealthiness_category.nordigen_categories</code>.
     */
    public String getNordigenCategories() {
        return (String) get(8);
    }

    /**
     * Setter for <code>alfa.wealthiness_category.nordigen_weighted_wealthiness</code>.
     */
    public void setNordigenWeightedWealthiness(BigDecimal value) {
        set(9, value);
    }

    /**
     * Getter for <code>alfa.wealthiness_category.nordigen_weighted_wealthiness</code>.
     */
    public BigDecimal getNordigenWeightedWealthiness() {
        return (BigDecimal) get(9);
    }

    /**
     * Setter for <code>alfa.wealthiness_category.weight_in_precent</code>.
     */
    public void setWeightInPrecent(BigDecimal value) {
        set(10, value);
    }

    /**
     * Getter for <code>alfa.wealthiness_category.weight_in_precent</code>.
     */
    public BigDecimal getWeightInPrecent() {
        return (BigDecimal) get(10);
    }

    /**
     * Setter for <code>alfa.wealthiness_category.wealthiness_id</code>.
     */
    public void setWealthinessId(Long value) {
        set(11, value);
    }

    /**
     * Getter for <code>alfa.wealthiness_category.wealthiness_id</code>.
     */
    public Long getWealthinessId() {
        return (Long) get(11);
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
    // Record12 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row12<Long, LocalDateTime, String, Long, LocalDateTime, String, String, BigDecimal, String, BigDecimal, BigDecimal, Long> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row12<Long, LocalDateTime, String, Long, LocalDateTime, String, String, BigDecimal, String, BigDecimal, BigDecimal, Long> valuesRow() {
        return (Row12) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return WealthinessCategory.WEALTHINESS_CATEGORY.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return WealthinessCategory.WEALTHINESS_CATEGORY.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return WealthinessCategory.WEALTHINESS_CATEGORY.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return WealthinessCategory.WEALTHINESS_CATEGORY.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return WealthinessCategory.WEALTHINESS_CATEGORY.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return WealthinessCategory.WEALTHINESS_CATEGORY.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return WealthinessCategory.WEALTHINESS_CATEGORY.CATEGORY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field8() {
        return WealthinessCategory.WEALTHINESS_CATEGORY.MANUAL_WEIGHTED_WEALTHINESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return WealthinessCategory.WEALTHINESS_CATEGORY.NORDIGEN_CATEGORIES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field10() {
        return WealthinessCategory.WEALTHINESS_CATEGORY.NORDIGEN_WEIGHTED_WEALTHINESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field11() {
        return WealthinessCategory.WEALTHINESS_CATEGORY.WEIGHT_IN_PRECENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field12() {
        return WealthinessCategory.WEALTHINESS_CATEGORY.WEALTHINESS_ID;
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
        return getCategory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value8() {
        return getManualWeightedWealthiness();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getNordigenCategories();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value10() {
        return getNordigenWeightedWealthiness();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value11() {
        return getWeightInPrecent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value12() {
        return getWealthinessId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WealthinessCategoryRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WealthinessCategoryRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WealthinessCategoryRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WealthinessCategoryRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WealthinessCategoryRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WealthinessCategoryRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WealthinessCategoryRecord value7(String value) {
        setCategory(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WealthinessCategoryRecord value8(BigDecimal value) {
        setManualWeightedWealthiness(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WealthinessCategoryRecord value9(String value) {
        setNordigenCategories(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WealthinessCategoryRecord value10(BigDecimal value) {
        setNordigenWeightedWealthiness(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WealthinessCategoryRecord value11(BigDecimal value) {
        setWeightInPrecent(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WealthinessCategoryRecord value12(Long value) {
        setWealthinessId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WealthinessCategoryRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, String value7, BigDecimal value8, String value9, BigDecimal value10, BigDecimal value11, Long value12) {
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
     * Create a detached WealthinessCategoryRecord
     */
    public WealthinessCategoryRecord() {
        super(WealthinessCategory.WEALTHINESS_CATEGORY);
    }

    /**
     * Create a detached, initialised WealthinessCategoryRecord
     */
    public WealthinessCategoryRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, String category, BigDecimal manualWeightedWealthiness, String nordigenCategories, BigDecimal nordigenWeightedWealthiness, BigDecimal weightInPrecent, Long wealthinessId) {
        super(WealthinessCategory.WEALTHINESS_CATEGORY);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, category);
        set(7, manualWeightedWealthiness);
        set(8, nordigenCategories);
        set(9, nordigenWeightedWealthiness);
        set(10, weightInPrecent);
        set(11, wealthinessId);
    }
}
