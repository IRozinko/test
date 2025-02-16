/*
 * This file is generated by jOOQ.
*/
package fintech.bo.db.jooq.payment;


import fintech.bo.db.jooq.payment.tables.Disbursement;
import fintech.bo.db.jooq.payment.tables.Institution;
import fintech.bo.db.jooq.payment.tables.InstitutionAccount;
import fintech.bo.db.jooq.payment.tables.Payment;
import fintech.bo.db.jooq.payment.tables.Statement;
import fintech.bo.db.jooq.payment.tables.StatementRow;
import fintech.bo.db.jooq.payment.tables.StatementRowAttributes;
import fintech.bo.db.jooq.payment.tables.records.DisbursementRecord;
import fintech.bo.db.jooq.payment.tables.records.InstitutionAccountRecord;
import fintech.bo.db.jooq.payment.tables.records.InstitutionRecord;
import fintech.bo.db.jooq.payment.tables.records.PaymentRecord;
import fintech.bo.db.jooq.payment.tables.records.StatementRecord;
import fintech.bo.db.jooq.payment.tables.records.StatementRowAttributesRecord;
import fintech.bo.db.jooq.payment.tables.records.StatementRowRecord;

import javax.annotation.Generated;

import org.jooq.ForeignKey;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;


/**
 * A class modelling foreign key relationships between tables of the <code>payment</code> 
 * schema
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<DisbursementRecord> DISBURSEMENT_PKEY = UniqueKeys0.DISBURSEMENT_PKEY;
    public static final UniqueKey<DisbursementRecord> PAYMENT_DISBURSEMENT_REFERENCE_UNIQUE = UniqueKeys0.PAYMENT_DISBURSEMENT_REFERENCE_UNIQUE;
    public static final UniqueKey<InstitutionRecord> INSTITUTION_PKEY = UniqueKeys0.INSTITUTION_PKEY;
    public static final UniqueKey<InstitutionRecord> UK_QHW15H5F7NC4G3NDVA8SORY1U = UniqueKeys0.UK_QHW15H5F7NC4G3NDVA8SORY1U;
    public static final UniqueKey<InstitutionAccountRecord> INSTITUTION_ACCOUNT_PKEY = UniqueKeys0.INSTITUTION_ACCOUNT_PKEY;
    public static final UniqueKey<PaymentRecord> PAYMENT_PKEY = UniqueKeys0.PAYMENT_PKEY;
    public static final UniqueKey<PaymentRecord> IDX_PAYMENT_KEY = UniqueKeys0.IDX_PAYMENT_KEY;
    public static final UniqueKey<StatementRecord> STATEMENT_PKEY = UniqueKeys0.STATEMENT_PKEY;
    public static final UniqueKey<StatementRowRecord> STATEMENT_ROW_PKEY = UniqueKeys0.STATEMENT_ROW_PKEY;
    public static final UniqueKey<StatementRowAttributesRecord> STATEMENT_ROW_ATTRIBUTES_PKEY = UniqueKeys0.STATEMENT_ROW_ATTRIBUTES_PKEY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<DisbursementRecord, InstitutionAccountRecord> DISBURSEMENT__FK_DISBURSEMENT_INSTITUTION_ACCOUNT_ID = ForeignKeys0.DISBURSEMENT__FK_DISBURSEMENT_INSTITUTION_ACCOUNT_ID;
    public static final ForeignKey<DisbursementRecord, InstitutionRecord> DISBURSEMENT__FK_DISBURSEMENT_INSTITUTION_ID = ForeignKeys0.DISBURSEMENT__FK_DISBURSEMENT_INSTITUTION_ID;
    public static final ForeignKey<InstitutionAccountRecord, InstitutionRecord> INSTITUTION_ACCOUNT__FK_INSTITUTION_ACCOUNT_INSTITUTION_ID = ForeignKeys0.INSTITUTION_ACCOUNT__FK_INSTITUTION_ACCOUNT_INSTITUTION_ID;
    public static final ForeignKey<PaymentRecord, InstitutionAccountRecord> PAYMENT__FK_PAYMENT_ACCOUNT_ID = ForeignKeys0.PAYMENT__FK_PAYMENT_ACCOUNT_ID;
    public static final ForeignKey<StatementRecord, InstitutionRecord> STATEMENT__FK_STATEMENT_INSTITUTION_ID = ForeignKeys0.STATEMENT__FK_STATEMENT_INSTITUTION_ID;
    public static final ForeignKey<StatementRowRecord, PaymentRecord> STATEMENT_ROW__FK_STATEMENT_ROW_PAYMENT_ID = ForeignKeys0.STATEMENT_ROW__FK_STATEMENT_ROW_PAYMENT_ID;
    public static final ForeignKey<StatementRowRecord, StatementRecord> STATEMENT_ROW__FK_STATEMENT_ROW_STATEMENT_ID = ForeignKeys0.STATEMENT_ROW__FK_STATEMENT_ROW_STATEMENT_ID;
    public static final ForeignKey<StatementRowAttributesRecord, StatementRowRecord> STATEMENT_ROW_ATTRIBUTES__FK_STATEMENT_ROW_ATTRIBUTES_STATEMENT_ROW_ID = ForeignKeys0.STATEMENT_ROW_ATTRIBUTES__FK_STATEMENT_ROW_ATTRIBUTES_STATEMENT_ROW_ID;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class UniqueKeys0 extends AbstractKeys {
        public static final UniqueKey<DisbursementRecord> DISBURSEMENT_PKEY = createUniqueKey(Disbursement.DISBURSEMENT, "disbursement_pkey", Disbursement.DISBURSEMENT.ID);
        public static final UniqueKey<DisbursementRecord> PAYMENT_DISBURSEMENT_REFERENCE_UNIQUE = createUniqueKey(Disbursement.DISBURSEMENT, "payment_disbursement_reference_unique", Disbursement.DISBURSEMENT.REFERENCE);
        public static final UniqueKey<InstitutionRecord> INSTITUTION_PKEY = createUniqueKey(Institution.INSTITUTION, "institution_pkey", Institution.INSTITUTION.ID);
        public static final UniqueKey<InstitutionRecord> UK_QHW15H5F7NC4G3NDVA8SORY1U = createUniqueKey(Institution.INSTITUTION, "uk_qhw15h5f7nc4g3ndva8sory1u", Institution.INSTITUTION.NAME);
        public static final UniqueKey<InstitutionAccountRecord> INSTITUTION_ACCOUNT_PKEY = createUniqueKey(InstitutionAccount.INSTITUTION_ACCOUNT, "institution_account_pkey", InstitutionAccount.INSTITUTION_ACCOUNT.ID);
        public static final UniqueKey<PaymentRecord> PAYMENT_PKEY = createUniqueKey(Payment.PAYMENT_, "payment_pkey", Payment.PAYMENT_.ID);
        public static final UniqueKey<PaymentRecord> IDX_PAYMENT_KEY = createUniqueKey(Payment.PAYMENT_, "idx_payment_key", Payment.PAYMENT_.KEY);
        public static final UniqueKey<StatementRecord> STATEMENT_PKEY = createUniqueKey(Statement.STATEMENT, "statement_pkey", Statement.STATEMENT.ID);
        public static final UniqueKey<StatementRowRecord> STATEMENT_ROW_PKEY = createUniqueKey(StatementRow.STATEMENT_ROW, "statement_row_pkey", StatementRow.STATEMENT_ROW.ID);
        public static final UniqueKey<StatementRowAttributesRecord> STATEMENT_ROW_ATTRIBUTES_PKEY = createUniqueKey(StatementRowAttributes.STATEMENT_ROW_ATTRIBUTES, "statement_row_attributes_pkey", StatementRowAttributes.STATEMENT_ROW_ATTRIBUTES.STATEMENT_ROW_ID, StatementRowAttributes.STATEMENT_ROW_ATTRIBUTES.KEY);
    }

    private static class ForeignKeys0 extends AbstractKeys {
        public static final ForeignKey<DisbursementRecord, InstitutionAccountRecord> DISBURSEMENT__FK_DISBURSEMENT_INSTITUTION_ACCOUNT_ID = createForeignKey(fintech.bo.db.jooq.payment.Keys.INSTITUTION_ACCOUNT_PKEY, Disbursement.DISBURSEMENT, "disbursement__fk_disbursement_institution_account_id", Disbursement.DISBURSEMENT.INSTITUTION_ACCOUNT_ID);
        public static final ForeignKey<DisbursementRecord, InstitutionRecord> DISBURSEMENT__FK_DISBURSEMENT_INSTITUTION_ID = createForeignKey(fintech.bo.db.jooq.payment.Keys.INSTITUTION_PKEY, Disbursement.DISBURSEMENT, "disbursement__fk_disbursement_institution_id", Disbursement.DISBURSEMENT.INSTITUTION_ID);
        public static final ForeignKey<InstitutionAccountRecord, InstitutionRecord> INSTITUTION_ACCOUNT__FK_INSTITUTION_ACCOUNT_INSTITUTION_ID = createForeignKey(fintech.bo.db.jooq.payment.Keys.INSTITUTION_PKEY, InstitutionAccount.INSTITUTION_ACCOUNT, "institution_account__fk_institution_account_institution_id", InstitutionAccount.INSTITUTION_ACCOUNT.INSTITUTION_ID);
        public static final ForeignKey<PaymentRecord, InstitutionAccountRecord> PAYMENT__FK_PAYMENT_ACCOUNT_ID = createForeignKey(fintech.bo.db.jooq.payment.Keys.INSTITUTION_ACCOUNT_PKEY, Payment.PAYMENT_, "payment__fk_payment_account_id", Payment.PAYMENT_.ACCOUNT_ID);
        public static final ForeignKey<StatementRecord, InstitutionRecord> STATEMENT__FK_STATEMENT_INSTITUTION_ID = createForeignKey(fintech.bo.db.jooq.payment.Keys.INSTITUTION_PKEY, Statement.STATEMENT, "statement__fk_statement_institution_id", Statement.STATEMENT.INSTITUTION_ID);
        public static final ForeignKey<StatementRowRecord, PaymentRecord> STATEMENT_ROW__FK_STATEMENT_ROW_PAYMENT_ID = createForeignKey(fintech.bo.db.jooq.payment.Keys.PAYMENT_PKEY, StatementRow.STATEMENT_ROW, "statement_row__fk_statement_row_payment_id", StatementRow.STATEMENT_ROW.PAYMENT_ID);
        public static final ForeignKey<StatementRowRecord, StatementRecord> STATEMENT_ROW__FK_STATEMENT_ROW_STATEMENT_ID = createForeignKey(fintech.bo.db.jooq.payment.Keys.STATEMENT_PKEY, StatementRow.STATEMENT_ROW, "statement_row__fk_statement_row_statement_id", StatementRow.STATEMENT_ROW.STATEMENT_ID);
        public static final ForeignKey<StatementRowAttributesRecord, StatementRowRecord> STATEMENT_ROW_ATTRIBUTES__FK_STATEMENT_ROW_ATTRIBUTES_STATEMENT_ROW_ID = createForeignKey(fintech.bo.db.jooq.payment.Keys.STATEMENT_ROW_PKEY, StatementRowAttributes.STATEMENT_ROW_ATTRIBUTES, "statement_row_attributes__fk_statement_row_attributes_statement_row_id", StatementRowAttributes.STATEMENT_ROW_ATTRIBUTES.STATEMENT_ROW_ID);
    }
}
