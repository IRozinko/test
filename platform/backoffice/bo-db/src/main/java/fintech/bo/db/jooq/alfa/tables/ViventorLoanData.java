/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.alfa.tables;


import fintech.bo.db.jooq.alfa.Alfa;
import fintech.bo.db.jooq.alfa.Keys;
import fintech.bo.db.jooq.alfa.tables.records.ViventorLoanDataRecord;

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
public class ViventorLoanData extends TableImpl<ViventorLoanDataRecord> {

    private static final long serialVersionUID = 464851906;

    /**
     * The reference instance of <code>alfa.viventor_loan_data</code>
     */
    public static final ViventorLoanData VIVENTOR_LOAN_DATA = new ViventorLoanData();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViventorLoanDataRecord> getRecordType() {
        return ViventorLoanDataRecord.class;
    }

    /**
     * The column <code>alfa.viventor_loan_data.id</code>.
     */
    public final TableField<ViventorLoanDataRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>alfa.viventor_loan_data.created_at</code>.
     */
    public final TableField<ViventorLoanDataRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>alfa.viventor_loan_data.created_by</code>.
     */
    public final TableField<ViventorLoanDataRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>alfa.viventor_loan_data.entity_version</code>.
     */
    public final TableField<ViventorLoanDataRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>alfa.viventor_loan_data.updated_at</code>.
     */
    public final TableField<ViventorLoanDataRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>alfa.viventor_loan_data.updated_by</code>.
     */
    public final TableField<ViventorLoanDataRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>alfa.viventor_loan_data.loan_id</code>.
     */
    public final TableField<ViventorLoanDataRecord, Long> LOAN_ID = createField("loan_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>alfa.viventor_loan_data.viventor_loan_id</code>.
     */
    public final TableField<ViventorLoanDataRecord, String> VIVENTOR_LOAN_ID = createField("viventor_loan_id", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>alfa.viventor_loan_data.viventor_loan_extension</code>.
     */
    public final TableField<ViventorLoanDataRecord, Short> VIVENTOR_LOAN_EXTENSION = createField("viventor_loan_extension", org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

    /**
     * The column <code>alfa.viventor_loan_data.loan_index</code>.
     */
    public final TableField<ViventorLoanDataRecord, Short> LOAN_INDEX = createField("loan_index", org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

    /**
     * The column <code>alfa.viventor_loan_data.status</code>.
     */
    public final TableField<ViventorLoanDataRecord, String> STATUS = createField("status", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>alfa.viventor_loan_data.status_detail</code>.
     */
    public final TableField<ViventorLoanDataRecord, String> STATUS_DETAIL = createField("status_detail", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>alfa.viventor_loan_data.viventor_loan</code>.
     */
    public final TableField<ViventorLoanDataRecord, String> VIVENTOR_LOAN = createField("viventor_loan", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>alfa.viventor_loan_data.principal</code>.
     */
    public final TableField<ViventorLoanDataRecord, BigDecimal> PRINCIPAL = createField("principal", org.jooq.impl.SQLDataType.NUMERIC.precision(19, 2).nullable(false), this, "");

    /**
     * The column <code>alfa.viventor_loan_data.interest_rate</code>.
     */
    public final TableField<ViventorLoanDataRecord, BigDecimal> INTEREST_RATE = createField("interest_rate", org.jooq.impl.SQLDataType.NUMERIC.precision(5, 2).nullable(false), this, "");

    /**
     * The column <code>alfa.viventor_loan_data.last_synced_at</code>.
     */
    public final TableField<ViventorLoanDataRecord, LocalDateTime> LAST_SYNCED_AT = createField("last_synced_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>alfa.viventor_loan_data.start_date</code>.
     */
    public final TableField<ViventorLoanDataRecord, LocalDate> START_DATE = createField("start_date", org.jooq.impl.SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column <code>alfa.viventor_loan_data.maturity_date</code>.
     */
    public final TableField<ViventorLoanDataRecord, LocalDate> MATURITY_DATE = createField("maturity_date", org.jooq.impl.SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column <code>alfa.viventor_loan_data.in_test_group</code>.
     */
    public final TableField<ViventorLoanDataRecord, Boolean> IN_TEST_GROUP = createField("in_test_group", org.jooq.impl.SQLDataType.BOOLEAN.defaultValue(org.jooq.impl.DSL.field("false", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * Create a <code>alfa.viventor_loan_data</code> table reference
     */
    public ViventorLoanData() {
        this("viventor_loan_data", null);
    }

    /**
     * Create an aliased <code>alfa.viventor_loan_data</code> table reference
     */
    public ViventorLoanData(String alias) {
        this(alias, VIVENTOR_LOAN_DATA);
    }

    private ViventorLoanData(String alias, Table<ViventorLoanDataRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViventorLoanData(String alias, Table<ViventorLoanDataRecord> aliased, Field<?>[] parameters) {
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
    public UniqueKey<ViventorLoanDataRecord> getPrimaryKey() {
        return Keys.VIVENTOR_LOAN_DATA_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ViventorLoanDataRecord>> getKeys() {
        return Arrays.<UniqueKey<ViventorLoanDataRecord>>asList(Keys.VIVENTOR_LOAN_DATA_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViventorLoanData as(String alias) {
        return new ViventorLoanData(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViventorLoanData rename(String name) {
        return new ViventorLoanData(name, null);
    }
}
