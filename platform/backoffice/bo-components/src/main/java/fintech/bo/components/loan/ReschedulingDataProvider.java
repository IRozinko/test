package fintech.bo.components.loan;

import com.google.common.collect.ImmutableMap;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.client.SearchableJooqClientDataProvider;
import fintech.bo.components.common.SearchFieldValue;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static fintech.bo.components.common.SearchFieldOptions.ALL;
import static fintech.bo.components.common.SearchFieldOptions.LOAN_NUMBER;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.db.jooq.alfa.tables.LoanRescheduling.LOAN_RESCHEDULING;

@Slf4j
@Setter
public class ReschedulingDataProvider extends SearchableJooqClientDataProvider<Record> {

    private static final Map<String, Function<String, Condition>> searchFields =
        ImmutableMap.<String, Function<String, Condition>>builder()
            .put(LOAN_NUMBER, val -> LOAN.LOAN_NUMBER.likeIgnoreCase(val + "%"))
            .build();

    private String status;

    public ReschedulingDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
        super(db, searchFields, jooqClientDataService);
    }


    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = createSelect();
        query.getFilter().ifPresent(filter -> applySearch(select, new SearchFieldValue(ALL, filter)));
        applySearch(select);
        setFilters();
        applyFilter(select);
        return select;
    }

    protected SelectWhereStep<Record> createSelect() {
        return db.select(fields(
            LOAN_RESCHEDULING.fields(),
            LOAN_RESCHEDULING.CREATED_AT,
            LOAN.LOAN_NUMBER,
            LOAN.ISSUE_DATE,
            LOAN.PAYMENT_DUE_DATE,
            LOAN.CLOSE_DATE,
            LOAN.OVERDUE_DAYS,
            LOAN.TOTAL_DUE,
            LOAN.TOTAL_OUTSTANDING))
            .from(LOAN_RESCHEDULING)
            .join(LOAN).on(LOAN.ID.eq(LOAN_RESCHEDULING.LOAN_ID))
            .join(CLIENT).on(LOAN.CLIENT_ID.eq(CLIENT.ID));
    }

    protected void setFilters() {
        resetFilterConditions();
        addFilterCondition(status, LOAN_RESCHEDULING.STATUS::eq);
    }

    @Override
    protected Object id(Record item) {
        return item.get(LOAN_RESCHEDULING.ID);
    }

    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        return QuerySortOrder.desc(LOAN_RESCHEDULING.STATUS.getName()).build();
    }
}
