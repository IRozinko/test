/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.quartz.tables.records;


import fintech.bo.db.jooq.quartz.tables.QrtzSimpleTriggers;
import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;


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
public class QrtzSimpleTriggersRecord extends UpdatableRecordImpl<QrtzSimpleTriggersRecord> implements Record6<String, String, String, Long, Long, Long> {

    private static final long serialVersionUID = 221774916;

    /**
     * Setter for <code>quartz.qrtz_simple_triggers.sched_name</code>.
     */
    public void setSchedName(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>quartz.qrtz_simple_triggers.sched_name</code>.
     */
    public String getSchedName() {
        return (String) get(0);
    }

    /**
     * Setter for <code>quartz.qrtz_simple_triggers.trigger_name</code>.
     */
    public void setTriggerName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>quartz.qrtz_simple_triggers.trigger_name</code>.
     */
    public String getTriggerName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>quartz.qrtz_simple_triggers.trigger_group</code>.
     */
    public void setTriggerGroup(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>quartz.qrtz_simple_triggers.trigger_group</code>.
     */
    public String getTriggerGroup() {
        return (String) get(2);
    }

    /**
     * Setter for <code>quartz.qrtz_simple_triggers.repeat_count</code>.
     */
    public void setRepeatCount(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>quartz.qrtz_simple_triggers.repeat_count</code>.
     */
    public Long getRepeatCount() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>quartz.qrtz_simple_triggers.repeat_interval</code>.
     */
    public void setRepeatInterval(Long value) {
        set(4, value);
    }

    /**
     * Getter for <code>quartz.qrtz_simple_triggers.repeat_interval</code>.
     */
    public Long getRepeatInterval() {
        return (Long) get(4);
    }

    /**
     * Setter for <code>quartz.qrtz_simple_triggers.times_triggered</code>.
     */
    public void setTimesTriggered(Long value) {
        set(5, value);
    }

    /**
     * Getter for <code>quartz.qrtz_simple_triggers.times_triggered</code>.
     */
    public Long getTimesTriggered() {
        return (Long) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record3<String, String, String> key() {
        return (Record3) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<String, String, String, Long, Long, Long> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<String, String, String, Long, Long, Long> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return QrtzSimpleTriggers.QRTZ_SIMPLE_TRIGGERS.SCHED_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return QrtzSimpleTriggers.QRTZ_SIMPLE_TRIGGERS.TRIGGER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return QrtzSimpleTriggers.QRTZ_SIMPLE_TRIGGERS.TRIGGER_GROUP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return QrtzSimpleTriggers.QRTZ_SIMPLE_TRIGGERS.REPEAT_COUNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field5() {
        return QrtzSimpleTriggers.QRTZ_SIMPLE_TRIGGERS.REPEAT_INTERVAL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field6() {
        return QrtzSimpleTriggers.QRTZ_SIMPLE_TRIGGERS.TIMES_TRIGGERED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getSchedName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getTriggerName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getTriggerGroup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value4() {
        return getRepeatCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value5() {
        return getRepeatInterval();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value6() {
        return getTimesTriggered();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QrtzSimpleTriggersRecord value1(String value) {
        setSchedName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QrtzSimpleTriggersRecord value2(String value) {
        setTriggerName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QrtzSimpleTriggersRecord value3(String value) {
        setTriggerGroup(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QrtzSimpleTriggersRecord value4(Long value) {
        setRepeatCount(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QrtzSimpleTriggersRecord value5(Long value) {
        setRepeatInterval(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QrtzSimpleTriggersRecord value6(Long value) {
        setTimesTriggered(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QrtzSimpleTriggersRecord values(String value1, String value2, String value3, Long value4, Long value5, Long value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached QrtzSimpleTriggersRecord
     */
    public QrtzSimpleTriggersRecord() {
        super(QrtzSimpleTriggers.QRTZ_SIMPLE_TRIGGERS);
    }

    /**
     * Create a detached, initialised QrtzSimpleTriggersRecord
     */
    public QrtzSimpleTriggersRecord(String schedName, String triggerName, String triggerGroup, Long repeatCount, Long repeatInterval, Long timesTriggered) {
        super(QrtzSimpleTriggers.QRTZ_SIMPLE_TRIGGERS);

        set(0, schedName);
        set(1, triggerName);
        set(2, triggerGroup);
        set(3, repeatCount);
        set(4, repeatInterval);
        set(5, timesTriggered);
    }
}
