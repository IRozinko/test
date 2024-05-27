/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.checklist.tables;


import fintech.bo.db.jooq.checklist.Checklist;
import fintech.bo.db.jooq.checklist.Keys;
import fintech.bo.db.jooq.checklist.tables.records.ChecklistTypeRecord;
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
public class ChecklistType extends TableImpl<ChecklistTypeRecord> {

    private static final long serialVersionUID = 1086634728;

    /**
     * The reference instance of <code>checklist.checklist_type</code>
     */
    public static final ChecklistType CHECKLIST_TYPE = new ChecklistType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ChecklistTypeRecord> getRecordType() {
        return ChecklistTypeRecord.class;
    }

    /**
     * The column <code>checklist.checklist_type.id</code>.
     */
    public final TableField<ChecklistTypeRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>checklist.checklist_type.created_at</code>.
     */
    public final TableField<ChecklistTypeRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>checklist.checklist_type.created_by</code>.
     */
    public final TableField<ChecklistTypeRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>checklist.checklist_type.entity_version</code>.
     */
    public final TableField<ChecklistTypeRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>checklist.checklist_type.updated_at</code>.
     */
    public final TableField<ChecklistTypeRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>checklist.checklist_type.updated_by</code>.
     */
    public final TableField<ChecklistTypeRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>checklist.checklist_type.action</code>.
     */
    public final TableField<ChecklistTypeRecord, String> ACTION = createField("action", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>checklist.checklist_type.type</code>.
     */
    public final TableField<ChecklistTypeRecord, String> TYPE = createField("type", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * Create a <code>checklist.checklist_type</code> table reference
     */
    public ChecklistType() {
        this("checklist_type", null);
    }

    /**
     * Create an aliased <code>checklist.checklist_type</code> table reference
     */
    public ChecklistType(String alias) {
        this(alias, CHECKLIST_TYPE);
    }

    private ChecklistType(String alias, Table<ChecklistTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private ChecklistType(String alias, Table<ChecklistTypeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Checklist.CHECKLIST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ChecklistTypeRecord> getPrimaryKey() {
        return Keys.CHECKLIST_TYPE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ChecklistTypeRecord>> getKeys() {
        return Arrays.<UniqueKey<ChecklistTypeRecord>>asList(Keys.CHECKLIST_TYPE_PKEY, Keys.UK_PBQ37JOVPO3RRW1SGSNOEIW1Y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChecklistType as(String alias) {
        return new ChecklistType(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ChecklistType rename(String name) {
        return new ChecklistType(name, null);
    }
}
