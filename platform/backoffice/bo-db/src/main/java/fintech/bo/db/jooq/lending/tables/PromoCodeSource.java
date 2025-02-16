/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.lending.tables;


import fintech.bo.db.jooq.lending.Keys;
import fintech.bo.db.jooq.lending.Lending;
import fintech.bo.db.jooq.lending.tables.records.PromoCodeSourceRecord;

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
public class PromoCodeSource extends TableImpl<PromoCodeSourceRecord> {

    private static final long serialVersionUID = -872482405;

    /**
     * The reference instance of <code>lending.promo_code_source</code>
     */
    public static final PromoCodeSource PROMO_CODE_SOURCE = new PromoCodeSource();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PromoCodeSourceRecord> getRecordType() {
        return PromoCodeSourceRecord.class;
    }

    /**
     * The column <code>lending.promo_code_source.id</code>.
     */
    public final TableField<PromoCodeSourceRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>lending.promo_code_source.entity_version</code>.
     */
    public final TableField<PromoCodeSourceRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>lending.promo_code_source.created_at</code>.
     */
    public final TableField<PromoCodeSourceRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>lending.promo_code_source.created_by</code>.
     */
    public final TableField<PromoCodeSourceRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>lending.promo_code_source.updated_at</code>.
     */
    public final TableField<PromoCodeSourceRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>lending.promo_code_source.updated_by</code>.
     */
    public final TableField<PromoCodeSourceRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>lending.promo_code_source.promo_code_id</code>.
     */
    public final TableField<PromoCodeSourceRecord, Long> PROMO_CODE_ID = createField("promo_code_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>lending.promo_code_source.source</code>.
     */
    public final TableField<PromoCodeSourceRecord, String> SOURCE = createField("source", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * Create a <code>lending.promo_code_source</code> table reference
     */
    public PromoCodeSource() {
        this("promo_code_source", null);
    }

    /**
     * Create an aliased <code>lending.promo_code_source</code> table reference
     */
    public PromoCodeSource(String alias) {
        this(alias, PROMO_CODE_SOURCE);
    }

    private PromoCodeSource(String alias, Table<PromoCodeSourceRecord> aliased) {
        this(alias, aliased, null);
    }

    private PromoCodeSource(String alias, Table<PromoCodeSourceRecord> aliased, Field<?>[] parameters) {
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
    public UniqueKey<PromoCodeSourceRecord> getPrimaryKey() {
        return Keys.PROMO_CODE_SOURCE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<PromoCodeSourceRecord>> getKeys() {
        return Arrays.<UniqueKey<PromoCodeSourceRecord>>asList(Keys.PROMO_CODE_SOURCE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<PromoCodeSourceRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<PromoCodeSourceRecord, ?>>asList(Keys.PROMO_CODE_SOURCE__PROMO_CODE_SOURCE_PROMO_CODE_ID_FKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PromoCodeSource as(String alias) {
        return new PromoCodeSource(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PromoCodeSource rename(String name) {
        return new PromoCodeSource(name, null);
    }
}
