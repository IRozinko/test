/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.presence.tables;


import fintech.bo.db.jooq.presence.Keys;
import fintech.bo.db.jooq.presence.Presence;
import fintech.bo.db.jooq.presence.tables.records.OutboundLoadRecordRecord;
import org.jooq.Field;
import org.jooq.ForeignKey;
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
public class OutboundLoadRecord extends TableImpl<OutboundLoadRecordRecord> {

    private static final long serialVersionUID = -630983946;

    /**
     * The reference instance of <code>presence.outbound_load_record</code>
     */
    public static final OutboundLoadRecord OUTBOUND_LOAD_RECORD = new OutboundLoadRecord();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<OutboundLoadRecordRecord> getRecordType() {
        return OutboundLoadRecordRecord.class;
    }

    /**
     * The column <code>presence.outbound_load_record.id</code>.
     */
    public final TableField<OutboundLoadRecordRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>presence.outbound_load_record.entity_version</code>.
     */
    public final TableField<OutboundLoadRecordRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>presence.outbound_load_record.created_at</code>.
     */
    public final TableField<OutboundLoadRecordRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>presence.outbound_load_record.updated_at</code>.
     */
    public final TableField<OutboundLoadRecordRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>presence.outbound_load_record.created_by</code>.
     */
    public final TableField<OutboundLoadRecordRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>presence.outbound_load_record.updated_by</code>.
     */
    public final TableField<OutboundLoadRecordRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>presence.outbound_load_record.source_id</code>.
     */
    public final TableField<OutboundLoadRecordRecord, Integer> SOURCE_ID = createField("source_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>presence.outbound_load_record.name</code>.
     */
    public final TableField<OutboundLoadRecordRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>presence.outbound_load_record.status</code>.
     */
    public final TableField<OutboundLoadRecordRecord, String> STATUS = createField("status", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>presence.outbound_load_record.qualification_code</code>.
     */
    public final TableField<OutboundLoadRecordRecord, Short> QUALIFICATION_CODE = createField("qualification_code", org.jooq.impl.SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>presence.outbound_load_record.outbound_load_id</code>.
     */
    public final TableField<OutboundLoadRecordRecord, Long> OUTBOUND_LOAD_ID = createField("outbound_load_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>presence.outbound_load_record</code> table reference
     */
    public OutboundLoadRecord() {
        this("outbound_load_record", null);
    }

    /**
     * Create an aliased <code>presence.outbound_load_record</code> table reference
     */
    public OutboundLoadRecord(String alias) {
        this(alias, OUTBOUND_LOAD_RECORD);
    }

    private OutboundLoadRecord(String alias, Table<OutboundLoadRecordRecord> aliased) {
        this(alias, aliased, null);
    }

    private OutboundLoadRecord(String alias, Table<OutboundLoadRecordRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Presence.PRESENCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<OutboundLoadRecordRecord> getPrimaryKey() {
        return Keys.OUTBOUND_LOAD_RECORD_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<OutboundLoadRecordRecord>> getKeys() {
        return Arrays.<UniqueKey<OutboundLoadRecordRecord>>asList(Keys.OUTBOUND_LOAD_RECORD_PKEY, Keys.OUTBOUND_LOAD_RECORD_SOURCE_ID_OUTBOUND_LOAD_ID_KEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<OutboundLoadRecordRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<OutboundLoadRecordRecord, ?>>asList(Keys.OUTBOUND_LOAD_RECORD__FK_OUTBOUND_LOAD_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutboundLoadRecord as(String alias) {
        return new OutboundLoadRecord(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public OutboundLoadRecord rename(String name) {
        return new OutboundLoadRecord(name, null);
    }
}
