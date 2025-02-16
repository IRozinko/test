/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.workflow.tables;


import fintech.bo.db.jooq.workflow.Keys;
import fintech.bo.db.jooq.workflow.Workflow;
import fintech.bo.db.jooq.workflow.tables.records.ActivityRecord;
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
public class Activity extends TableImpl<ActivityRecord> {

    private static final long serialVersionUID = 977045314;

    /**
     * The reference instance of <code>workflow.activity</code>
     */
    public static final Activity ACTIVITY = new Activity();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ActivityRecord> getRecordType() {
        return ActivityRecord.class;
    }

    /**
     * The column <code>workflow.activity.id</code>.
     */
    public final TableField<ActivityRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>workflow.activity.created_at</code>.
     */
    public final TableField<ActivityRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>workflow.activity.created_by</code>.
     */
    public final TableField<ActivityRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>workflow.activity.entity_version</code>.
     */
    public final TableField<ActivityRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>workflow.activity.updated_at</code>.
     */
    public final TableField<ActivityRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>workflow.activity.updated_by</code>.
     */
    public final TableField<ActivityRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>workflow.activity.actor</code>.
     */
    public final TableField<ActivityRecord, String> ACTOR = createField("actor", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>workflow.activity.attempts</code>.
     */
    public final TableField<ActivityRecord, Long> ATTEMPTS = createField("attempts", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>workflow.activity.completed_at</code>.
     */
    public final TableField<ActivityRecord, LocalDateTime> COMPLETED_AT = createField("completed_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>workflow.activity.error</code>.
     */
    public final TableField<ActivityRecord, String> ERROR = createField("error", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>workflow.activity.expires_at</code>.
     */
    public final TableField<ActivityRecord, LocalDateTime> EXPIRES_AT = createField("expires_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>workflow.activity.name</code>.
     */
    public final TableField<ActivityRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>workflow.activity.next_attempt_at</code>.
     */
    public final TableField<ActivityRecord, LocalDateTime> NEXT_ATTEMPT_AT = createField("next_attempt_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>workflow.activity.resolution</code>.
     */
    public final TableField<ActivityRecord, String> RESOLUTION = createField("resolution", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>workflow.activity.resolution_detail</code>.
     */
    public final TableField<ActivityRecord, String> RESOLUTION_DETAIL = createField("resolution_detail", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>workflow.activity.status</code>.
     */
    public final TableField<ActivityRecord, String> STATUS = createField("status", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>workflow.activity.workflow_id</code>.
     */
    public final TableField<ActivityRecord, Long> WORKFLOW_ID = createField("workflow_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>workflow.activity.ui_state</code>.
     */
    public final TableField<ActivityRecord, String> UI_STATE = createField("ui_state", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>workflow.activity</code> table reference
     */
    public Activity() {
        this("activity", null);
    }

    /**
     * Create an aliased <code>workflow.activity</code> table reference
     */
    public Activity(String alias) {
        this(alias, ACTIVITY);
    }

    private Activity(String alias, Table<ActivityRecord> aliased) {
        this(alias, aliased, null);
    }

    private Activity(String alias, Table<ActivityRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Workflow.WORKFLOW;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ActivityRecord> getPrimaryKey() {
        return Keys.ACTIVITY_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ActivityRecord>> getKeys() {
        return Arrays.<UniqueKey<ActivityRecord>>asList(Keys.ACTIVITY_PKEY, Keys.IDX_ACTIVITY_ACTIVITY_NAME_UQ);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<ActivityRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ActivityRecord, ?>>asList(Keys.ACTIVITY__FK_ACTIVITY_WORKFLOW_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Activity as(String alias) {
        return new Activity(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Activity rename(String name) {
        return new Activity(name, null);
    }
}
