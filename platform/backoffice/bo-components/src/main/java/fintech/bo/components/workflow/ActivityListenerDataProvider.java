package fintech.bo.components.workflow;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.workflow.tables.ActivityListener;
import fintech.bo.db.jooq.workflow.tables.records.ActivityListenerRecord;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

@Slf4j
public class ActivityListenerDataProvider extends JooqDataProvider<ActivityListenerRecord> {

    @Setter
    private String workflowName;

    @Setter
    private Integer version;

    public ActivityListenerDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<ActivityListenerRecord> buildSelect(Query<ActivityListenerRecord, String> query) {
        SelectWhereStep<ActivityListenerRecord> select = db
            .selectFrom(ActivityListener.ACTIVITY_LISTENER);

        if (version != null) {
            select.where(ActivityListener.ACTIVITY_LISTENER.WORKFLOW_VERSION.eq(version));
        }
        if (workflowName != null) {
            select.where(ActivityListener.ACTIVITY_LISTENER.WORKFLOW_NAME.eq(workflowName));
        }
        return select;
    }

    @Override
    protected Object id(ActivityListenerRecord item) {
        return item.getId();
    }


}
