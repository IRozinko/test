package fintech.lending.core.loan;

import com.google.common.collect.ImmutableSet;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import fintech.lending.core.db.Entities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

@Data
@Accessors(chain = true)
public class LoanQuery {

    private Long clientId;
    private String loanNumber;
    private Set<LoanStatus> statuses = new HashSet<>();
    private Set<LoanStatusDetail> statusDetails = new HashSet<>();
    private Set<LoanStatusDetail> excludeStatusDetails = new HashSet<>();

    private LocalDate issueDateTo;
    private LocalDate issueDateFrom;
    private LocalDate maturityDateTo;
    private LocalDate maturityDateFrom;
    private List<OrderSpecifier> orders = new ArrayList<>();


    @AllArgsConstructor
    public enum LoanSortField {

        ID(Entities.loan.id),
        ISSUE_DATE(Entities.loan.issueDate),
        NUMBER(Entities.loan.number);

        Expression<? extends Comparable> expression;
    }

    public LoanQuery orderBy(LoanSortField field, Order order) {
        this.orders.add(new OrderSpecifier<>(order, field.expression));
        return this;
    }

    public LoanQuery orDefaultOrder(OrderSpecifier... orders) {
        if (this.orders.isEmpty())
            this.orders.addAll(asList(orders));
        return this;
    }

    public OrderSpecifier[] orders() {
        return orders.toArray(new OrderSpecifier[0]);
    }

    public static LoanQuery paidLoans(Long clientId) {
        return new LoanQuery().setClientId(clientId).setStatuses(ImmutableSet.of(LoanStatus.CLOSED)).setStatusDetails(LoanStatusDetail.paidStates);
    }

    public static LoanQuery closedLoans(Long clientId) {
        return new LoanQuery().setClientId(clientId).setStatuses(ImmutableSet.of(LoanStatus.CLOSED));
    }

    public static LoanQuery openLoans(Long clientId) {
        return new LoanQuery().setClientId(clientId).setStatuses(ImmutableSet.of(LoanStatus.OPEN));
    }

    public static LoanQuery nonVoidedLoans(Long clientId) {
        return new LoanQuery().setClientId(clientId).setExcludeStatusDetails(ImmutableSet.of(LoanStatusDetail.VOIDED));
    }

    public static LoanQuery allLoans(Long clientId) {
        return new LoanQuery().setClientId(clientId);
    }
    public static LoanQuery allLoansByNumber(String loanNumber) {
        return new LoanQuery().setLoanNumber(loanNumber);
    }

    public static LoanQuery openLoans(Long clientId, LoanStatusDetail... statuses) {
        return new LoanQuery().setClientId(clientId).setStatuses(ImmutableSet.of(LoanStatus.OPEN)).setStatusDetails(ImmutableSet.copyOf(statuses));
    }

    public static LoanQuery allOpenLoans(LoanStatusDetail... statuses) {
        return new LoanQuery().setStatuses(ImmutableSet.of(LoanStatus.OPEN)).setStatusDetails(ImmutableSet.copyOf(statuses));
    }
}
