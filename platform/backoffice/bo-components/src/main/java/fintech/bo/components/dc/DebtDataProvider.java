package fintech.bo.components.dc;

import com.google.common.collect.ImmutableMap;
import com.vaadin.data.provider.Query;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.client.SearchableJooqClientDataProvider;
import fintech.bo.components.common.SearchFieldValue;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.function.Function;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.components.common.SearchFieldOptions.ALL;
import static fintech.bo.components.common.SearchFieldOptions.CLIENT_NUMBER;
import static fintech.bo.components.common.SearchFieldOptions.LOAN_NUMBER;
import static fintech.bo.db.jooq.crm.Tables.CLIENT;
import static fintech.bo.db.jooq.crm.Tables.EMAIL_CONTACT;
import static fintech.bo.db.jooq.dc.Tables.DEBT;

public class DebtDataProvider extends SearchableJooqClientDataProvider<Record> {

    private static final Map<String, Function<String, Condition>> searchFields =
        ImmutableMap.<String, Function<String, Condition>>builder()
            .put(LOAN_NUMBER, val -> DEBT.LOAN_NUMBER.likeIgnoreCase(val + "%"))
            .put(CLIENT_NUMBER, val -> CLIENT.CLIENT_NUMBER.likeIgnoreCase(StringUtils.wrap(val, "%")))
            .putAll(DEFAULT_CLIENT_SEARCH_FIELDS)
            .build();

    public DebtDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
        super(db, searchFields, jooqClientDataService);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = createSelect();

        query.getFilter().ifPresent(filter -> applySearch(select, new SearchFieldValue(ALL, filter)));
        applySearch(select);

        applyFilter(select);
        return select;
    }

    protected SelectWhereStep<Record> createSelect() {
        return db.select(fields(
            DEBT.fields(),
            CLIENT.CLIENT_NUMBER,
            CLIENT.PHONE,
            EMAIL_CONTACT.EMAIL,
            CLIENT.DOCUMENT_NUMBER,
            FIELD_CLIENT_NAME
        ))
            .from(DEBT)
            .join(CLIENT).on(DEBT.CLIENT_ID.eq(CLIENT.ID))
            .leftJoin(EMAIL_CONTACT).on(EMAIL_CONTACT.CLIENT_ID.eq(CLIENT.ID).and(EMAIL_CONTACT.IS_PRIMARY));
    }

    public void setFilters(DebtFilterRequest request) {
        resetFilterConditions();

        addFilterCondition(request.getAgent(), v -> DcConstants.UNASSIGNED_AGENT.equals(v) ? DEBT.AGENT.isNull() : DEBT.AGENT.eq(v));
        addFilterCondition(request.getMinDpd(), DEBT.DPD::greaterOrEqual);
        addFilterCondition(request.getMaxDpd(), DEBT.DPD::lessOrEqual);
        addFilterCondition(request.getNextActionFrom(), v -> DEBT.NEXT_ACTION_AT.gt(v.atStartOfDay()));
        addFilterCondition(request.getNextActionTo(), v -> DEBT.NEXT_ACTION_AT.lt(LocalDateTime.of(v, LocalTime.MAX)));
        addFilterCondition(request.getPortfolio(), DEBT.PORTFOLIO::eq);
        addFilterCondition(request.getAging(), DEBT.AGING_BUCKET::eq);
        addFilterCondition(request.getManagingCompany(), DEBT.MANAGING_COMPANY::eq);
        addFilterCondition(request.getOwningCompany(), DEBT.OWNING_COMPANY::eq);
        addFilterCondition(request.getStatus(), DEBT.STATUS::eq);
        addFilterCondition(request.getSubStatus(), DEBT.SUB_STATUS::eq);
        addFilterCondition(request.getLoanStatusDetail(), DEBT.LOAN_STATUS_DETAIL::eq);
    }

    @Override
    protected Object id(Record item) {
        return item.get(DEBT.ID);
    }
}
