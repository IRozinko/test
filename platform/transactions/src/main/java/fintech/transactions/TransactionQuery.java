package fintech.transactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class TransactionQuery {

    private Long clientId;
    private Long loanId;
    private Long paymentId;
    private Long disbursementId;
    private Long invoiceId;
    private Long scheduleId;
    private Long installmentId;
    private Boolean invoiceIdNotNull;
    private TransactionType transactionType;
    private List<TransactionType> transactionTypes;
    private String transactionSubType;
    private Boolean voided;
    private LocalDate valueDateFrom;
    private LocalDate valueDateTo;
    private LocalDate valueDateIs;

    public static TransactionQuery byClient(Long clientId, TransactionType transactionType) {
        TransactionQuery query = new TransactionQuery();
        query.setClientId(clientId);
        query.setTransactionType(transactionType);
        return query;
    }

    public static TransactionQuery byClient(Long clientId, TransactionType... transactionTypes) {
        TransactionQuery query = new TransactionQuery();
        query.setClientId(clientId);
        query.setTransactionTypes(newArrayList(transactionTypes));
        return query;
    }

    public static TransactionQuery notVoidedByClient(Long clientId, TransactionType... transactionTypes) {
        TransactionQuery query = new TransactionQuery();
        query.setClientId(clientId);
        query.setTransactionTypes(newArrayList(transactionTypes));
        query.setVoided(false);
        return query;
    }

    public static TransactionQuery byLoan(Long loanId, TransactionType transactionType) {
        TransactionQuery query = new TransactionQuery();
        query.setLoanId(loanId);
        query.setTransactionType(transactionType);
        return query;
    }

    public static TransactionQuery notVoidedByLoan(Long loanId, TransactionType transactionType) {
        TransactionQuery query = new TransactionQuery();
        query.setLoanId(loanId);
        query.setTransactionType(transactionType);
        query.setVoided(false);
        return query;
    }

    public static TransactionQuery notVoidedByLoan(Long loanId, TransactionType ... transactionTypes) {
        TransactionQuery query = new TransactionQuery();
        query.setLoanId(loanId);
        query.setTransactionTypes(newArrayList(transactionTypes));
        query.setVoided(false);
        return query;
    }

    public static TransactionQuery notVoidedByLoan(Long loanId, TransactionType transactionType, LocalDate valueDateFrom) {
        TransactionQuery query = new TransactionQuery();
        query.setLoanId(loanId);
        query.setTransactionType(transactionType);
        query.setVoided(false);
        query.setValueDateFrom(valueDateFrom);
        return query;
    }

    public static TransactionQuery byLoan(Long loanId, LocalDate valueDateTo) {
        TransactionQuery query = new TransactionQuery();
        query.setLoanId(loanId);
        query.setValueDateTo(valueDateTo);
        return query;
    }

    public static TransactionQuery byLoanInvoiced(Long loanId, LocalDate valueDateTo) {
        TransactionQuery query = new TransactionQuery();
        query.setLoanId(loanId);
        query.setValueDateTo(valueDateTo);
        query.setInvoiceIdNotNull(true);
        return query;
    }

    public static TransactionQuery byPaymentId(Long paymentId, TransactionType transactionType) {
        TransactionQuery query = new TransactionQuery();
        query.setPaymentId(paymentId);
        query.setTransactionType(transactionType);
        return query;
    }

    public static TransactionQuery byInvoice(Long invoiceId) {
        TransactionQuery query = new TransactionQuery();
        query.setInvoiceId(invoiceId);
        return query;
    }

    public static TransactionQuery byInvoice(Long invoiceId, LocalDate valueDateTo) {
        TransactionQuery query = new TransactionQuery();
        query.setInvoiceId(invoiceId);
        query.setValueDateTo(valueDateTo);
        return query;
    }

    public static TransactionQuery all() {
        return new TransactionQuery();
    }

    public static TransactionQuery byClient(Long clientId) {
        TransactionQuery query = new TransactionQuery();
        query.setClientId(clientId);
        return query;
    }

    public static TransactionQuery byLoan(Long loanId) {
        TransactionQuery query = new TransactionQuery();
        query.setLoanId(loanId);
        return query;
    }

    public static TransactionQuery byPayment(Long paymentId) {
        TransactionQuery query = new TransactionQuery();
        query.setPaymentId(paymentId);
        return query;
    }

    public static TransactionQuery byDisbursement(Long disbursementId) {
        TransactionQuery query = new TransactionQuery();
        query.setDisbursementId(disbursementId);
        return query;
    }

    public static TransactionQuery byDisbursement(Long disbursementId, TransactionType type, boolean voided) {
        TransactionQuery query = new TransactionQuery();
        query.setDisbursementId(disbursementId);
        query.setTransactionType(type);
        query.setVoided(voided);
        return query;
    }

    public static TransactionQuery byInstallment(Long installmentId) {
        TransactionQuery query = new TransactionQuery();
        query.setInstallmentId(installmentId);
        return query;
    }

    public static TransactionQuery notVoidedByInstallment(Long installmentId, TransactionType type, LocalDate valueDateIs) {
        TransactionQuery query = new TransactionQuery();
        query.setInstallmentId(installmentId);
        query.setTransactionType(type);
        query.setValueDateIs(valueDateIs);
        query.setVoided(false);
        return query;
    }

    public static TransactionQuery byInstallment(Long installmentId, LocalDate valueDateTo) {
        TransactionQuery query = new TransactionQuery();
        query.setInstallmentId(installmentId);
        query.setValueDateTo(valueDateTo);
        return query;
    }
}
