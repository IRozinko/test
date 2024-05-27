package fintech.bo.components.workflow;

import fintech.bo.db.jooq.workflow.tables.records.WorkflowAttributeRecord;
import fintech.bo.db.jooq.workflow.tables.records.WorkflowRecord;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.bo.db.jooq.workflow.Tables.ACTIVITY;
import static fintech.bo.db.jooq.workflow.Tables.WORKFLOW_ATTRIBUTE;
import static fintech.bo.db.jooq.workflow.tables.Workflow.WORKFLOW_;

@Component
public class WorkflowQueries {

    @Autowired
    private DSLContext db;

    public WorkflowRecord findById(Long id) {
        return db.selectFrom(WORKFLOW_).where(WORKFLOW_.ID.eq(id)).fetchOne();
    }

    public Result<WorkflowAttributeRecord> findAttributes(Long workflowId) {
        return db.selectFrom(WORKFLOW_ATTRIBUTE).where(WORKFLOW_ATTRIBUTE.WORKFLOW_ID.eq(workflowId)).fetch();
    }

    public Optional<WorkflowAttributeRecord> findAttributeByKey(Long workflowId, String key) {
        return Optional.ofNullable(db.selectFrom(WORKFLOW_ATTRIBUTE).where(WORKFLOW_ATTRIBUTE.WORKFLOW_ID.eq(workflowId).and(WORKFLOW_ATTRIBUTE.KEY.eq(key))).fetchOne());
    }


    public List<String> findActivityNames(String... status) {
        return db.selectDistinct(ACTIVITY.NAME).from(ACTIVITY).where(ACTIVITY.STATUS.in(status)).fetch().stream()
            .map(Record1::value1).sorted(String::compareTo)
            .collect(Collectors.toList());
    }
}
