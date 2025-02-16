/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.crm.tables.records;


import fintech.bo.db.jooq.crm.tables.Country;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
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
public class CountryRecord extends UpdatableRecordImpl<CountryRecord> implements Record7<Long, String, String, String, String, String, Boolean> {

    private static final long serialVersionUID = 880633706;

    /**
     * Setter for <code>crm.country.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>crm.country.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>crm.country.name</code>.
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>crm.country.name</code>.
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>crm.country.display_name</code>.
     */
    public void setDisplayName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>crm.country.display_name</code>.
     */
    public String getDisplayName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>crm.country.nationality</code>.
     */
    public void setNationality(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>crm.country.nationality</code>.
     */
    public String getNationality() {
        return (String) get(3);
    }

    /**
     * Setter for <code>crm.country.nationality_display_name</code>.
     */
    public void setNationalityDisplayName(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>crm.country.nationality_display_name</code>.
     */
    public String getNationalityDisplayName() {
        return (String) get(4);
    }

    /**
     * Setter for <code>crm.country.code</code>.
     */
    public void setCode(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>crm.country.code</code>.
     */
    public String getCode() {
        return (String) get(5);
    }

    /**
     * Setter for <code>crm.country.home_country</code>.
     */
    public void setHomeCountry(Boolean value) {
        set(6, value);
    }

    /**
     * Getter for <code>crm.country.home_country</code>.
     */
    public Boolean getHomeCountry() {
        return (Boolean) get(6);
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
    // Record7 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<Long, String, String, String, String, String, Boolean> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<Long, String, String, String, String, String, Boolean> valuesRow() {
        return (Row7) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Country.COUNTRY.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Country.COUNTRY.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Country.COUNTRY.DISPLAY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Country.COUNTRY.NATIONALITY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Country.COUNTRY.NATIONALITY_DISPLAY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Country.COUNTRY.CODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field7() {
        return Country.COUNTRY.HOME_COUNTRY;
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
    public String value2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getDisplayName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getNationality();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getNationalityDisplayName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value7() {
        return getHomeCountry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CountryRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CountryRecord value2(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CountryRecord value3(String value) {
        setDisplayName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CountryRecord value4(String value) {
        setNationality(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CountryRecord value5(String value) {
        setNationalityDisplayName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CountryRecord value6(String value) {
        setCode(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CountryRecord value7(Boolean value) {
        setHomeCountry(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CountryRecord values(Long value1, String value2, String value3, String value4, String value5, String value6, Boolean value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CountryRecord
     */
    public CountryRecord() {
        super(Country.COUNTRY);
    }

    /**
     * Create a detached, initialised CountryRecord
     */
    public CountryRecord(Long id, String name, String displayName, String nationality, String nationalityDisplayName, String code, Boolean homeCountry) {
        super(Country.COUNTRY);

        set(0, id);
        set(1, name);
        set(2, displayName);
        set(3, nationality);
        set(4, nationalityDisplayName);
        set(5, code);
        set(6, homeCountry);
    }
}
