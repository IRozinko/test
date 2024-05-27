/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.notification.tables;


import fintech.bo.db.jooq.notification.Keys;
import fintech.bo.db.jooq.notification.tables.records.NotificationRecord;
import org.jooq.Field;
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
public class Notification extends TableImpl<NotificationRecord> {

    private static final long serialVersionUID = -1367532525;

    /**
     * The reference instance of <code>notification.notification</code>
     */
    public static final Notification NOTIFICATION_ = new Notification();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<NotificationRecord> getRecordType() {
        return NotificationRecord.class;
    }

    /**
     * The column <code>notification.notification.id</code>.
     */
    public final TableField<NotificationRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>notification.notification.created_at</code>.
     */
    public final TableField<NotificationRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>notification.notification.created_by</code>.
     */
    public final TableField<NotificationRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>notification.notification.entity_version</code>.
     */
    public final TableField<NotificationRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>notification.notification.updated_at</code>.
     */
    public final TableField<NotificationRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>notification.notification.updated_by</code>.
     */
    public final TableField<NotificationRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>notification.notification.sms_log_id</code>.
     */
    public final TableField<NotificationRecord, Long> SMS_LOG_ID = createField("sms_log_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>notification.notification.email_log_id</code>.
     */
    public final TableField<NotificationRecord, Long> EMAIL_LOG_ID = createField("email_log_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>notification.notification.cms_key</code>.
     */
    public final TableField<NotificationRecord, String> CMS_KEY = createField("cms_key", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>notification.notification.sent_at</code>.
     */
    public final TableField<NotificationRecord, LocalDateTime> SENT_AT = createField("sent_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>notification.notification.client_id</code>.
     */
    public final TableField<NotificationRecord, Long> CLIENT_ID = createField("client_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>notification.notification.loan_id</code>.
     */
    public final TableField<NotificationRecord, Long> LOAN_ID = createField("loan_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>notification.notification.loan_application_id</code>.
     */
    public final TableField<NotificationRecord, Long> LOAN_APPLICATION_ID = createField("loan_application_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>notification.notification.task_id</code>.
     */
    public final TableField<NotificationRecord, Long> TASK_ID = createField("task_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>notification.notification.debt_id</code>.
     */
    public final TableField<NotificationRecord, Long> DEBT_ID = createField("debt_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * Create a <code>notification.notification</code> table reference
     */
    public Notification() {
        this("notification", null);
    }

    /**
     * Create an aliased <code>notification.notification</code> table reference
     */
    public Notification(String alias) {
        this(alias, NOTIFICATION_);
    }

    private Notification(String alias, Table<NotificationRecord> aliased) {
        this(alias, aliased, null);
    }

    private Notification(String alias, Table<NotificationRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return fintech.bo.db.jooq.notification.Notification.NOTIFICATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<NotificationRecord> getPrimaryKey() {
        return Keys.NOTIFICATION_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<NotificationRecord>> getKeys() {
        return Arrays.<UniqueKey<NotificationRecord>>asList(Keys.NOTIFICATION_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Notification as(String alias) {
        return new Notification(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Notification rename(String name) {
        return new Notification(name, null);
    }
}
