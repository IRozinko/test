/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.crm.tables;


import fintech.bo.db.jooq.crm.Crm;
import fintech.bo.db.jooq.crm.Keys;
import fintech.bo.db.jooq.crm.tables.records.ClientAttachmentRecord;

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
public class ClientAttachment extends TableImpl<ClientAttachmentRecord> {

    private static final long serialVersionUID = 2128898645;

    /**
     * The reference instance of <code>crm.client_attachment</code>
     */
    public static final ClientAttachment CLIENT_ATTACHMENT = new ClientAttachment();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ClientAttachmentRecord> getRecordType() {
        return ClientAttachmentRecord.class;
    }

    /**
     * The column <code>crm.client_attachment.id</code>.
     */
    public final TableField<ClientAttachmentRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>crm.client_attachment.created_at</code>.
     */
    public final TableField<ClientAttachmentRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>crm.client_attachment.created_by</code>.
     */
    public final TableField<ClientAttachmentRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>crm.client_attachment.entity_version</code>.
     */
    public final TableField<ClientAttachmentRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>crm.client_attachment.updated_at</code>.
     */
    public final TableField<ClientAttachmentRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>crm.client_attachment.updated_by</code>.
     */
    public final TableField<ClientAttachmentRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>crm.client_attachment.application_id</code>.
     */
    public final TableField<ClientAttachmentRecord, Long> APPLICATION_ID = createField("application_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>crm.client_attachment.file_id</code>.
     */
    public final TableField<ClientAttachmentRecord, Long> FILE_ID = createField("file_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>crm.client_attachment.attachment_group</code>.
     */
    public final TableField<ClientAttachmentRecord, String> ATTACHMENT_GROUP = createField("attachment_group", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>crm.client_attachment.loan_id</code>.
     */
    public final TableField<ClientAttachmentRecord, Long> LOAN_ID = createField("loan_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>crm.client_attachment.name</code>.
     */
    public final TableField<ClientAttachmentRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>crm.client_attachment.status</code>.
     */
    public final TableField<ClientAttachmentRecord, String> STATUS = createField("status", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>crm.client_attachment.status_detail</code>.
     */
    public final TableField<ClientAttachmentRecord, String> STATUS_DETAIL = createField("status_detail", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>crm.client_attachment.attachment_type</code>.
     */
    public final TableField<ClientAttachmentRecord, String> ATTACHMENT_TYPE = createField("attachment_type", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>crm.client_attachment.client_id</code>.
     */
    public final TableField<ClientAttachmentRecord, Long> CLIENT_ID = createField("client_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>crm.client_attachment.attachment_sub_type</code>.
     */
    public final TableField<ClientAttachmentRecord, String> ATTACHMENT_SUB_TYPE = createField("attachment_sub_type", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>crm.client_attachment.transaction_id</code>.
     */
    public final TableField<ClientAttachmentRecord, Long> TRANSACTION_ID = createField("transaction_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>crm.client_attachment.auto_approve</code>.
     */
    public final TableField<ClientAttachmentRecord, Boolean> AUTO_APPROVE = createField("auto_approve", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.field("false", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>crm.client_attachment.auto_approve_term</code>.
     */
    public final TableField<ClientAttachmentRecord, Integer> AUTO_APPROVE_TERM = createField("auto_approve_term", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * Create a <code>crm.client_attachment</code> table reference
     */
    public ClientAttachment() {
        this("client_attachment", null);
    }

    /**
     * Create an aliased <code>crm.client_attachment</code> table reference
     */
    public ClientAttachment(String alias) {
        this(alias, CLIENT_ATTACHMENT);
    }

    private ClientAttachment(String alias, Table<ClientAttachmentRecord> aliased) {
        this(alias, aliased, null);
    }

    private ClientAttachment(String alias, Table<ClientAttachmentRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Crm.CRM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ClientAttachmentRecord> getPrimaryKey() {
        return Keys.CLIENT_ATTACHMENT_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ClientAttachmentRecord>> getKeys() {
        return Arrays.<UniqueKey<ClientAttachmentRecord>>asList(Keys.CLIENT_ATTACHMENT_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<ClientAttachmentRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ClientAttachmentRecord, ?>>asList(Keys.CLIENT_ATTACHMENT__FK_CLIENT_ATTACHMENT_CLIENT_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientAttachment as(String alias) {
        return new ClientAttachment(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ClientAttachment rename(String name) {
        return new ClientAttachment(name, null);
    }
}
