/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.cms.tables.records;


import fintech.bo.db.jooq.cms.tables.Locale;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.TableRecordImpl;

import javax.annotation.Generated;


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
public class LocaleRecord extends TableRecordImpl<LocaleRecord> implements Record2<String, Boolean> {

    private static final long serialVersionUID = 1334635236;

    /**
     * Setter for <code>cms.locale.locale</code>.
     */
    public void setLocale(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>cms.locale.locale</code>.
     */
    public String getLocale() {
        return (String) get(0);
    }

    /**
     * Setter for <code>cms.locale.is_default</code>.
     */
    public void setIsDefault(Boolean value) {
        set(1, value);
    }

    /**
     * Getter for <code>cms.locale.is_default</code>.
     */
    public Boolean getIsDefault() {
        return (Boolean) get(1);
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<String, Boolean> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<String, Boolean> valuesRow() {
        return (Row2) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return Locale.LOCALE.LOCALE_;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field2() {
        return Locale.LOCALE.IS_DEFAULT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getLocale();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value2() {
        return getIsDefault();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocaleRecord value1(String value) {
        setLocale(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocaleRecord value2(Boolean value) {
        setIsDefault(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocaleRecord values(String value1, Boolean value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached LocaleRecord
     */
    public LocaleRecord() {
        super(Locale.LOCALE);
    }

    /**
     * Create a detached, initialised LocaleRecord
     */
    public LocaleRecord(String locale, Boolean isDefault) {
        super(Locale.LOCALE);

        set(0, locale);
        set(1, isDefault);
    }
}
