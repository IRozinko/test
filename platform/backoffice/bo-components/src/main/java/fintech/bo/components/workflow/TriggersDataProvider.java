package fintech.bo.components.workflow;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.workflow.tables.records.TriggerRecord;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.workflow.Tables.TRIGGER;

public class TriggersDataProvider extends JooqDataProvider<TriggerRecord> {

    private Long workflowId;

    public TriggersDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<TriggerRecord> buildSelect(Query<TriggerRecord, String> query) {
        SelectWhereStep<TriggerRecord> select = db.selectFrom(TRIGGER);
        if (workflowId != null) {
            select.where(TRIGGER.WORKFLOW_ID.eq(workflowId));
        }
        return select;
    }

    @Override
    protected Object id(TriggerRecord item) {
        return item.getId();
    }


    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }
}
