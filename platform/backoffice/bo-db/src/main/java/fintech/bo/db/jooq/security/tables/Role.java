/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.security.tables;


import fintech.bo.db.jooq.security.Keys;
import fintech.bo.db.jooq.security.Security;
import fintech.bo.db.jooq.security.tables.records.RoleRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
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
public class Role extends TableImpl<RoleRecord> {

    private static final long serialVersionUID = -1424649040;

    /**
     * The reference instance of <code>security.role</code>
     */
    public static final Role ROLE = new Role();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RoleRecord> getRecordType() {
        return RoleRecord.class;
    }

    /**
     * The column <code>security.role.id</code>.
     */
    public final TableField<RoleRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('security.role_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>security.role.created_at</code>.
     */
    public final TableField<RoleRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>security.role.created_by</code>.
     */
    public final TableField<RoleRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>security.role.entity_version</code>.
     */
    public final TableField<RoleRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>security.role.updated_at</code>.
     */
    public final TableField<RoleRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>security.role.updated_by</code>.
     */
    public final TableField<RoleRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>security.role.name</code>.
     */
    public final TableField<RoleRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * Create a <code>security.role</code> table reference
     */
    public Role() {
        this("role", null);
    }

    /**
     * Create an aliased <code>security.role</code> table reference
     */
    public Role(String alias) {
        this(alias, ROLE);
    }

    private Role(String alias, Table<RoleRecord> aliased) {
        this(alias, aliased, null);
    }

    private Role(String alias, Table<RoleRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Security.SECURITY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<RoleRecord, Long> getIdentity() {
        return Keys.IDENTITY_ROLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<RoleRecord> getPrimaryKey() {
        return Keys.ROLE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<RoleRecord>> getKeys() {
        return Arrays.<UniqueKey<RoleRecord>>asList(Keys.ROLE_PKEY, Keys.UK_8SEWWNPAMNGI6B1DWAA88ASKK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role as(String alias) {
        return new Role(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Role rename(String name) {
        return new Role(name, null);
    }
}
