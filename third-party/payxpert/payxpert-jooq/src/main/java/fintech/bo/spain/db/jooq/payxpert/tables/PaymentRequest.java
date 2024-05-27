/*
 * This file is generated by jOOQ.
*/
package fintech.bo.spain.db.jooq.payxpert.tables;


import fintech.bo.spain.db.jooq.payxpert.Keys;
import fintech.bo.spain.db.jooq.payxpert.Payxpert;
import fintech.bo.spain.db.jooq.payxpert.tables.records.PaymentRequestRecord;
import org.jooq.*;
import org.jooq.impl.TableImpl;

import javax.annotation.Generated;
import java.math.BigDecimal;
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
public class PaymentRequest extends TableImpl<PaymentRequestRecord> {

    private static final long serialVersionUID = 1975282955;

    /**
     * The reference instance of <code>payxpert.payment_request</code>
     */
    public static final PaymentRequest PAYMENT_REQUEST = new PaymentRequest();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PaymentRequestRecord> getRecordType() {
        return PaymentRequestRecord.class;
    }

    /**
     * The column <code>payxpert.payment_request.id</code>.
     */
    public final TableField<PaymentRequestRecord, Long> ID = createField("id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>payxpert.payment_request.created_at</code>.
     */
    public final TableField<PaymentRequestRecord, LocalDateTime> CREATED_AT = createField("created_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>payxpert.payment_request.created_by</code>.
     */
    public final TableField<PaymentRequestRecord, String> CREATED_BY = createField("created_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payxpert.payment_request.entity_version</code>.
     */
    public final TableField<PaymentRequestRecord, Long> ENTITY_VERSION = createField("entity_version", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>payxpert.payment_request.updated_at</code>.
     */
    public final TableField<PaymentRequestRecord, LocalDateTime> UPDATED_AT = createField("updated_at", org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>payxpert.payment_request.updated_by</code>.
     */
    public final TableField<PaymentRequestRecord, String> UPDATED_BY = createField("updated_by", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payxpert.payment_request.amount</code>.
     */
    public final TableField<PaymentRequestRecord, BigDecimal> AMOUNT = createField("amount", org.jooq.impl.SQLDataType.NUMERIC.precision(19, 2).nullable(false), this, "");

    /**
     * The column <code>payxpert.payment_request.callback_received_at</code>.
     */
    public final TableField<PaymentRequestRecord, LocalDateTime> CALLBACK_RECEIVED_AT = createField("callback_received_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>payxpert.payment_request.callback_transaction_id</code>.
     */
    public final TableField<PaymentRequestRecord, String> CALLBACK_TRANSACTION_ID = createField("callback_transaction_id", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payxpert.payment_request.card_brand</code>.
     */
    public final TableField<PaymentRequestRecord, String> CARD_BRAND = createField("card_brand", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payxpert.payment_request.card_expire_month</code>.
     */
    public final TableField<PaymentRequestRecord, Long> CARD_EXPIRE_MONTH = createField("card_expire_month", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>payxpert.payment_request.card_expire_year</code>.
     */
    public final TableField<PaymentRequestRecord, Long> CARD_EXPIRE_YEAR = createField("card_expire_year", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>payxpert.payment_request.card_holder_name</code>.
     */
    public final TableField<PaymentRequestRecord, String> CARD_HOLDER_NAME = createField("card_holder_name", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payxpert.payment_request.card_is3dsecure</code>.
     */
    public final TableField<PaymentRequestRecord, Boolean> CARD_IS3DSECURE = createField("card_is3dsecure", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>payxpert.payment_request.card_number</code>.
     */
    public final TableField<PaymentRequestRecord, String> CARD_NUMBER = createField("card_number", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payxpert.payment_request.client_id</code>.
     */
    public final TableField<PaymentRequestRecord, Long> CLIENT_ID = createField("client_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>payxpert.payment_request.ctrl_callback_url</code>.
     */
    public final TableField<PaymentRequestRecord, String> CTRL_CALLBACK_URL = createField("ctrl_callback_url", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>payxpert.payment_request.ctrl_redirect_url</code>.
     */
    public final TableField<PaymentRequestRecord, String> CTRL_REDIRECT_URL = createField("ctrl_redirect_url", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payxpert.payment_request.currency</code>.
     */
    public final TableField<PaymentRequestRecord, String> CURRENCY = createField("currency", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>payxpert.payment_request.customer_redirect_url</code>.
     */
    public final TableField<PaymentRequestRecord, String> CUSTOMER_REDIRECT_URL = createField("customer_redirect_url", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>payxpert.payment_request.customer_token</code>.
     */
    public final TableField<PaymentRequestRecord, String> CUSTOMER_TOKEN = createField("customer_token", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payxpert.payment_request.enable_recurring_payments</code>.
     */
    public final TableField<PaymentRequestRecord, Boolean> ENABLE_RECURRING_PAYMENTS = createField("enable_recurring_payments", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>payxpert.payment_request.error_code</code>.
     */
    public final TableField<PaymentRequestRecord, String> ERROR_CODE = createField("error_code", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payxpert.payment_request.error_message</code>.
     */
    public final TableField<PaymentRequestRecord, String> ERROR_MESSAGE = createField("error_message", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payxpert.payment_request.invoice_id</code>.
     */
    public final TableField<PaymentRequestRecord, Long> INVOICE_ID = createField("invoice_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>payxpert.payment_request.last_status_check_at</code>.
     */
    public final TableField<PaymentRequestRecord, LocalDateTime> LAST_STATUS_CHECK_AT = createField("last_status_check_at", org.jooq.impl.SQLDataType.LOCALDATETIME, this, "");

    /**
     * The column <code>payxpert.payment_request.loan_id</code>.
     */
    public final TableField<PaymentRequestRecord, Long> LOAN_ID = createField("loan_id", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>payxpert.payment_request.merchant_token</code>.
     */
    public final TableField<PaymentRequestRecord, String> MERCHANT_TOKEN = createField("merchant_token", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payxpert.payment_request.operation</code>.
     */
    public final TableField<PaymentRequestRecord, String> OPERATION = createField("operation", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>payxpert.payment_request.order_id</code>.
     */
    public final TableField<PaymentRequestRecord, String> ORDER_ID = createField("order_id", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>payxpert.payment_request.payment_type</code>.
     */
    public final TableField<PaymentRequestRecord, String> PAYMENT_TYPE = createField("payment_type", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>payxpert.payment_request.save_credit_card</code>.
     */
    public final TableField<PaymentRequestRecord, Boolean> SAVE_CREDIT_CARD = createField("save_credit_card", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");

    /**
     * The column <code>payxpert.payment_request.status</code>.
     */
    public final TableField<PaymentRequestRecord, String> STATUS = createField("status", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>payxpert.payment_request.status_check_attempts</code>.
     */
    public final TableField<PaymentRequestRecord, Long> STATUS_CHECK_ATTEMPTS = createField("status_check_attempts", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>payxpert.payment_request.status_detail</code>.
     */
    public final TableField<PaymentRequestRecord, String> STATUS_DETAIL = createField("status_detail", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>payxpert.payment_request</code> table reference
     */
    public PaymentRequest() {
        this("payment_request", null);
    }

    /**
     * Create an aliased <code>payxpert.payment_request</code> table reference
     */
    public PaymentRequest(String alias) {
        this(alias, PAYMENT_REQUEST);
    }

    private PaymentRequest(String alias, Table<PaymentRequestRecord> aliased) {
        this(alias, aliased, null);
    }

    private PaymentRequest(String alias, Table<PaymentRequestRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Payxpert.PAYXPERT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<PaymentRequestRecord> getPrimaryKey() {
        return Keys.PAYMENT_REQUEST_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<PaymentRequestRecord>> getKeys() {
        return Arrays.<UniqueKey<PaymentRequestRecord>>asList(Keys.PAYMENT_REQUEST_PKEY, Keys.UK_EQWNGVCT8F0FCUNBLWEEMWJO6);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentRequest as(String alias) {
        return new PaymentRequest(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PaymentRequest rename(String name) {
        return new PaymentRequest(name, null);
    }
}
