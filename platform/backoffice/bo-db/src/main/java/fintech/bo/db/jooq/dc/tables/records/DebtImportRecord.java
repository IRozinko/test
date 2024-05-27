/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.dc.tables.records;


import fintech.bo.db.jooq.dc.tables.DebtImport;

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
public class DebtImportRecord extends UpdatableRecordImpl<DebtImportRecord> implements Record10<Long, LocalDateTime, String, Long, LocalDateTime, String, String, String, Boolean, String> {

    private static final long serialVersionUID = -581949805;

    /**
     * Setter for <code>dc.debt_import.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>dc.debt_import.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>dc.debt_import.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>dc.debt_import.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>dc.debt_import.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>dc.debt_import.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>dc.debt_import.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>dc.debt_import.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>dc.debt_import.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>dc.debt_import.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>dc.debt_import.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>dc.debt_import.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>dc.debt_import.name</code>.
     */
    public void setName(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>dc.debt_import.name</code>.
     */
    public String getName() {
        return (String) get(6);
    }

    /**
     * Setter for <code>dc.debt_import.debt_import_format</code>.
     */
    public void setDebtImportFormat(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>dc.debt_import.debt_import_format</code>.
     */
    public String getDebtImportFormat() {
        return (String) get(7);
    }

    /**
     * Setter for <code>dc.debt_import.disabled</code>.
     */
    public void setDisabled(Boolean value) {
        set(8, value);
    }

    /**
     * Getter for <code>dc.debt_import.disabled</code>.
     */
    public Boolean getDisabled() {
        return (Boolean) get(8);
    }

    /**
     * Setter for <code>dc.debt_import.code</code>.
     */
    public void setCode(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>dc.debt_import.code</code>.
     */
    public String getCode() {
        return (String) get(9);
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
    public Row10<Long, LocalDateTime, String, Long, LocalDateTime, String, String, String, Boolean, String> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<Long, LocalDateTime, String, Long, LocalDateTime, String, String, String, Boolean, String> valuesRow() {
        return (Row10) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return DebtImport.DEBT_IMPORT.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return DebtImport.DEBT_IMPORT.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return DebtImport.DEBT_IMPORT.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return DebtImport.DEBT_IMPORT.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return DebtImport.DEBT_IMPORT.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return DebtImport.DEBT_IMPORT.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return DebtImport.DEBT_IMPORT.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return DebtImport.DEBT_IMPORT.DEBT_IMPORT_FORMAT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field9() {
        return DebtImport.DEBT_IMPORT.DISABLED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return DebtImport.DEBT_IMPORT.CODE;
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
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getDebtImportFormat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value9() {
        return getDisabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DebtImportRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DebtImportRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DebtImportRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DebtImportRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DebtImportRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DebtImportRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DebtImportRecord value7(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DebtImportRecord value8(String value) {
        setDebtImportFormat(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DebtImportRecord value9(Boolean value) {
        setDisabled(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DebtImportRecord value10(String value) {
        setCode(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DebtImportRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, String value7, String value8, Boolean value9, String value10) {
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
     * Create a detached DebtImportRecord
     */
    public DebtImportRecord() {
        super(DebtImport.DEBT_IMPORT);
    }

    /**
     * Create a detached, initialised DebtImportRecord
     */
    public DebtImportRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, String name, String debtImportFormat, Boolean disabled, String code) {
        super(DebtImport.DEBT_IMPORT);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, name);
        set(7, debtImportFormat);
        set(8, disabled);
        set(9, code);
    }
}
