/*
 * This file is generated by jOOQ.
*/
package fintech.bo.spain.db.jooq.experian.tables;


import fintech.bo.spain.db.jooq.experian.Keys;
import fintech.bo.spain.db.jooq.experian.SpainExperian;
import fintech.bo.spain.db.jooq.experian.tables.records.CaisOperacionesRecord;
import org.jooq.*;
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
public class CaisOperaciones extends TableImpl<CaisOperacionesRecord> {

    private static final long serialVersionUID = -1494423218;

    /**
     * The reference instance of <code>spain_experian.cais_operaciones</code>
     */
    public static final CaisOperaciones CAIS_OPERACIONES = new CaisOperaciones();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CaisOperacionesRecord> getRecordType() {
        return CaisOperacionesRecord.class;
    }

    /**
     * The column <code>spain_experian.cais_operaciones.id</code>.
     */
    public final TableField<CaisOperacionesRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>spain_experian.cais_operaciones.created_at</code>.
     */
    public final TableField<CaisOperacionesRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>spain_experian.cais_operaciones.created_by</code>.
     */
    public final TableField<CaisOperacionesRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>spain_experian.cais_operaciones.entity_version</code>.
     */
    public final TableField<CaisOperacionesRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>spain_experian.cais_operaciones.updated_at</code>.
     */
    public final TableField<CaisOperacionesRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>spain_experian.cais_operaciones.updated_by</code>.
     */
    public final TableField<CaisOperacionesRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>spain_experian.cais_operaciones.application_id</code>.
     */
    public final TableField<CaisOperacionesRecord, Long> APPLICATION_ID = createField("application_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>spain_experian.cais_operaciones.client_id</code>.
     */
    public final TableField<CaisOperacionesRecord, Long> CLIENT_ID = createField("client_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>spain_experian.cais_operaciones.document_number</code>.
     */
    public final TableField<CaisOperacionesRecord, String> DOCUMENT_NUMBER = createField("document_number", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>spain_experian.cais_operaciones.error</code>.
     */
    public final TableField<CaisOperacionesRecord, String> ERROR = createField("error", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>spain_experian.cais_operaciones.numero_registros_devueltos</code>.
     */
    public final TableField<CaisOperacionesRecord, Integer> NUMERO_REGISTROS_DEVUELTOS = createField("numero_registros_devueltos", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>spain_experian.cais_operaciones.request_body</code>.
     */
    public final TableField<CaisOperacionesRecord, String> REQUEST_BODY = createField("request_body", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>spain_experian.cais_operaciones.response_body</code>.
     */
    public final TableField<CaisOperacionesRecord, String> RESPONSE_BODY = createField("response_body", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>spain_experian.cais_operaciones.status</code>.
     */
    public final TableField<CaisOperacionesRecord, String> STATUS = createField("status", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * Create a <code>spain_experian.cais_operaciones</code> table reference
     */
    public CaisOperaciones() {
        this("cais_operaciones", null);
    }

    /**
     * Create an aliased <code>spain_experian.cais_operaciones</code> table reference
     */
    public CaisOperaciones(String alias) {
        this(alias, CAIS_OPERACIONES);
    }

    private CaisOperaciones(String alias, Table<CaisOperacionesRecord> aliased) {
        this(alias, aliased, null);
    }

    private CaisOperaciones(String alias, Table<CaisOperacionesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return SpainExperian.SPAIN_EXPERIAN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<CaisOperacionesRecord> getPrimaryKey() {
        return Keys.CAIS_OPERACIONES_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<CaisOperacionesRecord>> getKeys() {
        return Arrays.<UniqueKey<CaisOperacionesRecord>>asList(Keys.CAIS_OPERACIONES_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaisOperaciones as(String alias) {
        return new CaisOperaciones(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public CaisOperaciones rename(String name) {
        return new CaisOperaciones(name, null);
    }
}
