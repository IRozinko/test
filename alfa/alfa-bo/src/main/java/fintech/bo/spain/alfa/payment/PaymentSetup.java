package fintech.bo.spain.alfa.payment;

import fintech.bo.api.client.LoanApiClient;
import fintech.bo.api.client.PaymentApiClient;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.ClientQueries;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.payments.PaymentComponents;
import fintech.bo.components.payments.PaymentConstants;
import fintech.bo.components.payments.disbursement.DisbursementComponents;
import fintech.bo.components.payments.handlers.*;
import fintech.bo.components.transaction.TransactionConstants;
import fintech.bo.db.jooq.payment.tables.records.PaymentRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentSetup {

    @Autowired
    private PaymentComponents paymentComponents;

    @Autowired
    private PaymentApiClient paymentApiClient;

    @Autowired
    private LoanComponents loanComponents;

    @Autowired
    private ClientQueries clientQueries;

    @Autowired
    private LoanApiClient loanApiClient;

    @Autowired
    private ClientComponents clientComponents;

    @Autowired
    private DisbursementComponents disbursementComponents;

    private boolean initialized;

    public synchronized void init() {
        if (!initialized) {
            paymentComponents.registerTransactionHandler("Loan Repayment", PaymentSetup::isIncoming, () -> new RepaymentTransactionHandler(clientComponents, loanComponents, clientQueries, paymentApiClient, loanApiClient));
            paymentComponents.registerTransactionHandler("Loan Extension", PaymentSetup::isIncoming, () -> new ExtensionTransactionHandler(clientComponents, loanComponents, clientQueries, paymentApiClient, loanApiClient));
            paymentComponents.registerTransactionHandler("Overpayment", PaymentSetup::isIncoming, () -> new OverpaymentTransactionHandler(clientComponents, loanComponents, request -> paymentApiClient.addOverpaymentTransaction(request)));
            paymentComponents.registerTransactionHandler("Unidentified liabilities to customers", PaymentSetup::isIncoming, () -> new PaymentTransactionHandler(paymentApiClient, clientComponents, OtherTransactionConfig.builder().showClientSelection(true).transactionSubType(TransactionConstants.TRANSACTION_SUB_TYPE_UNIDENTIFIED_LIABILITIES_TO_CUSTOMERS).build()));
            paymentComponents.registerTransactionHandler("Settle Disbursement", PaymentSetup::isOutgoing, () -> new DisbursementSettledTransactionHandler(paymentApiClient, disbursementComponents));
            paymentComponents.registerTransactionHandler("Faulty Out", PaymentSetup::isOutgoing, () -> new PaymentTransactionHandler(paymentApiClient, clientComponents, OtherTransactionConfig.builder().showClientSelection(true).transactionSubType(TransactionConstants.TRANSACTION_SUB_TYPE_FAULTY_OUT).build()));
            paymentComponents.registerTransactionHandler("Bank Commission", PaymentSetup::isOutgoing, () -> new PaymentTransactionHandler(paymentApiClient, clientComponents, OtherTransactionConfig.builder().showClientSelection(false).transactionSubType(TransactionConstants.TRANSACTION_SUB_TYPE_BANK_COMMISSION).build()));
            paymentComponents.registerTransactionHandler("Refund Overpayment", PaymentSetup::isOutgoing, () -> new OverpaymentTransactionHandler(clientComponents, loanComponents, request -> paymentApiClient.addRefundOverpaymentTransaction(request)));
            paymentComponents.registerTransactionHandler("Inter Company Transfer", p -> true, () -> new PaymentTransactionHandler(paymentApiClient, clientComponents, OtherTransactionConfig.builder().showClientSelection(false).transactionSubType(TransactionConstants.TRANSACTION_SUB_TYPE_INTER_COMPANY_TRANSFER).build()));
            paymentComponents.registerTransactionHandler("Principal Viventor", p -> true, () -> new PaymentTransactionHandler(paymentApiClient, clientComponents, OtherTransactionConfig.builder().showClientSelection(false).transactionSubType(TransactionConstants.TRANSACTION_SUB_TYPE_PRINCIPAL_VIVENTOR).build()));
            paymentComponents.registerTransactionHandler("Other", p -> true, () -> new PaymentTransactionHandler(paymentApiClient, clientComponents, OtherTransactionConfig.builder().showClientSelection(false).transactionSubType(TransactionConstants.TRANSACTION_SUB_TYPE_OTHER).build()));
            paymentComponents.registerTransactionHandler("Unidentified liabilities to customers", PaymentSetup::isIncoming, () -> new PaymentTransactionHandler(paymentApiClient, clientComponents, OtherTransactionConfig.builder().showClientSelection(true).transactionSubType(TransactionConstants.TRANSACTION_SUB_TYPE_UNIDENTIFIED_LIABILITIES_TO_CUSTOMERS).build()));
            initialized = true;
        }
    }

    public static boolean isIncoming(PaymentRecord payment) {
        return PaymentConstants.TYPE_INCOMING.equals(payment.getPaymentType());
    }

    public static boolean isOutgoing(PaymentRecord payment) {
        return PaymentConstants.TYPE_OUTGOING.equals(payment.getPaymentType());
    }
}
