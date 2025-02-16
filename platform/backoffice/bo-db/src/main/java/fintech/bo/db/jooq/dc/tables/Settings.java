/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.dc.tables;


import fintech.bo.db.jooq.dc.Dc;
import fintech.bo.db.jooq.dc.Keys;
import fintech.bo.db.jooq.dc.tables.records.SettingsRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


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
public class Settings extends TableImpl<SettingsRecord> {

    private static final long serialVersionUID = 908413380;

    /**
     * The reference instance of <code>dc.settings</code>
     */
    public static final Settings SETTINGS = new Settings();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SettingsRecord> getRecordType() {
        return SettingsRecord.class;
    }

    /**
     * The column <code>dc.settings.id</code>.
     */
    public final TableField<SettingsRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>dc.settings.created_at</code>.
     */
    public final TableField<SettingsRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>dc.settings.created_by</code>.
     */
    public final TableField<SettingsRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>dc.settings.entity_version</code>.
     */
    public final TableField<SettingsRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>dc.settings.updated_at</code>.
     */
    public final TableField<SettingsRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>dc.settings.updated_by</code>.
     */
    public final TableField<SettingsRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>dc.settings.settings_json</code>.
     */
    public final TableField<SettingsRecord, String> SETTINGS_JSON = createField("settings_json", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>dc.settings</code> table reference
     */
    public Settings() {
        this("settings", null);
    }

    /**
     * Create an aliased <code>dc.settings</code> table reference
     */
    public Settings(String alias) {
        this(alias, SETTINGS);
    }

    private Settings(String alias, Table<SettingsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Settings(String alias, Table<SettingsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Dc.DC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SettingsRecord> getPrimaryKey() {
        return Keys.SETTINGS_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SettingsRecord>> getKeys() {
        return Arrays.<UniqueKey<SettingsRecord>>asList(Keys.SETTINGS_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Settings as(String alias) {
        return new Settings(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Settings rename(String name) {
        return new Settings(name, null);
    }
}
