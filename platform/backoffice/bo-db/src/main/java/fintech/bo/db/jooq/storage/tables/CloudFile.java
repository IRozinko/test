/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.storage.tables;


import fintech.bo.db.jooq.storage.Keys;
import fintech.bo.db.jooq.storage.Storage;
import fintech.bo.db.jooq.storage.tables.records.CloudFileRecord;

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
public class CloudFile extends TableImpl<CloudFileRecord> {

    private static final long serialVersionUID = 953582451;

    /**
     * The reference instance of <code>storage.cloud_file</code>
     */
    public static final CloudFile CLOUD_FILE = new CloudFile();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CloudFileRecord> getRecordType() {
        return CloudFileRecord.class;
    }

    /**
     * The column <code>storage.cloud_file.id</code>.
     */
    public final TableField<CloudFileRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('storage.cloud_file_id_seq'::regclass)", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * The column <code>storage.cloud_file.created_at</code>.
     */
    public final TableField<CloudFileRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>storage.cloud_file.created_by</code>.
     */
    public final TableField<CloudFileRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>storage.cloud_file.entity_version</code>.
     */
    public final TableField<CloudFileRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>storage.cloud_file.updated_at</code>.
     */
    public final TableField<CloudFileRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>storage.cloud_file.updated_by</code>.
     */
    public final TableField<CloudFileRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>storage.cloud_file.content_type</code>.
     */
    public final TableField<CloudFileRecord, String> CONTENT_TYPE = createField("content_type", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>storage.cloud_file.directory</code>.
     */
    public final TableField<CloudFileRecord, String> DIRECTORY = createField("directory", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>storage.cloud_file.file_size</code>.
     */
    public final TableField<CloudFileRecord, Long> FILE_SIZE = createField("file_size", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>storage.cloud_file.file_uuid</code>.
     */
    public final TableField<CloudFileRecord, String> FILE_UUID = createField("file_uuid", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>storage.cloud_file.last_downloaded_at</code>.
     */
    public final TableField<CloudFileRecord, LocalDateTime> LAST_DOWNLOADED_AT = createField("last_downloaded_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>storage.cloud_file.original_file_name</code>.
     */
    public final TableField<CloudFileRecord, String> ORIGINAL_FILE_NAME = createField("original_file_name", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>storage.cloud_file.times_downloaded</code>.
     */
    public final TableField<CloudFileRecord, Long> TIMES_DOWNLOADED = createField("times_downloaded", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>storage.cloud_file</code> table reference
     */
    public CloudFile() {
        this("cloud_file", null);
    }

    /**
     * Create an aliased <code>storage.cloud_file</code> table reference
     */
    public CloudFile(String alias) {
        this(alias, CLOUD_FILE);
    }

    private CloudFile(String alias, Table<CloudFileRecord> aliased) {
        this(alias, aliased, null);
    }

    private CloudFile(String alias, Table<CloudFileRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Storage.STORAGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<CloudFileRecord, Long> getIdentity() {
        return Keys.IDENTITY_CLOUD_FILE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<CloudFileRecord> getPrimaryKey() {
        return Keys.CLOUD_FILE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<CloudFileRecord>> getKeys() {
        return Arrays.<UniqueKey<CloudFileRecord>>asList(Keys.CLOUD_FILE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CloudFile as(String alias) {
        return new CloudFile(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public CloudFile rename(String name) {
        return new CloudFile(name, null);
    }
}
