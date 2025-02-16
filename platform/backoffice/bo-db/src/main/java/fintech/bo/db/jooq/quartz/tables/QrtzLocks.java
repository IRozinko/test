/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.quartz.tables;


import fintech.bo.db.jooq.quartz.Keys;
import fintech.bo.db.jooq.quartz.Quartz;
import fintech.bo.db.jooq.quartz.tables.records.QrtzLocksRecord;
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
public class QrtzLocks extends TableImpl<QrtzLocksRecord> {

    private static final long serialVersionUID = 357300451;

    /**
     * The reference instance of <code>quartz.qrtz_locks</code>
     */
    public static final QrtzLocks QRTZ_LOCKS = new QrtzLocks();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<QrtzLocksRecord> getRecordType() {
        return QrtzLocksRecord.class;
    }

    /**
     * The column <code>quartz.qrtz_locks.sched_name</code>.
     */
    public final TableField<QrtzLocksRecord, String> SCHED_NAME = createField("sched_name", org.jooq.impl.SQLDataType.VARCHAR.length(120).nullable(false), this, "");

    /**
     * The column <code>quartz.qrtz_locks.lock_name</code>.
     */
    public final TableField<QrtzLocksRecord, String> LOCK_NAME = createField("lock_name", org.jooq.impl.SQLDataType.VARCHAR.length(40).nullable(false), this, "");

    /**
     * Create a <code>quartz.qrtz_locks</code> table reference
     */
    public QrtzLocks() {
        this("qrtz_locks", null);
    }

    /**
     * Create an aliased <code>quartz.qrtz_locks</code> table reference
     */
    public QrtzLocks(String alias) {
        this(alias, QRTZ_LOCKS);
    }

    private QrtzLocks(String alias, Table<QrtzLocksRecord> aliased) {
        this(alias, aliased, null);
    }

    private QrtzLocks(String alias, Table<QrtzLocksRecord> aliased, Field<?>[] parameters) {
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
    public UniqueKey<QrtzLocksRecord> getPrimaryKey() {
        return Keys.QRTZ_LOCKS_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<QrtzLocksRecord>> getKeys() {
        return Arrays.<UniqueKey<QrtzLocksRecord>>asList(Keys.QRTZ_LOCKS_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QrtzLocks as(String alias) {
        return new QrtzLocks(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public QrtzLocks rename(String name) {
        return new QrtzLocks(name, null);
    }
}
