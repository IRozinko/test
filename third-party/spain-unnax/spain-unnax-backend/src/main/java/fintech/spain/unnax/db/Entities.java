package fintech.spain.unnax.db;

public abstract class Entities {

    public static final String SCHEMA = "spain_unnax";

    public static final QCallbackEntity callBack = QCallbackEntity.callbackEntity;
    public static final QPaymentWithCardEntity paymentWithCard = QPaymentWithCardEntity.paymentWithCardEntity;
    public static final QPaymentWithTransferAuthorizedEntity paymentWithTransferAuthorizedEntity = QPaymentWithTransferAuthorizedEntity.paymentWithTransferAuthorizedEntity;
    public static final QPaymentWithTransferCompletedEntity paymentWithTransferCompletedEntity = QPaymentWithTransferCompletedEntity.paymentWithTransferCompletedEntity;
    public static final QDisbursementQueueEntity disbursementQueue = QDisbursementQueueEntity.disbursementQueueEntity;
    public static final QTransferAutoEntity transferOut = QTransferAutoEntity.transferAutoEntity;
    public static final QWebHookEntity webHook = QWebHookEntity.webHookEntity;
    public static final QCreditCardEntity creditCard = QCreditCardEntity.creditCardEntity;
    public static final QCardChargeRequestEntity cardChargeRequest = QCardChargeRequestEntity.cardChargeRequestEntity;
    public static final QBankStatementsRequestEntity bankStatementsRequestEntity = QBankStatementsRequestEntity.bankStatementsRequestEntity;


}
