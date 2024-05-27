package fintech.lending.core.invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceQuery {

    private Long clientId;

    private Long loanId;

    private InvoiceStatus status;

    private LocalDate invoiceDateFrom;

    private LocalDate invoiceDateTo;

    private LocalDate periodToTo;

    private LocalDate dueDateFrom;

    private LocalDate dueDateTo;

    private LocalDate closedFrom;

    private LocalDate closedTo;

    private Boolean voided = false;

    private List<InvoiceStatusDetail> statusDetailArrayList = newArrayList();

    private String number;

    private Boolean manual;

    public static InvoiceQuery byClient(Long clientId) {
        InvoiceQuery query = new InvoiceQuery();
        query.setClientId(clientId);
        return query;
    }

    public static InvoiceQuery byClientAuto(Long clientId, InvoiceStatusDetail... statuses) {
        InvoiceQuery query = new InvoiceQuery();
        query.setClientId(clientId);
        query.setStatusDetailArrayList(newArrayList(statuses));
        query.setManual(false);
        return query;
    }

    public static InvoiceQuery byLoan(Long loanId) {
        InvoiceQuery query = new InvoiceQuery();
        query.setLoanId(loanId);
        return query;
    }

    public static InvoiceQuery byLoanOpen(Long loanId) {
        InvoiceQuery query = new InvoiceQuery();
        query.setLoanId(loanId);
        query.setStatus(InvoiceStatus.OPEN);
        return query;
    }

    public static InvoiceQuery byLoan(Long loanId, InvoiceStatusDetail... statuses) {
        InvoiceQuery query = new InvoiceQuery();
        query.setLoanId(loanId);
        query.setStatusDetailArrayList(newArrayList(statuses));
        return query;
    }

    public static InvoiceQuery byNumber(String number) {
        InvoiceQuery query = new InvoiceQuery();
        query.setNumber(number);
        return query;
    }
}
