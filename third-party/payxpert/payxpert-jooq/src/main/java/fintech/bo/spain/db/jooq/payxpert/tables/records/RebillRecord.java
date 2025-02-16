/*
 * This file is generated by jOOQ.
*/
package fintech.bo.spain.db.jooq.payxpert.tables.records;


import fintech.bo.spain.db.jooq.payxpert.tables.Rebill;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record19;
import org.jooq.Row19;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;
import java.math.BigDecimal;
import java.time.LocalDateTime;


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
public class RebillRecord extends UpdatableRecordImpl<RebillRecord> implements Record19<Long, LocalDateTime, String, Long, LocalDateTime, String, BigDecimal, Long, String, String, String, Long, Long, LocalDateTime, Long, String, String, String, Long> {

    private static final long serialVersionUID = 247183739;

    /**
     * Setter for <code>payxpert.rebill.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>payxpert.rebill.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>payxpert.rebill.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>payxpert.rebill.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>payxpert.rebill.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>payxpert.rebill.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>payxpert.rebill.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>payxpert.rebill.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>payxpert.rebill.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>payxpert.rebill.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>payxpert.rebill.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>payxpert.rebill.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>payxpert.rebill.amount</code>.
     */
    public void setAmount(BigDecimal value) {
        set(6, value);
    }

    /**
     * Getter for <code>payxpert.rebill.amount</code>.
     */
    public BigDecimal getAmount() {
        return (BigDecimal) get(6);
    }

    /**
     * Setter for <code>payxpert.rebill.client_id</code>.
     */
    public void setClientId(Long value) {
        set(7, value);
    }

    /**
     * Getter for <code>payxpert.rebill.client_id</code>.
     */
    public Long getClientId() {
        return (Long) get(7);
    }

    /**
     * Setter for <code>payxpert.rebill.currency</code>.
     */
    public void setCurrency(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>payxpert.rebill.currency</code>.
     */
    public String getCurrency() {
        return (String) get(8);
    }

    /**
     * Setter for <code>payxpert.rebill.error_code</code>.
     */
    public void setErrorCode(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>payxpert.rebill.error_code</code>.
     */
    public String getErrorCode() {
        return (String) get(9);
    }

    /**
     * Setter for <code>payxpert.rebill.error_message</code>.
     */
    public void setErrorMessage(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>payxpert.rebill.error_message</code>.
     */
    public String getErrorMessage() {
        return (String) get(10);
    }

    /**
     * Setter for <code>payxpert.rebill.invoice_id</code>.
     */
    public void setInvoiceId(Long value) {
        set(11, value);
    }

    /**
     * Getter for <code>payxpert.rebill.invoice_id</code>.
     */
    public Long getInvoiceId() {
        return (Long) get(11);
    }

    /**
     * Setter for <code>payxpert.rebill.loan_id</code>.
     */
    public void setLoanId(Long value) {
        set(12, value);
    }

    /**
     * Getter for <code>payxpert.rebill.loan_id</code>.
     */
    public Long getLoanId() {
        return (Long) get(12);
    }

    /**
     * Setter for <code>payxpert.rebill.payment_created_at</code>.
     */
    public void setPaymentCreatedAt(LocalDateTime value) {
        set(13, value);
    }

    /**
     * Getter for <code>payxpert.rebill.payment_created_at</code>.
     */
    public LocalDateTime getPaymentCreatedAt() {
        return (LocalDateTime) get(13);
    }

    /**
     * Setter for <code>payxpert.rebill.payment_id</code>.
     */
    public void setPaymentId(Long value) {
        set(14, value);
    }

    /**
     * Getter for <code>payxpert.rebill.payment_id</code>.
     */
    public Long getPaymentId() {
        return (Long) get(14);
    }

    /**
     * Setter for <code>payxpert.rebill.response_statement_descriptor</code>.
     */
    public void setResponseStatementDescriptor(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>payxpert.rebill.response_statement_descriptor</code>.
     */
    public String getResponseStatementDescriptor() {
        return (String) get(15);
    }

    /**
     * Setter for <code>payxpert.rebill.response_transaction_id</code>.
     */
    public void setResponseTransactionId(String value) {
        set(16, value);
    }

    /**
     * Getter for <code>payxpert.rebill.response_transaction_id</code>.
     */
    public String getResponseTransactionId() {
        return (String) get(16);
    }

    /**
     * Setter for <code>payxpert.rebill.status</code>.
     */
    public void setStatus(String value) {
        set(17, value);
    }

    /**
     * Getter for <code>payxpert.rebill.status</code>.
     */
    public String getStatus() {
        return (String) get(17);
    }

    /**
     * Setter for <code>payxpert.rebill.credit_card_id</code>.
     */
    public void setCreditCardId(Long value) {
        set(18, value);
    }

    /**
     * Getter for <code>payxpert.rebill.credit_card_id</code>.
     */
    public Long getCreditCardId() {
        return (Long) get(18);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record19 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row19<Long, LocalDateTime, String, Long, LocalDateTime, String, BigDecimal, Long, String, String, String, Long, Long, LocalDateTime, Long, String, String, String, Long> fieldsRow() {
        return (Row19) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row19<Long, LocalDateTime, String, Long, LocalDateTime, String, BigDecimal, Long, String, String, String, Long, Long, LocalDateTime, Long, String, String, String, Long> valuesRow() {
        return (Row19) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return Rebill.REBILL.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return Rebill.REBILL.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Rebill.REBILL.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return Rebill.REBILL.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return Rebill.REBILL.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Rebill.REBILL.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field7() {
        return Rebill.REBILL.AMOUNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field8() {
        return Rebill.REBILL.CLIENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Rebill.REBILL.CURRENCY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return Rebill.REBILL.ERROR_CODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return Rebill.REBILL.ERROR_MESSAGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field12() {
        return Rebill.REBILL.INVOICE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field13() {
        return Rebill.REBILL.LOAN_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field14() {
        return Rebill.REBILL.PAYMENT_CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field15() {
        return Rebill.REBILL.PAYMENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field16() {
        return Rebill.REBILL.RESPONSE_STATEMENT_DESCRIPTOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field17() {
        return Rebill.REBILL.RESPONSE_TRANSACTION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field18() {
        return Rebill.REBILL.STATUS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field19() {
        return Rebill.REBILL.CREDIT_CARD_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value2() {
        return getCreatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getCreatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value4() {
        return getEntityVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value5() {
        return getUpdatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getUpdatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value7() {
        return getAmount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value8() {
        return getClientId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getCurrency();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getErrorCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getErrorMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value12() {
        return getInvoiceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value13() {
        return getLoanId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime value14() {
        return getPaymentCreatedAt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value15() {
        return getPaymentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value16() {
        return getResponseStatementDescriptor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value17() {
        return getResponseTransactionId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value18() {
        return getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value19() {
        return getCreditCardId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value7(BigDecimal value) {
        setAmount(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value8(Long value) {
        setClientId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value9(String value) {
        setCurrency(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value10(String value) {
        setErrorCode(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value11(String value) {
        setErrorMessage(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value12(Long value) {
        setInvoiceId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value13(Long value) {
        setLoanId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value14(LocalDateTime value) {
        setPaymentCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value15(Long value) {
        setPaymentId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value16(String value) {
        setResponseStatementDescriptor(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value17(String value) {
        setResponseTransactionId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value18(String value) {
        setStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord value19(Long value) {
        setCreditCardId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebillRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, BigDecimal value7, Long value8, String value9, String value10, String value11, Long value12, Long value13, LocalDateTime value14, Long value15, String value16, String value17, String value18, Long value19) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        value17(value17);
        value18(value18);
        value19(value19);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RebillRecord
     */
    public RebillRecord() {
        super(Rebill.REBILL);
    }

    /**
     * Create a detached, initialised RebillRecord
     */
    public RebillRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, BigDecimal amount, Long clientId, String currency, String errorCode, String errorMessage, Long invoiceId, Long loanId, LocalDateTime paymentCreatedAt, Long paymentId, String responseStatementDescriptor, String responseTransactionId, String status, Long creditCardId) {
        super(Rebill.REBILL);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, amount);
        set(7, clientId);
        set(8, currency);
        set(9, errorCode);
        set(10, errorMessage);
        set(11, invoiceId);
        set(12, loanId);
        set(13, paymentCreatedAt);
        set(14, paymentId);
        set(15, responseStatementDescriptor);
        set(16, responseTransactionId);
        set(17, status);
        set(18, creditCardId);
    }
}
