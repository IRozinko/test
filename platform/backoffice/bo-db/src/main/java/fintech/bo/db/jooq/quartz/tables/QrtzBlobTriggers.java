/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.quartz.tables;


import fintech.bo.db.jooq.quartz.Keys;
import fintech.bo.db.jooq.quartz.Quartz;
import fintech.bo.db.jooq.quartz.tables.records.QrtzBlobTriggersRecord;
import org.jooq.*;
import org.jooq.impl.TableImpl;

import javax.annotation.Generated;
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
public class QrtzBlobTriggers extends TableImpl<QrtzBlobTriggersRecord> {

    private static final long serialVersionUID = 1472161906;

    /**
     * The reference instance of <code>quartz.qrtz_blob_triggers</code>
     */
    public static final QrtzBlobTriggers QRTZ_BLOB_TRIGGERS = new QrtzBlobTriggers();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<QrtzBlobTriggersRecord> getRecordType() {
        return QrtzBlobTriggersRecord.class;
    }

    /**
     * The column <code>quartz.qrtz_blob_triggers.sched_name</code>.
     */
    public final TableField<QrtzBlobTriggersRecord, String> SCHED_NAME = createField("sched_name", org.jooq.impl.SQLDataType.VARCHAR.length(120).nullable(false), this, "");

    /**
     * The column <code>quartz.qrtz_blob_triggers.trigger_name</code>.
     */
    public final TableField<QrtzBlobTriggersRecord, String> TRIGGER_NAME = createField("trigger_name", org.jooq.impl.SQLDataType.VARCHAR.length(200).nullable(false), this, "");

    /**
     * The column <code>quartz.qrtz_blob_triggers.trigger_group</code>.
     */
    public final TableField<QrtzBlobTriggersRecord, String> TRIGGER_GROUP = createField("trigger_group", org.jooq.impl.SQLDataType.VARCHAR.length(200).nullable(false), this, "");

    /**
     * The column <code>quartz.qrtz_blob_triggers.blob_data</code>.
     */
    public final TableField<QrtzBlobTriggersRecord, byte[]> BLOB_DATA = createField("blob_data", org.jooq.impl.SQLDataType.BLOB, this, "");

    /**
     * Create a <code>quartz.qrtz_blob_triggers</code> table reference
     */
    public QrtzBlobTriggers() {
        this("qrtz_blob_triggers", null);
    }

    /**
     * Create an aliased <code>quartz.qrtz_blob_triggers</code> table reference
     */
    public QrtzBlobTriggers(String alias) {
        this(alias, QRTZ_BLOB_TRIGGERS);
    }

    private QrtzBlobTriggers(String alias, Table<QrtzBlobTriggersRecord> aliased) {
        this(alias, aliased, null);
    }

    private QrtzBlobTriggers(String alias, Table<QrtzBlobTriggersRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Quartz.QUARTZ;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<QrtzBlobTriggersRecord> getPrimaryKey() {
        return Keys.QRTZ_BLOB_TRIGGERS_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<QrtzBlobTriggersRecord>> getKeys() {
        return Arrays.<UniqueKey<QrtzBlobTriggersRecord>>asList(Keys.QRTZ_BLOB_TRIGGERS_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<QrtzBlobTriggersRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<QrtzBlobTriggersRecord, ?>>asList(Keys.QRTZ_BLOB_TRIGGERS__QRTZ_BLOB_TRIGGERS_SCHED_NAME_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QrtzBlobTriggers as(String alias) {
        return new QrtzBlobTriggers(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public QrtzBlobTriggers rename(String name) {
        return new QrtzBlobTriggers(name, null);
    }
}
