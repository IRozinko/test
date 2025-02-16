/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.decision_engine.tables;


import fintech.bo.db.jooq.decision_engine.DecisionEngine;
import fintech.bo.db.jooq.decision_engine.Keys;
import fintech.bo.db.jooq.decision_engine.tables.records.RequestRecord;

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
public class Request extends TableImpl<RequestRecord> {

    private static final long serialVersionUID = 1348369806;

    /**
     * The reference instance of <code>decision_engine.request</code>
     */
    public static final Request REQUEST = new Request();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RequestRecord> getRecordType() {
        return RequestRecord.class;
    }

    /**
     * The column <code>decision_engine.request.id</code>.
     */
    public final TableField<RequestRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>decision_engine.request.created_at</code>.
     */
    public final TableField<RequestRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>decision_engine.request.created_by</code>.
     */
    public final TableField<RequestRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>decision_engine.request.entity_version</code>.
     */
    public final TableField<RequestRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>decision_engine.request.updated_at</code>.
     */
    public final TableField<RequestRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>decision_engine.request.updated_by</code>.
     */
    public final TableField<RequestRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>decision_engine.request.scenario</code>.
     */
    public final TableField<RequestRecord, String> SCENARIO = createField("scenario", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>decision_engine.request.client_id</code>.
     */
    public final TableField<RequestRecord, Long> CLIENT_ID = createField("client_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>decision_engine.request.scoring_model_id</code>.
     */
    public final TableField<RequestRecord, Long> SCORING_MODEL_ID = createField("scoring_model_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>decision_engine.request.status</code>.
     */
    public final TableField<RequestRecord, String> STATUS = createField("status", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>decision_engine.request.error</code>.
     */
    public final TableField<RequestRecord, String> ERROR = createField("error", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>decision_engine.request.response</code>.
     */
    public final TableField<RequestRecord, String> RESPONSE = createField("response", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>decision_engine.request.decision</code>.
     */
    public final TableField<RequestRecord, String> DECISION = createField("decision", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>decision_engine.request.rating</code>.
     */
    public final TableField<RequestRecord, String> RATING = createField("rating", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>decision_engine.request.score</code>.
     */
    public final TableField<RequestRecord, String> SCORE = createField("score", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>decision_engine.request.variables_result</code>.
     */
    public final TableField<RequestRecord, String> VARIABLES_RESULT = createField("variables_result", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>decision_engine.request</code> table reference
     */
    public Request() {
        this("request", null);
    }

    /**
     * Create an aliased <code>decision_engine.request</code> table reference
     */
    public Request(String alias) {
        this(alias, REQUEST);
    }

    private Request(String alias, Table<RequestRecord> aliased) {
        this(alias, aliased, null);
    }

    private Request(String alias, Table<RequestRecord> aliased, Field<?>[] parameters) {
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
    public UniqueKey<RequestRecord> getPrimaryKey() {
        return Keys.REQUEST_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<RequestRecord>> getKeys() {
        return Arrays.<UniqueKey<RequestRecord>>asList(Keys.REQUEST_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Request as(String alias) {
        return new Request(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Request rename(String name) {
        return new Request(name, null);
    }
}
