/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.task.tables;


import fintech.bo.db.jooq.task.Keys;
import fintech.bo.db.jooq.task.Task;
import fintech.bo.db.jooq.task.tables.records.LogRecord;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;

import javax.annotation.Generated;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


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
public class Log extends TableImpl<LogRecord> {

    private static final long serialVersionUID = -1587357794;

    /**
     * The reference instance of <code>task.log</code>
     */
    public static final Log LOG = new Log();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LogRecord> getRecordType() {
        return LogRecord.class;
    }

    /**
     * The column <code>task.log.id</code>.
     */
    public final TableField<LogRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>task.log.created_at</code>.
     */
    public final TableField<LogRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>task.log.created_by</code>.
     */
    public final TableField<LogRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>task.log.entity_version</code>.
     */
    public final TableField<LogRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>task.log.updated_at</code>.
     */
    public final TableField<LogRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>task.log.updated_by</code>.
     */
    public final TableField<LogRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>task.log.agent</code>.
     */
    public final TableField<LogRecord, String> AGENT = createField("agent", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>task.log.comment</code>.
     */
    public final TableField<LogRecord, String> COMMENT = createField("comment", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>task.log.due_at</code>.
     */
    public final TableField<LogRecord, LocalDateTime> DUE_AT = createField("due_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>task.log.expires_at</code>.
     */
    public final TableField<LogRecord, LocalDateTime> EXPIRES_AT = createField("expires_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>task.log.operation</code>.
     */
    public final TableField<LogRecord, String> OPERATION = createField("operation", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>task.log.reason</code>.
     */
    public final TableField<LogRecord, String> REASON = createField("reason", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>task.log.resolution</code>.
     */
    public final TableField<LogRecord, String> RESOLUTION = createField("resolution", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>task.log.resolution_detail</code>.
     */
    public final TableField<LogRecord, String> RESOLUTION_DETAIL = createField("resolution_detail", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>task.log.resolution_sub_detail</code>.
     */
    public final TableField<LogRecord, String> RESOLUTION_SUB_DETAIL = createField("resolution_sub_detail", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>task.log.task_id</code>.
     */
    public final TableField<LogRecord, Long> TASK_ID = createField("task_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>task.log</code> table reference
     */
    public Log() {
        this("log", null);
    }

    /**
     * Create an aliased <code>task.log</code> table reference
     */
    public Log(String alias) {
        this(alias, LOG);
    }

    private Log(String alias, Table<LogRecord> aliased) {
        this(alias, aliased, null);
    }

    private Log(String alias, Table<LogRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Task.TASK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<LogRecord> getPrimaryKey() {
        return Keys.LOG_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<LogRecord>> getKeys() {
        return Arrays.<UniqueKey<LogRecord>>asList(Keys.LOG_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<LogRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<LogRecord, ?>>asList(Keys.LOG__FK_LOG_TASK_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Log as(String alias) {
        return new Log(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Log rename(String name) {
        return new Log(name, null);
    }
}
