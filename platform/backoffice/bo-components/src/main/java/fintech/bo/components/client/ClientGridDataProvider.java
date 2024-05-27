package fintech.bo.components.client;

import com.google.common.collect.ImmutableMap;
import com.vaadin.data.provider.Query;
import fintech.bo.components.common.SearchFieldValue;
import fintech.bo.components.workflow.WorkflowConstants;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;

import static fintech.bo.components.client.ClientDataProviderUtils.deletedClientCondition;
import static fintech.bo.components.common.SearchFieldOptions.ALL;
import static fintech.bo.components.common.SearchFieldOptions.CLIENT_NUMBER;
import static fintech.bo.components.common.SearchFieldOptions.SEGMENTS_TEXT;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.crm.tables.EmailContact.EMAIL_CONTACT;
import static fintech.bo.db.jooq.lending.Tables.LOAN_APPLICATION;
import static fintech.bo.db.jooq.workflow.Tables.ACTIVITY;
import static fintech.bo.db.jooq.workflow.Tables.WORKFLOW_;
import static fintech.bo.db.jooq.workflow.Workflow.WORKFLOW;

@Slf4j
@Setter
public class ClientGridDataProvider extends SearchableJooqClientDataProvider<Record> {

    private static final Map<String, Function<String, Condition>> searchFields =
        ImmutableMap.<String, Function<String, Condition>>builder()
            .put(CLIENT_NUMBER, val -> CLIENT.CLIENT_NUMBER.likeIgnoreCase(val + "%"))
            .putAll(DEFAULT_CLIENT_SEARCH_FIELDS)
            .put(SEGMENTS_TEXT, val -> CLIENT.SEGMENTS_TEXT.likeIgnoreCase(StringUtils.wrap(val, "%")).and(deletedClientCondition()))
            .build();

    public static final Field<String> FIELD_NEXT_ACTIVITY = DSL.select(DSL.max(ACTIVITY.NAME))
        .from(ACTIVITY)
        .join(WORKFLOW_).on(ACTIVITY.WORKFLOW_ID.eq(WORKFLOW.WORKFLOW_.ID))
        .where(ACTIVITY.STATUS.eq(WorkflowConstants.ACTIVITY_STATUS_ACTIVE).and(WORKFLOW_.CLIENT_ID.eq(CLIENT.ID)))
        .asField("next_activity");

    public static final Field<String> FIELD_LAST_CLOSE_REASON = DSL.select(LOAN_APPLICATION.CLOSE_REASON)
        .from(LOAN_APPLICATION)
        .where(
            LOAN_APPLICATION.ID.eq(
                DSL.select(DSL.max(LOAN_APPLICATION.ID)).from(LOAN_APPLICATION).where(LOAN_APPLICATION.CLIENT_ID.eq(CLIENT.ID))
            )
        ).asField("last_close_reason");

    private LocalDate createdFrom;
    private LocalDate createdTo;

    public ClientGridDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
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
        return db.select(
            fields(CLIENT.fields(),
                FIELD_NEXT_ACTIVITY,
                FIELD_LAST_CLOSE_REASON,
                EMAIL_CONTACT.EMAIL
            )
        )
            .from(CLIENT)
            .leftJoin(EMAIL_CONTACT).on(EMAIL_CONTACT.CLIENT_ID.eq(CLIENT.ID).and(EMAIL_CONTACT.IS_PRIMARY));
    }

    private void setFilters() {
        resetFilterConditions();
        addFilterCondition(createdFrom, createdFrom -> CLIENT.CREATED_AT.ge(createdFrom.atStartOfDay()));
        addFilterCondition(createdTo, createdTo -> CLIENT.CREATED_AT.lt(createdTo.plusDays(1).atStartOfDay()));
    }

    @Override
    protected Object id(Record item) {
        return item.get(CLIENT.ID);
    }
}
