/*
 * This file is generated by jOOQ.
*/
package fintech.spain.unnax.db.jooq.tables;


import fintech.spain.unnax.db.jooq.Keys;
import fintech.spain.unnax.db.jooq.SpainUnnax;
import fintech.spain.unnax.db.jooq.tables.records.CreditCardRecord;
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
public class CreditCard extends TableImpl<CreditCardRecord> {

    private static final long serialVersionUID = -294554016;

    /**
     * The reference instance of <code>spain_unnax.credit_card</code>
     */
    public static final CreditCard CREDIT_CARD = new CreditCard();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CreditCardRecord> getRecordType() {
        return CreditCardRecord.class;
    }

    /**
     * The column <code>spain_unnax.credit_card.id</code>.
     */
    public final TableField<CreditCardRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>spain_unnax.credit_card.created_at</code>.
     */
    public final TableField<CreditCardRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>spain_unnax.credit_card.created_by</code>.
     */
    public final TableField<CreditCardRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>spain_unnax.credit_card.entity_version</code>.
     */
    public final TableField<CreditCardRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>spain_unnax.credit_card.updated_at</code>.
     */
    public final TableField<CreditCardRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>spain_unnax.credit_card.updated_by</code>.
     */
    public final TableField<CreditCardRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>spain_unnax.credit_card.active</code>.
     */
    public final TableField<CreditCardRecord, Boolean> ACTIVE = createField("active", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>spain_unnax.credit_card.callback_transaction_id</code>.
     */
    public final TableField<CreditCardRecord, String> CALLBACK_TRANSACTION_ID = createField("callback_transaction_id", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>spain_unnax.credit_card.card_brand</code>.
     */
    public final TableField<CreditCardRecord, String> CARD_BRAND = createField("card_brand", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>spain_unnax.credit_card.card_bank</code>.
     */
    public final TableField<CreditCardRecord, String> CARD_BANK = createField("card_bank", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>spain_unnax.credit_card.card_expire_month</code>.
     */
    public final TableField<CreditCardRecord, Long> CARD_EXPIRE_MONTH = createField("card_expire_month", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>spain_unnax.credit_card.card_expire_year</code>.
     */
    public final TableField<CreditCardRecord, Long> CARD_EXPIRE_YEAR = createField("card_expire_year", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>spain_unnax.credit_card.card_holder_name</code>.
     */
    public final TableField<CreditCardRecord, String> CARD_HOLDER_NAME = createField("card_holder_name", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>spain_unnax.credit_card.card_token</code>.
     */
    public final TableField<CreditCardRecord, String> CARD_TOKEN = createField("card_token", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>spain_unnax.credit_card.order_code</code>.
     */
    public final TableField<CreditCardRecord, String> ORDER_CODE = createField("order_code", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>spain_unnax.credit_card.status</code>.
     */
    public final TableField<CreditCardRecord, String> STATUS = createField("status", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>spain_unnax.credit_card.error_details</code>.
     */
    public final TableField<CreditCardRecord, String> ERROR_DETAILS = createField("error_details", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>spain_unnax.credit_card.automatic_payment_enabled</code>.
     */
    public final TableField<CreditCardRecord, Boolean> AUTOMATIC_PAYMENT_ENABLED = createField("automatic_payment_enabled", org.jooq.impl.SQLDataType.BOOLEAN.defaultValue(org.jooq.impl.DSL.field("false", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>spain_unnax.credit_card.bin</code>.
     */
    public final TableField<CreditCardRecord, Long> BIN = createField("bin", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>spain_unnax.credit_card.pan</code>.
     */
    public final TableField<CreditCardRecord, String> PAN = createField("pan", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>spain_unnax.credit_card.client_number</code>.
     */
    public final TableField<CreditCardRecord, String> CLIENT_NUMBER = createField("client_number", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>spain_unnax.credit_card</code> table reference
     */
    public CreditCard() {
        this("credit_card", null);
    }

    /**
     * Create an aliased <code>spain_unnax.credit_card</code> table reference
     */
    public CreditCard(String alias) {
        this(alias, CREDIT_CARD);
    }

    private CreditCard(String alias, Table<CreditCardRecord> aliased) {
        this(alias, aliased, null);
    }

    private CreditCard(String alias, Table<CreditCardRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return SpainUnnax.SPAIN_UNNAX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<CreditCardRecord> getPrimaryKey() {
        return Keys.CREDIT_CARD_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<CreditCardRecord>> getKeys() {
        return Arrays.<UniqueKey<CreditCardRecord>>asList(Keys.CREDIT_CARD_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreditCard as(String alias) {
        return new CreditCard(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public CreditCard rename(String name) {
        return new CreditCard(name, null);
    }
}
