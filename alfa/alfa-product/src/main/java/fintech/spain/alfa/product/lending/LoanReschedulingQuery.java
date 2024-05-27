package fintech.spain.alfa.product.lending;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class LoanReschedulingQuery {

    private Long loanId;
    private LoanReschedulingStatus status;
    private List<LoanReschedulingStatus> statuses;

    public static LoanReschedulingQuery rescheduled(Long loanId) {
        LoanReschedulingQuery query = new LoanReschedulingQuery();
        query.setLoanId(loanId);
        query.setStatuses(newArrayList(LoanReschedulingStatus.RESCHEDULED, LoanReschedulingStatus.PENDING_TO_BREAK));
        return query;
    }

    public static LoanReschedulingQuery cancelled(Long loanId) {
        LoanReschedulingQuery query = new LoanReschedulingQuery();
        query.setLoanId(loanId);
        query.setStatus(LoanReschedulingStatus.CANCELLED);
        return query;
    }

    public static LoanReschedulingQuery paid(Long loanId) {
        LoanReschedulingQuery query = new LoanReschedulingQuery();
        query.setLoanId(loanId);
        query.setStatus(LoanReschedulingStatus.RESCHEDULED_PAID);
        return query;
    }

}
