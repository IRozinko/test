/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.payment.tables;


import fintech.bo.db.jooq.payment.Keys;
import fintech.bo.db.jooq.payment.Payment;
import fintech.bo.db.jooq.payment.tables.records.InstitutionRecord;

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
public class Institution extends TableImpl<InstitutionRecord> {

    private static final long serialVersionUID = 1247165440;

    /**
     * The reference instance of <code>payment.institution</code>
     */
    public static final Institution INSTITUTION = new Institution();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<InstitutionRecord> getRecordType() {
        return InstitutionRecord.class;
    }

    /**
     * The column <code>payment.institution.id</code>.
     */
    public final TableField<InstitutionRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>payment.institution.created_at</code>.
     */
    public final TableField<InstitutionRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>payment.institution.created_by</code>.
     */
    public final TableField<InstitutionRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payment.institution.entity_version</code>.
     */
    public final TableField<InstitutionRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>payment.institution.updated_at</code>.
     */
    public final TableField<InstitutionRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>payment.institution.updated_by</code>.
     */
    public final TableField<InstitutionRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payment.institution.institution_type</code>.
     */
    public final TableField<InstitutionRecord, String> INSTITUTION_TYPE = createField("institution_type", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>payment.institution.name</code>.
     */
    public final TableField<InstitutionRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>payment.institution.payment_methods</code>.
     */
    public final TableField<InstitutionRecord, String> PAYMENT_METHODS = createField("payment_methods", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payment.institution.is_primary</code>.
     */
    public final TableField<InstitutionRecord, Boolean> IS_PRIMARY = createField("is_primary", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>payment.institution.statement_export_format</code>.
     */
    public final TableField<InstitutionRecord, String> STATEMENT_EXPORT_FORMAT = createField("statement_export_format", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payment.institution.statement_export_params_json</code>.
     */
    public final TableField<InstitutionRecord, String> STATEMENT_EXPORT_PARAMS_JSON = createField("statement_export_params_json", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payment.institution.statement_import_format</code>.
     */
    public final TableField<InstitutionRecord, String> STATEMENT_IMPORT_FORMAT = createField("statement_import_format", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payment.institution.disabled</code>.
     */
    public final TableField<InstitutionRecord, Boolean> DISABLED = createField("disabled", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("false", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>payment.institution.code</code>.
     */
    public final TableField<InstitutionRecord, String> CODE = createField("code", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payment.institution.statement_api_exporter</code>.
     */
    public final TableField<InstitutionRecord, String> STATEMENT_API_EXPORTER = createField("statement_api_exporter", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>payment.institution</code> table reference
     */
    public Institution() {
        this("institution", null);
    }

    /**
     * Create an aliased <code>payment.institution</code> table reference
     */
    public Institution(String alias) {
        this(alias, INSTITUTION);
    }

    private Institution(String alias, Table<InstitutionRecord> aliased) {
        this(alias, aliased, null);
    }

    private Institution(String alias, Table<InstitutionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Payment.PAYMENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<InstitutionRecord> getPrimaryKey() {
        return Keys.INSTITUTION_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<InstitutionRecord>> getKeys() {
        return Arrays.<UniqueKey<InstitutionRecord>>asList(Keys.INSTITUTION_PKEY, Keys.UK_QHW15H5F7NC4G3NDVA8SORY1U);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Institution as(String alias) {
        return new Institution(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Institution rename(String name) {
        return new Institution(name, null);
    }
}
