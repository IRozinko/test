package fintech.bo.components.application;

import com.google.common.collect.ImmutableMap;
import com.vaadin.data.provider.Query;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.client.SearchableJooqClientDataProvider;
import fintech.bo.components.common.SearchFieldValue;
import fintech.bo.db.jooq.crm.tables.Client;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.components.common.SearchFieldOptions.ALL;
import static fintech.bo.components.common.SearchFieldOptions.APPLICATION_NUMBER;
import static fintech.bo.components.common.SearchFieldOptions.CLOSE_REASON;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.crm.tables.EmailContact.EMAIL_CONTACT;
import static fintech.bo.db.jooq.lending.Tables.PROMO_CODE;
import static fintech.bo.db.jooq.lending.tables.Discount.DISCOUNT;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.db.jooq.lending.tables.LoanApplication.LOAN_APPLICATION;
import static fintech.bo.db.jooq.workflow.tables.Activity.ACTIVITY;
import static fintech.bo.db.jooq.workflow.tables.Workflow.WORKFLOW_;
import static org.jooq.impl.DSL.exists;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.nvl;
import static org.jooq.impl.DSL.select;
import static org.jooq.util.postgres.PostgresDSL.array;

@Slf4j
@Setter
public class LoanApplicationDataProvider extends SearchableJooqClientDataProvider<Record> {

    public static final Field<String[]> FIELD_ACTIVE_WORKFLOW_STEPS = array(select(ACTIVITY.NAME).from(ACTIVITY).where(ACTIVITY.WORKFLOW_ID.eq(WORKFLOW_.ID).and(ACTIVITY.STATUS.eq("ACTIVE"))));

    private static final Map<String, Function<String, Condition>> searchFields =
        ImmutableMap.<String, Function<String, Condition>>builder()
            .put(APPLICATION_NUMBER, val -> LOAN_APPLICATION.APPLICATION_NUMBER.likeIgnoreCase(val + "%"))
            .putAll(DEFAULT_CLIENT_SEARCH_FIELDS)
            .put(CLOSE_REASON, val -> LOAN_APPLICATION.CLOSE_REASON.likeIgnoreCase(StringUtils.wrap(val, "%")))
            .build();

    private String type;
    private String statusDetail;
    private String workflowStep;
    private String sourceType;
    private String sourceName;
    private String closeReason;
    private LocalDate submitDateFrom;
    private LocalDate submitDateTo;
    private LocalDate closeDateFrom;
    private LocalDate closeDateTo;
    private Long clientId;

    public LoanApplicationDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
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
            LOAN_APPLICATION.fields(),
            FIELD_CLIENT_NAME,
            LOAN.LOAN_NUMBER,
            FIELD_ACTIVE_WORKFLOW_STEPS,
            CLIENT.DOCUMENT_NUMBER,
            CLIENT.PHONE,
            nvl(PROMO_CODE.RATE_IN_PERCENT, DISCOUNT.RATE_IN_PERCENT).as(field(name("discount_in_percent")))
        ))
            .from(LOAN_APPLICATION
                .join(CLIENT).on(LOAN_APPLICATION.CLIENT_ID.eq(CLIENT.ID))
                .leftJoin(EMAIL_CONTACT).on(EMAIL_CONTACT.CLIENT_ID.eq(Client.CLIENT.ID).and(EMAIL_CONTACT.IS_PRIMARY))
                .leftJoin(LOAN).on(LOAN_APPLICATION.LOAN_ID.eq(LOAN.ID))
                .leftJoin(WORKFLOW_).on(LOAN_APPLICATION.ID.eq(WORKFLOW_.APPLICATION_ID).and(WORKFLOW_.STATUS.eq("ACTIVE")))
            )
            .leftJoin(DISCOUNT).on(LOAN_APPLICATION.DISCOUNT_ID.eq(DISCOUNT.ID))
            .leftJoin(PROMO_CODE).on(LOAN_APPLICATION.PROMO_CODE_ID.eq(PROMO_CODE.ID));
    }

    protected void setFilters() {
        resetFilterConditions();
        addFilterCondition(type, LOAN_APPLICATION.TYPE::eq);
        addFilterCondition(clientId, LOAN_APPLICATION.CLIENT_ID::eq);
        addFilterCondition(statusDetail, LOAN_APPLICATION.STATUS_DETAIL::eq);
        addFilterCondition(workflowStep, step ->
            exists(
                select(ACTIVITY.ID)
                    .from(ACTIVITY)
                    .where(ACTIVITY.WORKFLOW_ID.eq(WORKFLOW_.ID)
                        .and(ACTIVITY.STATUS.eq("ACTIVE"))
                        .and(ACTIVITY.NAME.eq(step))))
        );
        addFilterCondition(sourceType, LOAN_APPLICATION.SOURCE_TYPE::eq);
        addFilterCondition(sourceName, LOAN_APPLICATION.SOURCE_NAME::eq);
        addFilterCondition(closeReason, LOAN_APPLICATION.CLOSE_REASON::eq);
        addFilterCondition(submitDateFrom, dateFrom -> LOAN_APPLICATION.SUBMITTED_AT.ge(dateFrom.atStartOfDay()));
        addFilterCondition(submitDateTo, dateTo -> LOAN_APPLICATION.SUBMITTED_AT.lt(dateTo.plusDays(1).atStartOfDay()));
        addFilterCondition(closeDateFrom, LOAN_APPLICATION.CLOSE_DATE::ge);
        addFilterCondition(closeDateTo, LOAN_APPLICATION.CLOSE_DATE::le);
    }

    @Override
    protected Object id(Record item) {
        return item.get(LOAN_APPLICATION.ID);
    }
}
