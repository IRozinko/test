package fintech.bo.components.task;

import com.google.common.collect.ImmutableList;
import fintech.bo.api.client.TaskApiClient;
import fintech.bo.api.model.task.TaskTypesResponse;
import fintech.bo.db.jooq.task.tables.records.TaskAttributeRecord;
import fintech.bo.db.jooq.task.tables.records.TaskRecord;
import fintech.retrofit.RetrofitHelper;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static fintech.bo.db.jooq.task.Tables.TASK_ATTRIBUTE;
import static fintech.bo.db.jooq.task.Task.TASK;

@Component
public class TaskQueries {

    @Autowired
    private TaskApiClient taskApiClient;

    @Autowired
    private DSLContext db;

    public List<String> getTaskTypes() {
        return RetrofitHelper.syncCall(taskApiClient.taskTypes())
                .map(TaskTypesResponse::getTaskTypes)
                .orElse(ImmutableList.of());
    }

    public TaskRecord findById(Long id) {
        return db.selectFrom(TASK.TASK_).where(TASK.TASK_.ID.eq(id)).fetchOne();
    }

    public List<TaskRecord> findSubtasksByParentId(Long id) {
        return db.selectFrom(TASK.TASK_).where(TASK.TASK_.PARENT_TASK_ID.eq(id)).fetch();
    }

    public Result<TaskAttributeRecord> findAttributes(Long taskId) {
        return db.selectFrom(TASK_ATTRIBUTE).where(TASK_ATTRIBUTE.TASK_ID.eq(taskId)).fetch();
    }

    public Optional<TaskAttributeRecord> findAttributeByKey(Long taskId, String key) {
        TaskAttributeRecord attribute = db.selectFrom(TASK_ATTRIBUTE).where(TASK_ATTRIBUTE.TASK_ID.eq(taskId).and(TASK_ATTRIBUTE.KEY.eq(key))).fetchOne();
        return Optional.ofNullable(attribute);
    }

}
