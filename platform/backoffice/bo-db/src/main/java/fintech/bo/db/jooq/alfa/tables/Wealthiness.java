/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.alfa.tables;


import fintech.bo.db.jooq.alfa.Alfa;
import fintech.bo.db.jooq.alfa.Keys;
import fintech.bo.db.jooq.alfa.tables.records.WealthinessRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class Wealthiness extends TableImpl<WealthinessRecord> {

    private static final long serialVersionUID = -1262509413;

    /**
     * The reference instance of <code>alfa.wealthiness</code>
     */
    public static final Wealthiness WEALTHINESS = new Wealthiness();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<WealthinessRecord> getRecordType() {
        return WealthinessRecord.class;
    }

    /**
     * The column <code>alfa.wealthiness.id</code>.
     */
    public final TableField<WealthinessRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>alfa.wealthiness.created_at</code>.
     */
    public final TableField<WealthinessRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>alfa.wealthiness.created_by</code>.
     */
    public final TableField<WealthinessRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>alfa.wealthiness.entity_version</code>.
     */
    public final TableField<WealthinessRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>alfa.wealthiness.updated_at</code>.
     */
    public final TableField<WealthinessRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>alfa.wealthiness.updated_by</code>.
     */
    public final TableField<WealthinessRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>alfa.wealthiness.account_number</code>.
     */
    public final TableField<WealthinessRecord, String> ACCOUNT_NUMBER = createField("account_number", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>alfa.wealthiness.client_id</code>.
     */
    public final TableField<WealthinessRecord, Long> CLIENT_ID = createField("client_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>alfa.wealthiness.instantor_response_id</code>.
     */
    public final TableField<WealthinessRecord, Long> INSTANTOR_RESPONSE_ID = createField("instantor_response_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>alfa.wealthiness.manual_weighted_wealthiness</code>.
     */
    public final TableField<WealthinessRecord, BigDecimal> MANUAL_WEIGHTED_WEALTHINESS = createField("manual_weighted_wealthiness", org.jooq.impl.SQLDataType.NUMERIC.precision(19, 2).nullable(false), this, "");

    /**
     * The column <code>alfa.wealthiness.months</code>.
     */
    public final TableField<WealthinessRecord, Integer> MONTHS = createField("months", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>alfa.wealthiness.nordigen_log_id</code>.
     */
    public final TableField<WealthinessRecord, Long> NORDIGEN_LOG_ID = createField("nordigen_log_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>alfa.wealthiness.nordigen_weighted_wealthiness</code>.
     */
    public final TableField<WealthinessRecord, BigDecimal> NORDIGEN_WEIGHTED_WEALTHINESS = createField("nordigen_weighted_wealthiness", org.jooq.impl.SQLDataType.NUMERIC.precision(19, 2).nullable(false), this, "");

    /**
     * The column <code>alfa.wealthiness.period_from</code>.
     */
    public final TableField<WealthinessRecord, LocalDate> PERIOD_FROM = createField("period_from", org.jooq.impl.SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column <code>alfa.wealthiness.period_to</code>.
     */
    public final TableField<WealthinessRecord, LocalDate> PERIOD_TO = createField("period_to", org.jooq.impl.SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * Create a <code>alfa.wealthiness</code> table reference
     */
    public Wealthiness() {
        this("wealthiness", null);
    }

    /**
     * Create an aliased <code>alfa.wealthiness</code> table reference
     */
    public Wealthiness(String alias) {
        this(alias, WEALTHINESS);
    }

    private Wealthiness(String alias, Table<WealthinessRecord> aliased) {
        this(alias, aliased, null);
    }

    private Wealthiness(String alias, Table<WealthinessRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Alfa.ALFA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<WealthinessRecord> getPrimaryKey() {
        return Keys.WEALTHINESS_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<WealthinessRecord>> getKeys() {
        return Arrays.<UniqueKey<WealthinessRecord>>asList(Keys.WEALTHINESS_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Wealthiness as(String alias) {
        return new Wealthiness(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Wealthiness rename(String name) {
        return new Wealthiness(name, null);
    }
}
