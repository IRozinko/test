package fintech.bo.components.task;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.task.tables.Log;
import fintech.bo.db.jooq.task.tables.records.LogRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

@Slf4j
public class TaskLogDataProvider extends JooqDataProvider<LogRecord> {

    private Long taskId;

    public TaskLogDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<LogRecord> buildSelect(Query<LogRecord, String> query) {
        SelectWhereStep<LogRecord> select = db.selectFrom(Log.LOG);
        if (taskId != null) {
            select.where(Log.LOG.TASK_ID.eq(taskId));
        }
        return select;
    }

    @Override
    protected Object id(LogRecord item) {
        return item.getId();
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
}
