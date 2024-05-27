/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.lending.tables;


import fintech.bo.db.jooq.lending.Keys;
import fintech.bo.db.jooq.lending.Lending;
import fintech.bo.db.jooq.lending.tables.records.LoanContractRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
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
public class LoanContract extends TableImpl<LoanContractRecord> {

    private static final long serialVersionUID = -16822036;

    /**
     * The reference instance of <code>lending.loan_contract</code>
     */
    public static final LoanContract LOAN_CONTRACT = new LoanContract();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LoanContractRecord> getRecordType() {
        return LoanContractRecord.class;
    }

    /**
     * The column <code>lending.loan_contract.id</code>.
     */
    public final TableField<LoanContractRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>lending.loan_contract.created_at</code>.
     */
    public final TableField<LoanContractRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>lending.loan_contract.created_by</code>.
     */
    public final TableField<LoanContractRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>lending.loan_contract.entity_version</code>.
     */
    public final TableField<LoanContractRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>lending.loan_contract.updated_at</code>.
     */
    public final TableField<LoanContractRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>lending.loan_contract.updated_by</code>.
     */
    public final TableField<LoanContractRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>lending.loan_contract.product_id</code>.
     */
    public final TableField<LoanContractRecord, Long> PRODUCT_ID = createField("product_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>lending.loan_contract.loan_id</code>.
     */
    public final TableField<LoanContractRecord, Long> LOAN_ID = createField("loan_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>lending.loan_contract.client_id</code>.
     */
    public final TableField<LoanContractRecord, Long> CLIENT_ID = createField("client_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>lending.loan_contract.application_id</code>.
     */
    public final TableField<LoanContractRecord, Long> APPLICATION_ID = createField("application_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>lending.loan_contract.contract_date</code>.
     */
    public final TableField<LoanContractRecord, LocalDate> CONTRACT_DATE = createField("contract_date", org.jooq.impl.SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column <code>lending.loan_contract.effective_date</code>.
     */
    public final TableField<LoanContractRecord, LocalDate> EFFECTIVE_DATE = createField("effective_date", org.jooq.impl.SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column <code>lending.loan_contract.maturity_date</code>.
     */
    public final TableField<LoanContractRecord, LocalDate> MATURITY_DATE = createField("maturity_date", org.jooq.impl.SQLDataType.LOCALDATE.nullable(false), this, "");

    /**
     * The column <code>lending.loan_contract.current</code>.
     */
    public final TableField<LoanContractRecord, Boolean> CURRENT = createField("current", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>lending.loan_contract.period_count</code>.
     */
    public final TableField<LoanContractRecord, Integer> PERIOD_COUNT = createField("period_count", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>lending.loan_contract.period_unit</code>.
     */
    public final TableField<LoanContractRecord, String> PERIOD_UNIT = createField("period_unit", org.jooq.impl.SQLDataType.CLOB.nullable(false).defaultValue(org.jooq.impl.DSL.field("'NA'::text", org.jooq.impl.SQLDataType.CLOB)), this, "");

    /**
     * The column <code>lending.loan_contract.number_of_installments</code>.
     */
    public final TableField<LoanContractRecord, Integer> NUMBER_OF_INSTALLMENTS = createField("number_of_installments", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>lending.loan_contract.close_loan_on_paid</code>.
     */
    public final TableField<LoanContractRecord, Boolean> CLOSE_LOAN_ON_PAID = createField("close_loan_on_paid", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("true", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>lending.loan_contract.base_overdue_days</code>.
     */
    public final TableField<LoanContractRecord, Integer> BASE_OVERDUE_DAYS = createField("base_overdue_days", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>lending.loan_contract.previous_contract_id</code>.
     */
    public final TableField<LoanContractRecord, Long> PREVIOUS_CONTRACT_ID = createField("previous_contract_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>lending.loan_contract.source_transaction_id</code>.
     */
    public final TableField<LoanContractRecord, Long> SOURCE_TRANSACTION_ID = createField("source_transaction_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>lending.loan_contract.source_transaction_type</code>.
     */
    public final TableField<LoanContractRecord, String> SOURCE_TRANSACTION_TYPE = createField("source_transaction_type", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>lending.loan_contract</code> table reference
     */
    public LoanContract() {
        this("loan_contract", null);
    }

    /**
     * Create an aliased <code>lending.loan_contract</code> table reference
     */
    public LoanContract(String alias) {
        this(alias, LOAN_CONTRACT);
    }

    private LoanContract(String alias, Table<LoanContractRecord> aliased) {
        this(alias, aliased, null);
    }

    private LoanContract(String alias, Table<LoanContractRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Lending.LENDING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<LoanContractRecord> getPrimaryKey() {
        return Keys.LOAN_CONTRACT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<LoanContractRecord>> getKeys() {
        return Arrays.<UniqueKey<LoanContractRecord>>asList(Keys.LOAN_CONTRACT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<LoanContractRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<LoanContractRecord, ?>>asList(Keys.LOAN_CONTRACT__LOAN_CONTRACT_PRODUCT_ID, Keys.LOAN_CONTRACT__LOAN_CONTRACT_LOAN_ID, Keys.LOAN_CONTRACT__LOAN_CONTRACT_APPLICATION_ID, Keys.LOAN_CONTRACT__LOAN_CONTRACT_PREVIOUS_CONTRACT_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoanContract as(String alias) {
        return new LoanContract(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public LoanContract rename(String name) {
        return new LoanContract(name, null);
    }
}
