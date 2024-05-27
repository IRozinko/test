package fintech.bo.components.task;

import com.google.common.collect.ImmutableMap;
import com.vaadin.data.provider.Query;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.client.SearchableJooqClientDataProvider;
import fintech.bo.components.common.SearchFieldValue;
import fintech.bo.db.jooq.task.tables.Task;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;

import java.util.Map;
import java.util.function.Function;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.components.client.ClientDataProviderUtils.deletedClientCondition;
import static fintech.bo.components.common.SearchFieldOptions.ALL;
import static fintech.bo.components.common.SearchFieldOptions.CLIENT_NUMBER;
import static fintech.bo.components.common.SearchFieldOptions.EMAIL;
import static fintech.bo.components.common.SearchFieldOptions.FIRST_NAME;
import static fintech.bo.components.common.SearchFieldOptions.LAST_NAME;
import static fintech.bo.components.common.SearchFieldOptions.TASK_AGENT;
import static fintech.bo.components.common.SearchFieldOptions.TASK_RESOLUTION;
import static fintech.bo.components.common.SearchFieldOptions.TASK_TYPE;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.crm.tables.EmailContact.EMAIL_CONTACT;
import static fintech.bo.db.jooq.task.Task.TASK;
import static fintech.bo.db.jooq.task.tables.Task.TASK_;

@Slf4j
@Setter
public class TaskDataProvider extends SearchableJooqClientDataProvider<Record> {

    public static final Field<Integer> FIELD_HOURS_DUE = DSL.field("(date_part('day', now() - due_at) * 24 + date_part('hour', now() - due_at))", Integer.class).as("hours_due");

    private static final Task ALIAS = TASK.TASK_.as("t");

    //selects comment of task or last comment of children's task
    public static final Field<String> FIELD_LAST_COMMENT = DSL.select(
        DSL.coalesce(
            TASK.TASK_.COMMENT,
            DSL.select(ALIAS.TASK_TYPE.concat(": ").concat(ALIAS.COMMENT)).from(ALIAS).where(ALIAS.PARENT_TASK_ID.eq(TASK.TASK_.ID).and(DSL.length(ALIAS.COMMENT).gt(0))).orderBy(ALIAS.ID.desc()).limit(1).asField()
        )
    ).asField("last_comment");

    private String status;
    private String taskType;
    private Long clientId;
    private Long applicationId;
    private Long workflowId;

    private static final Map<String, Function<String, Condition>> searchFields =
        ImmutableMap.<String, Function<String, Condition>>builder()
            .put(TASK_TYPE, val -> TASK_.TASK_TYPE.likeIgnoreCase(StringUtils.wrap(val, "%")))
            .put(TASK_AGENT, val -> TASK_.AGENT.likeIgnoreCase(StringUtils.wrap(val, "%")))
            .put(TASK_RESOLUTION, val -> TASK_.RESOLUTION.likeIgnoreCase(StringUtils.wrap(val, "%")))
            .put(CLIENT_NUMBER, val -> CLIENT.CLIENT_NUMBER.likeIgnoreCase(val + "%"))
            .put(FIRST_NAME, val -> CLIENT.FIRST_NAME.likeIgnoreCase(StringUtils.wrap(val, "%")).and(deletedClientCondition()))
            .put(LAST_NAME, val -> CLIENT.LAST_NAME.likeIgnoreCase(StringUtils.wrap(val, "%")).and(deletedClientCondition()))
            .put(EMAIL, val -> EMAIL_CONTACT.EMAIL.likeIgnoreCase(StringUtils.wrap(val, "%")).and(deletedClientCondition()))
            .build();

    public TaskDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
        super(db, searchFields, jooqClientDataService);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(fields(
                TASK.TASK_.fields(),
                FIELD_LAST_COMMENT,
                FIELD_HOURS_DUE,
                FIELD_CLIENT_NAME,
                CLIENT.CLIENT_NUMBER,
                EMAIL_CONTACT.EMAIL
            ))
            .from(TASK.TASK_
                .leftJoin(CLIENT).on(TASK.TASK_.CLIENT_ID.eq(CLIENT.ID))
                .leftJoin(EMAIL_CONTACT).on(EMAIL_CONTACT.CLIENT_ID.eq(CLIENT.ID).and(EMAIL_CONTACT.IS_PRIMARY)));

        select.where(TASK.TASK_.PARENT_TASK_ID.isNull());
        query.getFilter().ifPresent(filter -> applySearch(select, new SearchFieldValue(ALL, filter)));
        applySearch(select);
        setFilters();
        applyFilter(select);
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(TASK.TASK_.ID);
    }

    protected void setFilters() {
        resetFilterConditions();
        addFilterCondition(clientId, TASK.TASK_.CLIENT_ID::eq);
        addFilterCondition(applicationId, TASK.TASK_.APPLICATION_ID::eq);
        addFilterCondition(status, TASK.TASK_.STATUS::eq);
        addFilterCondition(taskType, TASK.TASK_.TASK_TYPE::eq);
        addFilterCondition(workflowId, TASK.TASK_.WORKFLOW_ID::eq);
    }
}
