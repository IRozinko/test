package fintech.bo.components.task;

import fintech.TimeMachine;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Map;

import static fintech.bo.db.jooq.task.tables.Agent.AGENT;
import static fintech.bo.db.jooq.task.tables.Task.TASK_;
import static org.jooq.impl.DSL.any;
import static org.jooq.impl.DSL.inline;
import static org.jooq.util.postgres.PostgresDSL.stringToArray;

@Slf4j
@Component
public class TaskQueueCache {

    private final DSLContext db;

    @Autowired
    public TaskQueueCache(DSLContext db) {
        this.db = db;
    }

    @Cacheable(value = "tasks_count", cacheManager = "cacheManager10Sec", sync = true)
    public Map<String, Integer> count() {
        try {
            log.debug("Requesting tasks count");

            //TODO task and agent don't share a foreign key and task types is a comma separated string
            return db.select(AGENT.EMAIL, TASK_.ID.count())
                .from(TASK_).join(AGENT)
                .on(TASK_.AGENT.eq(AGENT.EMAIL)
                    .or(TASK_.AGENT.isNull().and(TASK_.TASK_TYPE.eq(any(stringToArray(AGENT.TASK_TYPES, ","))).or(AGENT.TASK_TYPES.eq("*")))))
                .where(TASK_.DUE_AT.le(TimeMachine.now())
                    .and(TASK_.EXPIRES_AT.gt(TimeMachine.now()))
                    .and(TASK_.STATUS.eq("OPEN"))
                    .and(TASK_.PARENT_TASK_ID.isNull()))
                .groupBy(AGENT.EMAIL)
                .union(
                    db.select(inline("NOT_ASSIGNED"), TASK_.ID.count())
                        .from(TASK_)
                        .where(TASK_.DUE_AT.le(TimeMachine.now())
                            .and(TASK_.EXPIRES_AT.gt(TimeMachine.now()))
                            .and(TASK_.STATUS.eq("OPEN"))
                            .and(TASK_.AGENT.isNull())
                            .and(TASK_.PARENT_TASK_ID.isNull()))
                )
                .fetchMap(AGENT.EMAIL, TASK_.ID.count());
        } catch (Exception e) {
            log.error("Failed to task count", e);
            return null;
        }
    }
}
