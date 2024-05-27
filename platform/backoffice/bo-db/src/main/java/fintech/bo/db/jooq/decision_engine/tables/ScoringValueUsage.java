/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.decision_engine.tables;


import fintech.bo.db.jooq.decision_engine.DecisionEngine;
import fintech.bo.db.jooq.decision_engine.Keys;
import fintech.bo.db.jooq.decision_engine.tables.records.ScoringValueUsageRecord;

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
public class ScoringValueUsage extends TableImpl<ScoringValueUsageRecord> {

    private static final long serialVersionUID = -933706150;

    /**
     * The reference instance of <code>decision_engine.scoring_value_usage</code>
     */
    public static final ScoringValueUsage SCORING_VALUE_USAGE = new ScoringValueUsage();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ScoringValueUsageRecord> getRecordType() {
        return ScoringValueUsageRecord.class;
    }

    /**
     * The column <code>decision_engine.scoring_value_usage.id</code>.
     */
    public final TableField<ScoringValueUsageRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>decision_engine.scoring_value_usage.created_at</code>.
     */
    public final TableField<ScoringValueUsageRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>decision_engine.scoring_value_usage.created_by</code>.
     */
    public final TableField<ScoringValueUsageRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>decision_engine.scoring_value_usage.entity_version</code>.
     */
    public final TableField<ScoringValueUsageRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>decision_engine.scoring_value_usage.updated_at</code>.
     */
    public final TableField<ScoringValueUsageRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>decision_engine.scoring_value_usage.updated_by</code>.
     */
    public final TableField<ScoringValueUsageRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>decision_engine.scoring_value_usage.decision_engine_request_id</code>.
     */
    public final TableField<ScoringValueUsageRecord, Long> DECISION_ENGINE_REQUEST_ID = createField("decision_engine_request_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>decision_engine.scoring_value_usage.scoring_value_id</code>.
     */
    public final TableField<ScoringValueUsageRecord, Long> SCORING_VALUE_ID = createField("scoring_value_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>decision_engine.scoring_value_usage</code> table reference
     */
    public ScoringValueUsage() {
        this("scoring_value_usage", null);
    }

    /**
     * Create an aliased <code>decision_engine.scoring_value_usage</code> table reference
     */
    public ScoringValueUsage(String alias) {
        this(alias, SCORING_VALUE_USAGE);
    }

    private ScoringValueUsage(String alias, Table<ScoringValueUsageRecord> aliased) {
        this(alias, aliased, null);
    }

    private ScoringValueUsage(String alias, Table<ScoringValueUsageRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return DecisionEngine.DECISION_ENGINE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ScoringValueUsageRecord> getPrimaryKey() {
        return Keys.SCORING_VALUE_USAGE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ScoringValueUsageRecord>> getKeys() {
        return Arrays.<UniqueKey<ScoringValueUsageRecord>>asList(Keys.SCORING_VALUE_USAGE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoringValueUsage as(String alias) {
        return new ScoringValueUsage(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ScoringValueUsage rename(String name) {
        return new ScoringValueUsage(name, null);
    }
}
