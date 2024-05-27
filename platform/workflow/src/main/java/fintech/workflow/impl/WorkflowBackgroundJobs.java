package fintech.workflow.impl;

import com.google.common.base.Stopwatch;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import fintech.TimeMachine;
import fintech.workflow.ActivityStatus;
import fintech.workflow.Actor;
import fintech.workflow.TriggerStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static fintech.workflow.db.Entities.activity;
import static fintech.workflow.db.Entities.trigger;
import static fintech.workflow.db.Entities.workflow;

@Slf4j
@Component
@Transactional
public class WorkflowBackgroundJobs {

    private final ActivityCallables runner;
    private final JPQLQueryFactory queryFactory;

    private final int batchSize;

    @Autowired
    public WorkflowBackgroundJobs(ActivityCallables runner, JPQLQueryFactory queryFactory,
                                  @Value("${workflow.batchSize:50}") int batchSize) {
        this.batchSize = batchSize;
        this.runner = runner;
        this.queryFactory = queryFactory;
    }

    public void consumeNow() {
        run(TimeMachine.now());
    }

    public void run(LocalDateTime when) {
        expireActivities(when);
        executeTriggers(when);
        runSystemActivities(when);
    }

    public void runSystemActivities(LocalDateTime when) {
        JPQLQuery<Tuple> query = queryFactory.select(activity.id, activity.workflow.clientId)
            .from(activity)
            .where(activity.status.eq(ActivityStatus.ACTIVE)
                .and(activity.actor.eq(Actor.SYSTEM))
                .and(activity.nextAttemptAt.lt(when))
                .and(activity.workflow.suspended.isFalse()))
            .orderBy(activity.attempts.asc(), activity.nextAttemptAt.asc(), activity.id.asc())
            .limit(batchSize);
        int c = 0;
        Stopwatch stopwatch = Stopwatch.createStarted();
        try (CloseableIterator<Tuple> iterator = query.iterate()) {
            while (iterator.hasNext()) {
                Tuple activityData = iterator.next();
                try {
                    runner.systemActivityCallable(activityData.get(activity.id), activityData.get(activity.workflow.clientId)).call();
                } catch (Exception e) {
                    log.error("Failed to run system activity with id [" + activityData.get(trigger.id) + "]", e);
                }
                c++;
            }
        }
        if (c > 0) {
            log.info("Completed {} system activities in {} ms", c, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    public void executeTriggers(LocalDateTime when) {
        JPQLQuery<Tuple> query = queryFactory.select(trigger.id, trigger.activityId, workflow.clientId)
            .from(trigger)
            .join(workflow).on(trigger.workflowId.eq(workflow.id))
            .where(trigger.status.eq(TriggerStatus.WAITING)
                .and(trigger.nextAttemptAt.lt(when))
                .and(workflow.suspended.isFalse()))
            .orderBy(trigger.nextAttemptAt.asc(), trigger.id.asc())
            .limit(batchSize);
        int c = 0;
        Stopwatch stopwatch = Stopwatch.createStarted();
        try (CloseableIterator<Tuple> iterator = query.iterate()) {
            while (iterator.hasNext()) {
                Tuple triggerData = iterator.next();
                try {
                    runner.triggerCallable(triggerData.get(trigger.id), triggerData.get(trigger.activityId), triggerData.get(workflow.clientId)).call();
                } catch (Exception e) {
                    log.error("Failed trigger with id [" + triggerData.get(trigger.id) + "]", e);
                }
                c++;
            }
        }
        if (c > 0) {
            log.info("Completed to process {} activity triggers in {} ms", c, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    public void expireActivities(LocalDateTime when) {
        JPQLQuery<Tuple> query = queryFactory.select(activity.id, activity.workflow.clientId)
            .from(activity)
            .where(activity.status.eq(ActivityStatus.ACTIVE)
                .and(activity.expiresAt.lt(when))
                .and(activity.workflow.suspended.isFalse()))
            .orderBy(activity.expiresAt.asc(), activity.id.asc())
            .limit(batchSize);
        int c = 0;
        Stopwatch stopwatch = Stopwatch.createStarted();
        try (CloseableIterator<Tuple> iterator = query.iterate()) {
            while (iterator.hasNext()) {
                Tuple activityData = iterator.next();
                try {
                    runner.expiredActivityCallable(activityData.get(activity.id), activityData.get(activity.workflow.clientId)).call();
                } catch (Exception e) {
                    log.error("Failed to expire activity with id [" + activityData.get(activity.id) + "]", e);
                }
                c++;
            }
        }
        if (c > 0) {
            log.info("Completed to process {} expired activities in {} ms", c, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }
}
