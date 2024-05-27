/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.dowjones.tables;


import fintech.bo.db.jooq.dowjones.Dowjones;
import fintech.bo.db.jooq.dowjones.Keys;
import fintech.bo.db.jooq.dowjones.tables.records.RequestRecord;
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
public class Request extends TableImpl<RequestRecord> {

    private static final long serialVersionUID = -1851405533;

    /**
     * The reference instance of <code>dowjones.request</code>
     */
    public static final Request REQUEST = new Request();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RequestRecord> getRecordType() {
        return RequestRecord.class;
    }

    /**
     * The column <code>dowjones.request.id</code>.
     */
    public final TableField<RequestRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>dowjones.request.created_at</code>.
     */
    public final TableField<RequestRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>dowjones.request.created_by</code>.
     */
    public final TableField<RequestRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>dowjones.request.entity_version</code>.
     */
    public final TableField<RequestRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>dowjones.request.updated_at</code>.
     */
    public final TableField<RequestRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>dowjones.request.updated_by</code>.
     */
    public final TableField<RequestRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>dowjones.request.request_body</code>.
     */
    public final TableField<RequestRecord, String> REQUEST_BODY = createField("request_body", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>dowjones.request.response_body</code>.
     */
    public final TableField<RequestRecord, String> RESPONSE_BODY = createField("response_body", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>dowjones.request.request_url</code>.
     */
    public final TableField<RequestRecord, String> REQUEST_URL = createField("request_url", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>dowjones.request.response_status_code</code>.
     */
    public final TableField<RequestRecord, Integer> RESPONSE_STATUS_CODE = createField("response_status_code", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>dowjones.request.client_id</code>.
     */
    public final TableField<RequestRecord, Long> CLIENT_ID = createField("client_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>dowjones.request.status</code>.
     */
    public final TableField<RequestRecord, String> STATUS = createField("status", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>dowjones.request.error</code>.
     */
    public final TableField<RequestRecord, String> ERROR = createField("error", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>dowjones.request</code> table reference
     */
    public Request() {
        this("request", null);
    }

    /**
     * Create an aliased <code>dowjones.request</code> table reference
     */
    public Request(String alias) {
        this(alias, REQUEST);
    }

    private Request(String alias, Table<RequestRecord> aliased) {
        this(alias, aliased, null);
    }

    private Request(String alias, Table<RequestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Dowjones.DOWJONES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<RequestRecord> getPrimaryKey() {
        return Keys.REQUEST_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<RequestRecord>> getKeys() {
        return Arrays.<UniqueKey<RequestRecord>>asList(Keys.REQUEST_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Request as(String alias) {
        return new Request(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Request rename(String name) {
        return new Request(name, null);
    }
}
