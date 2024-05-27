package fintech.bo.components.loan;

import com.google.common.collect.ImmutableMap;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.client.SearchableJooqClientDataProvider;
import fintech.bo.components.common.SearchFieldValue;
import fintech.bo.db.jooq.lending.tables.Discount;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.components.client.ClientDataProviderUtils.deletedClientCondition;
import static fintech.bo.components.common.SearchFieldOptions.ALL;
import static fintech.bo.components.common.SearchFieldOptions.LOAN_NUMBER;
import static fintech.bo.components.common.SearchFieldOptions.SECOND_LAST_NAME;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.crm.tables.EmailContact.EMAIL_CONTACT;
import static fintech.bo.db.jooq.lending.tables.Discount.DISCOUNT;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;

@Slf4j
@Setter
public class LoanDataProvider extends SearchableJooqClientDataProvider<Record> {

    public static final Field<LocalDateTime> FIELD_CLIENT_CREATED_AT = CLIENT.CREATED_AT.as("client_created_at");

    private static final Map<String, Function<String, Condition>> searchFields =
        ImmutableMap.<String, Function<String, Condition>>builder()
            .put(LOAN_NUMBER, val -> LOAN.LOAN_NUMBER.likeIgnoreCase(val + "%"))
            .putAll(DEFAULT_CLIENT_SEARCH_FIELDS)
            .put(SECOND_LAST_NAME, val -> CLIENT.SECOND_LAST_NAME.likeIgnoreCase(StringUtils.wrap(val, "%")).and(deletedClientCondition()))
            .build();

    private String statusDetail;
    private String status;
    private Long clientId;
    private LocalDate issueDateFrom;
    private LocalDate issueDateTo;
    private LocalDate dueDateFrom;
    private LocalDate dueDateTo;
    private LocalDate closeDateFrom;
    private LocalDate closeDateTo;

    public LoanDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
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
            LOAN.fields(),
            FIELD_CLIENT_NAME,
            FIELD_CLIENT_CREATED_AT,
            CLIENT.FIRST_NAME,
            CLIENT.LAST_NAME,
            CLIENT.SECOND_LAST_NAME,
            CLIENT.DOCUMENT_NUMBER,
            EMAIL_CONTACT.EMAIL,
            DISCOUNT.RATE_IN_PERCENT
        ))
            .from(LOAN)
            .join(CLIENT).on(LOAN.CLIENT_ID.eq(CLIENT.ID))
            .leftJoin(EMAIL_CONTACT).on(EMAIL_CONTACT.CLIENT_ID.eq(CLIENT.ID).and(EMAIL_CONTACT.IS_PRIMARY))
            .leftJoin(Discount.DISCOUNT).on(LOAN.DISCOUNT_ID.eq(DISCOUNT.ID));
    }

    protected void setFilters() {
        resetFilterConditions();
        addFilterCondition(clientId, LOAN.CLIENT_ID::eq);
        addFilterCondition(statusDetail, LOAN.STATUS_DETAIL::eq);
        addFilterCondition(status, LOAN.STATUS::eq);
        addFilterCondition(issueDateFrom, LOAN.ISSUE_DATE::ge);
        addFilterCondition(issueDateTo, LOAN.ISSUE_DATE::le);
        addFilterCondition(dueDateFrom, LOAN.PAYMENT_DUE_DATE::ge);
        addFilterCondition(dueDateTo, LOAN.PAYMENT_DUE_DATE::le);
        addFilterCondition(closeDateFrom, LOAN.CLOSE_DATE::ge);
        addFilterCondition(closeDateTo, LOAN.CLOSE_DATE::le);
    }

    @Override
    protected Object id(Record item) {
        return item.get(LOAN.ID);
    }

    @Override
    protected List<QuerySortOrder> getDefaultSortOrders() {
        return QuerySortOrder.desc(LOAN.ISSUE_DATE.getName()).build();
    }
}
