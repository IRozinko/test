/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.lending.tables;


import fintech.bo.db.jooq.lending.Keys;
import fintech.bo.db.jooq.lending.Lending;
import fintech.bo.db.jooq.lending.tables.records.CreditLimitRecord;

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
public class CreditLimit extends TableImpl<CreditLimitRecord> {

    private static final long serialVersionUID = 1072248212;

    /**
     * The reference instance of <code>lending.credit_limit</code>
     */
    public static final CreditLimit CREDIT_LIMIT = new CreditLimit();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CreditLimitRecord> getRecordType() {
        return CreditLimitRecord.class;
    }

    /**
     * The column <code>lending.credit_limit.id</code>.
     */
    public final TableField<CreditLimitRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>lending.credit_limit.created_at</code>.
     */
    public final TableField<CreditLimitRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>lending.credit_limit.created_by</code>.
     */
    public final TableField<CreditLimitRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>lending.credit_limit.entity_version</code>.
     */
    public final TableField<CreditLimitRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>lending.credit_limit.updated_at</code>.
     */
    public final TableField<CreditLimitRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>lending.credit_limit.updated_by</code>.
     */
    public final TableField<CreditLimitRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>lending.credit_limit.active_from</code>.
     */
    public final TableField<CreditLimitRecord, LocalDate> ACTIVE_FROM = createField("active_from", org.jooq.impl.SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column <code>lending.credit_limit.client_id</code>.
     */
    public final TableField<CreditLimitRecord, Long> CLIENT_ID = createField("client_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>lending.credit_limit.credit_limit</code>.
     */
    public final TableField<CreditLimitRecord, BigDecimal> CREDIT_LIMIT_ = createField("credit_limit", org.jooq.impl.SQLDataType.NUMERIC.precision(19, 2).nullable(false), this, "");

    /**
     * The column <code>lending.credit_limit.reason</code>.
     */
    public final TableField<CreditLimitRecord, String> REASON = createField("reason", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * Create a <code>lending.credit_limit</code> table reference
     */
    public CreditLimit() {
        this("credit_limit", null);
    }

    /**
     * Create an aliased <code>lending.credit_limit</code> table reference
     */
    public CreditLimit(String alias) {
        this(alias, CREDIT_LIMIT);
    }

    private CreditLimit(String alias, Table<CreditLimitRecord> aliased) {
        this(alias, aliased, null);
    }

    private CreditLimit(String alias, Table<CreditLimitRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Lending.LENDING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<CreditLimitRecord> getPrimaryKey() {
        return Keys.CREDIT_LIMIT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<CreditLimitRecord>> getKeys() {
        return Arrays.<UniqueKey<CreditLimitRecord>>asList(Keys.CREDIT_LIMIT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreditLimit as(String alias) {
        return new CreditLimit(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public CreditLimit rename(String name) {
        return new CreditLimit(name, null);
    }
}
