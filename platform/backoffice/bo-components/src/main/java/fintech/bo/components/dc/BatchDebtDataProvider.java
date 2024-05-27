package fintech.bo.components.dc;

import com.google.common.collect.ImmutableMap;
import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.client.SearchableJooqClientDataProvider;
import fintech.bo.components.common.SearchFieldValue;
import fintech.bo.db.jooq.crm.tables.Client;
import fintech.bo.db.jooq.dc.Tables;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.db.jooq.crm.Tables.CLIENT;
import static fintech.bo.db.jooq.crm.tables.EmailContact.EMAIL_CONTACT;
import static fintech.bo.db.jooq.dc.Tables.DEBT;

public class BatchDebtDataProvider extends SearchableJooqClientDataProvider<Record> {

    private static final Map<String, Function<String, Condition>> searchFields =
        ImmutableMap.<String, Function<String, Condition>>builder()
            .put("Loan Number", val -> Tables.DEBT.LOAN_NUMBER.in(StringUtils.deleteWhitespace(val).split(",")))
            .build();

    public BatchDebtDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
        super(db, searchFields, jooqClientDataService);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = createSelect();
        Optional.ofNullable(searchFieldValue).ifPresent(SearchFieldValue::removeWhiteSpaces);
        applySearch(select, searchFieldValue);
        applyFilter(select);
        return select;
    }

    private SelectWhereStep<Record> createSelect() {
        return db.select(
            JooqDataProvider.fields(
                DEBT.fields(),
                CLIENT.ID,
                CLIENT.CLIENT_NUMBER,
                CLIENT.PHONE,
                EMAIL_CONTACT.EMAIL,
                CLIENT.DOCUMENT_NUMBER,
                FIELD_CLIENT_NAME
            )).from(DEBT)
            .join(CLIENT).on(DEBT.CLIENT_ID.eq(CLIENT.ID))
            .leftJoin(EMAIL_CONTACT).on(EMAIL_CONTACT.CLIENT_ID.eq(Client.CLIENT.ID).and(EMAIL_CONTACT.IS_PRIMARY));
    }

    public void setFilters(DebtFilterRequest request) {
        resetFilterConditions();
        addFilterCondition(request.getMinDpd(), DEBT.DPD::greaterOrEqual);
        addFilterCondition(request.getMaxDpd(), DEBT.DPD::lessOrEqual);
        addFilterCondition(request.getAgent(), v -> DcConstants.UNASSIGNED_AGENT.equals(v) ? DEBT.AGENT.isNull() : DEBT.AGENT.eq(v));
        addFilterCondition(request.getPortfolio(), DEBT.PORTFOLIO::eq);
        addFilterCondition(request.getAging(), DEBT.AGING_BUCKET::eq);
        addFilterCondition(request.getStatus(), DEBT.STATUS::eq);
        addFilterCondition(request.getLoanStatusDetail(), DEBT.LOAN_STATUS_DETAIL::eq);
        addFilterCondition(request.getManagingCompany(), DEBT.MANAGING_COMPANY::eq);
        addFilterCondition(request.getOwningCompany(), DEBT.OWNING_COMPANY::eq);
    }

    @Override
    protected Object id(Record item) {
        return item.get(DEBT.ID);
    }

}
