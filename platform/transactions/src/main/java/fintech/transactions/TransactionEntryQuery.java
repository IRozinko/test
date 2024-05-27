package fintech.transactions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionEntryQuery {

    private TransactionType transactionType;

    private TransactionEntryType type;

    private List<String> subType;

    private Long loanId;

    private Long invoiceId;

    private Long scheduleId;

    private Long installmentId;

    private LocalDate valueDate;

    private LocalDate valueDateFrom;

    private LocalDate valueDateTo;

    private Boolean voided;

    public static TransactionEntryQuery byLoan(Long loanId, TransactionEntryType type) {
        TransactionEntryQuery query = new TransactionEntryQuery();
        query.setLoanId(loanId);
        query.setType(type);
        return query;
    }

    public static TransactionEntryQuery byLoan(Long loanId, TransactionEntryType type, String... subType) {
        TransactionEntryQuery query = new TransactionEntryQuery();
        query.setLoanId(loanId);
        query.setType(type);
        query.setSubType(Arrays.asList(subType));
        return query;
    }

    public static TransactionEntryQuery byInstallment(Long installmentId, TransactionEntryType type) {
        TransactionEntryQuery query = new TransactionEntryQuery();
        query.setInstallmentId(installmentId);
        query.setType(type);
        return query;
    }

    public static TransactionEntryQuery byInvoice(Long invoiceId, TransactionEntryType type) {
        TransactionEntryQuery query = new TransactionEntryQuery();
        query.setInvoiceId(invoiceId);
        query.setType(type);
        return query;
    }
}
