package fintech.bo.components.workflow;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.workflow.tables.records.ActivityRecord;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.workflow.Tables.ACTIVITY;

public class ActivitiesDataProvider extends JooqDataProvider<ActivityRecord> {

    private Long workflowId;

    public ActivitiesDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<ActivityRecord> buildSelect(Query<ActivityRecord, String> query) {
        SelectWhereStep<ActivityRecord> select = db.selectFrom(ACTIVITY);
        if (workflowId != null) {
            select.where(ACTIVITY.WORKFLOW_ID.eq(workflowId));
        }
        return select;
    }

    @Override
    protected Object id(ActivityRecord item) {
        return item.getId();
    }


    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }
}
