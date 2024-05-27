/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.alfa.tables;


import fintech.bo.db.jooq.alfa.Alfa;
import fintech.bo.db.jooq.alfa.Keys;
import fintech.bo.db.jooq.alfa.tables.records.LoanReschedulingRecord;

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
public class LoanRescheduling extends TableImpl<LoanReschedulingRecord> {

    private static final long serialVersionUID = -2037286868;

    /**
     * The reference instance of <code>alfa.loan_rescheduling</code>
     */
    public static final LoanRescheduling LOAN_RESCHEDULING = new LoanRescheduling();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LoanReschedulingRecord> getRecordType() {
        return LoanReschedulingRecord.class;
    }

    /**
     * The column <code>alfa.loan_rescheduling.id</code>.
     */
    public final TableField<LoanReschedulingRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>alfa.loan_rescheduling.loan_id</code>.
     */
    public final TableField<LoanReschedulingRecord, Long> LOAN_ID = createField("loan_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>alfa.loan_rescheduling.status</code>.
     */
    public final TableField<LoanReschedulingRecord, String> STATUS = createField("status", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>alfa.loan_rescheduling.number_of_payments</code>.
     */
    public final TableField<LoanReschedulingRecord, Integer> NUMBER_OF_PAYMENTS = createField("number_of_payments", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>alfa.loan_rescheduling.repayment_due_days</code>.
     */
    public final TableField<LoanReschedulingRecord, Integer> REPAYMENT_DUE_DAYS = createField("repayment_due_days", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>alfa.loan_rescheduling.grace_period_days</code>.
     */
    public final TableField<LoanReschedulingRecord, Integer> GRACE_PERIOD_DAYS = createField("grace_period_days", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>alfa.loan_rescheduling.installment_amount</code>.
     */
    public final TableField<LoanReschedulingRecord, BigDecimal> INSTALLMENT_AMOUNT = createField("installment_amount", org.jooq.impl.SQLDataType.NUMERIC.precision(19, 4).nullable(false), this, "");

    /**
     * The column <code>alfa.loan_rescheduling.reschedule_date</code>.
     */
    public final TableField<LoanReschedulingRecord, LocalDate> RESCHEDULE_DATE = createField("reschedule_date", org.jooq.impl.SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column <code>alfa.loan_rescheduling.expire_date</code>.
     */
    public final TableField<LoanReschedulingRecord, LocalDate> EXPIRE_DATE = createField("expire_date", org.jooq.impl.SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column <code>alfa.loan_rescheduling.entity_version</code>.
     */
    public final TableField<LoanReschedulingRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>alfa.loan_rescheduling.created_at</code>.
     */
    public final TableField<LoanReschedulingRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>alfa.loan_rescheduling.created_by</code>.
     */
    public final TableField<LoanReschedulingRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>alfa.loan_rescheduling.updated_at</code>.
     */
    public final TableField<LoanReschedulingRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>alfa.loan_rescheduling.updated_by</code>.
     */
    public final TableField<LoanReschedulingRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>alfa.loan_rescheduling</code> table reference
     */
    public LoanRescheduling() {
        this("loan_rescheduling", null);
    }

    /**
     * Create an aliased <code>alfa.loan_rescheduling</code> table reference
     */
    public LoanRescheduling(String alias) {
        this(alias, LOAN_RESCHEDULING);
    }

    private LoanRescheduling(String alias, Table<LoanReschedulingRecord> aliased) {
        this(alias, aliased, null);
    }

    private LoanRescheduling(String alias, Table<LoanReschedulingRecord> aliased, Field<?>[] parameters) {
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
    public UniqueKey<LoanReschedulingRecord> getPrimaryKey() {
        return Keys.LOAN_RESCHEDULING_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<LoanReschedulingRecord>> getKeys() {
        return Arrays.<UniqueKey<LoanReschedulingRecord>>asList(Keys.LOAN_RESCHEDULING_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoanRescheduling as(String alias) {
        return new LoanRescheduling(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public LoanRescheduling rename(String name) {
        return new LoanRescheduling(name, null);
    }
}
