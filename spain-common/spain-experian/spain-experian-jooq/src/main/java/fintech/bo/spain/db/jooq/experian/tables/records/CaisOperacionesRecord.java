/*
 * This file is generated by jOOQ.
*/
package fintech.bo.spain.db.jooq.experian.tables.records;


import fintech.bo.spain.db.jooq.experian.tables.CaisOperaciones;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record14;
import org.jooq.Row14;
import org.jooq.impl.UpdatableRecordImpl;

import javax.annotation.Generated;
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
public class CaisOperacionesRecord extends UpdatableRecordImpl<CaisOperacionesRecord> implements Record14<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, Long, String, String, Integer, String, String, String> {

    private static final long serialVersionUID = 593656345;

    /**
     * Setter for <code>spain_experian.cais_operaciones.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>spain_experian.cais_operaciones.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>spain_experian.cais_operaciones.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>spain_experian.cais_operaciones.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>spain_experian.cais_operaciones.created_by</code>.
     */
    public void setCreatedBy(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>spain_experian.cais_operaciones.created_by</code>.
     */
    public String getCreatedBy() {
        return (String) get(2);
    }

    /**
     * Setter for <code>spain_experian.cais_operaciones.entity_version</code>.
     */
    public void setEntityVersion(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>spain_experian.cais_operaciones.entity_version</code>.
     */
    public Long getEntityVersion() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>spain_experian.cais_operaciones.updated_at</code>.
     */
    public void setUpdatedAt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>spain_experian.cais_operaciones.updated_at</code>.
     */
    public LocalDateTime getUpdatedAt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>spain_experian.cais_operaciones.updated_by</code>.
     */
    public void setUpdatedBy(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>spain_experian.cais_operaciones.updated_by</code>.
     */
    public String getUpdatedBy() {
        return (String) get(5);
    }

    /**
     * Setter for <code>spain_experian.cais_operaciones.application_id</code>.
     */
    public void setApplicationId(Long value) {
        set(6, value);
    }

    /**
     * Getter for <code>spain_experian.cais_operaciones.application_id</code>.
     */
    public Long getApplicationId() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>spain_experian.cais_operaciones.client_id</code>.
     */
    public void setClientId(Long value) {
        set(7, value);
    }

    /**
     * Getter for <code>spain_experian.cais_operaciones.client_id</code>.
     */
    public Long getClientId() {
        return (Long) get(7);
    }

    /**
     * Setter for <code>spain_experian.cais_operaciones.document_number</code>.
     */
    public void setDocumentNumber(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>spain_experian.cais_operaciones.document_number</code>.
     */
    public String getDocumentNumber() {
        return (String) get(8);
    }

    /**
     * Setter for <code>spain_experian.cais_operaciones.error</code>.
     */
    public void setError(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>spain_experian.cais_operaciones.error</code>.
     */
    public String getError() {
        return (String) get(9);
    }

    /**
     * Setter for <code>spain_experian.cais_operaciones.numero_registros_devueltos</code>.
     */
    public void setNumeroRegistrosDevueltos(Integer value) {
        set(10, value);
    }

    /**
     * Getter for <code>spain_experian.cais_operaciones.numero_registros_devueltos</code>.
     */
    public Integer getNumeroRegistrosDevueltos() {
        return (Integer) get(10);
    }

    /**
     * Setter for <code>spain_experian.cais_operaciones.request_body</code>.
     */
    public void setRequestBody(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>spain_experian.cais_operaciones.request_body</code>.
     */
    public String getRequestBody() {
        return (String) get(11);
    }

    /**
     * Setter for <code>spain_experian.cais_operaciones.response_body</code>.
     */
    public void setResponseBody(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>spain_experian.cais_operaciones.response_body</code>.
     */
    public String getResponseBody() {
        return (String) get(12);
    }

    /**
     * Setter for <code>spain_experian.cais_operaciones.status</code>.
     */
    public void setStatus(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>spain_experian.cais_operaciones.status</code>.
     */
    public String getStatus() {
        return (String) get(13);
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
    // Record14 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row14<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, Long, String, String, Integer, String, String, String> fieldsRow() {
        return (Row14) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row14<Long, LocalDateTime, String, Long, LocalDateTime, String, Long, Long, String, String, Integer, String, String, String> valuesRow() {
        return (Row14) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return CaisOperaciones.CAIS_OPERACIONES.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field2() {
        return CaisOperaciones.CAIS_OPERACIONES.CREATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return CaisOperaciones.CAIS_OPERACIONES.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return CaisOperaciones.CAIS_OPERACIONES.ENTITY_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<LocalDateTime> field5() {
        return CaisOperaciones.CAIS_OPERACIONES.UPDATED_AT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return CaisOperaciones.CAIS_OPERACIONES.UPDATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field7() {
        return CaisOperaciones.CAIS_OPERACIONES.APPLICATION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field8() {
        return CaisOperaciones.CAIS_OPERACIONES.CLIENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return CaisOperaciones.CAIS_OPERACIONES.DOCUMENT_NUMBER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return CaisOperaciones.CAIS_OPERACIONES.ERROR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field11() {
        return CaisOperaciones.CAIS_OPERACIONES.NUMERO_REGISTROS_DEVUELTOS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field12() {
        return CaisOperaciones.CAIS_OPERACIONES.REQUEST_BODY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field13() {
        return CaisOperaciones.CAIS_OPERACIONES.RESPONSE_BODY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field14() {
        return CaisOperaciones.CAIS_OPERACIONES.STATUS;
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
    public Long value7() {
        return getApplicationId();
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
        return getDocumentNumber();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getError();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value11() {
        return getNumeroRegistrosDevueltos();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value12() {
        return getRequestBody();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value13() {
        return getResponseBody();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value14() {
        return getStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaisOperacionesRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaisOperacionesRecord value2(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaisOperacionesRecord value3(String value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaisOperacionesRecord value4(Long value) {
        setEntityVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaisOperacionesRecord value5(LocalDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaisOperacionesRecord value6(String value) {
        setUpdatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaisOperacionesRecord value7(Long value) {
        setApplicationId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaisOperacionesRecord value8(Long value) {
        setClientId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaisOperacionesRecord value9(String value) {
        setDocumentNumber(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaisOperacionesRecord value10(String value) {
        setError(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaisOperacionesRecord value11(Integer value) {
        setNumeroRegistrosDevueltos(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaisOperacionesRecord value12(String value) {
        setRequestBody(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaisOperacionesRecord value13(String value) {
        setResponseBody(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaisOperacionesRecord value14(String value) {
        setStatus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaisOperacionesRecord values(Long value1, LocalDateTime value2, String value3, Long value4, LocalDateTime value5, String value6, Long value7, Long value8, String value9, String value10, Integer value11, String value12, String value13, String value14) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CaisOperacionesRecord
     */
    public CaisOperacionesRecord() {
        super(CaisOperaciones.CAIS_OPERACIONES);
    }

    /**
     * Create a detached, initialised CaisOperacionesRecord
     */
    public CaisOperacionesRecord(Long id, LocalDateTime createdAt, String createdBy, Long entityVersion, LocalDateTime updatedAt, String updatedBy, Long applicationId, Long clientId, String documentNumber, String error, Integer numeroRegistrosDevueltos, String requestBody, String responseBody, String status) {
        super(CaisOperaciones.CAIS_OPERACIONES);

        set(0, id);
        set(1, createdAt);
        set(2, createdBy);
        set(3, entityVersion);
        set(4, updatedAt);
        set(5, updatedBy);
        set(6, applicationId);
        set(7, clientId);
        set(8, documentNumber);
        set(9, error);
        set(10, numeroRegistrosDevueltos);
        set(11, requestBody);
        set(12, responseBody);
        set(13, status);
    }
}
