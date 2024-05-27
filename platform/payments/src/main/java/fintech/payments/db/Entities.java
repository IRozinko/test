package fintech.payments.db;

public class Entities {

    public static final String SCHEMA = "payment";

    public static final QInstitutionEntity institution = QInstitutionEntity.institutionEntity;
    public static final QInstitutionAccountEntity institutionAccount = QInstitutionAccountEntity.institutionAccountEntity;
    public static final QPaymentEntity payment = QPaymentEntity.paymentEntity;
    public static final QStatementEntity statement = QStatementEntity.statementEntity;
    public static final QStatementRowEntity statementRow = QStatementRowEntity.statementRowEntity;
    public static final QDisbursementEntity disbursement = QDisbursementEntity.disbursementEntity;    

}
