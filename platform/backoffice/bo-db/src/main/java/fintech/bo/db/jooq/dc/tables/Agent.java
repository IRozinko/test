/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.dc.tables;


import fintech.bo.db.jooq.dc.Dc;
import fintech.bo.db.jooq.dc.Keys;
import fintech.bo.db.jooq.dc.tables.records.AgentRecord;

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
public class Agent extends TableImpl<AgentRecord> {

    private static final long serialVersionUID = -1315390344;

    /**
     * The reference instance of <code>dc.agent</code>
     */
    public static final Agent AGENT = new Agent();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<AgentRecord> getRecordType() {
        return AgentRecord.class;
    }

    /**
     * The column <code>dc.agent.id</code>.
     */
    public final TableField<AgentRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>dc.agent.created_at</code>.
     */
    public final TableField<AgentRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>dc.agent.created_by</code>.
     */
    public final TableField<AgentRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>dc.agent.entity_version</code>.
     */
    public final TableField<AgentRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>dc.agent.updated_at</code>.
     */
    public final TableField<AgentRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>dc.agent.updated_by</code>.
     */
    public final TableField<AgentRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>dc.agent.agent</code>.
     */
    public final TableField<AgentRecord, String> AGENT_ = createField("agent", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>dc.agent.disabled</code>.
     */
    public final TableField<AgentRecord, Boolean> DISABLED = createField("disabled", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * Create a <code>dc.agent</code> table reference
     */
    public Agent() {
        this("agent", null);
    }

    /**
     * Create an aliased <code>dc.agent</code> table reference
     */
    public Agent(String alias) {
        this(alias, AGENT);
    }

    private Agent(String alias, Table<AgentRecord> aliased) {
        this(alias, aliased, null);
    }

    private Agent(String alias, Table<AgentRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Dc.DC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<AgentRecord> getPrimaryKey() {
        return Keys.AGENT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<AgentRecord>> getKeys() {
        return Arrays.<UniqueKey<AgentRecord>>asList(Keys.AGENT_PKEY, Keys.UK_5IFYR83T40XPP54DY2DCDN6JU);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Agent as(String alias) {
        return new Agent(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Agent rename(String name) {
        return new Agent(name, null);
    }
}
