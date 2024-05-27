package fintech.quartz.impl;

import fintech.quartz.QuartzService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QuartzServiceBean implements QuartzService {

    private final Scheduler scheduler;

    @Autowired
    public QuartzServiceBean(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void pauseJob(String jobName) {
        try {
            scheduler.pauseTriggers(GroupMatcher.groupEquals(jobName));
            log.info("Job {} paused", jobName);
        } catch (SchedulerException e) {
            log.error("Error pausing job " + jobName, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resumeJob(String jobName) {
        try {
            scheduler.resumeTriggers(GroupMatcher.groupEquals(jobName));
            log.info("Job {} resumed", jobName);
        } catch (SchedulerException e) {
            log.error("Error resuming job " + jobName, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteJob(String jobName) {
        try {
            scheduler.deleteJob(JobKey.jobKey(jobName));
            log.info("Job {} deleted", jobName);
        } catch (SchedulerException e) {
            log.error("Error deleting job " + jobName, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void triggerJob(String jobName) {
        try {
            scheduler.triggerJob(JobKey.jobKey(jobName));
        } catch (SchedulerException e) {
            log.error("Error triggering job " + jobName, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pauseScheduler() {
        try {
            scheduler.pauseAll();
            log.info("Scheduler paused");
        } catch (SchedulerException e) {
            log.error("Error pausing scheduler");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resumeScheduler() {
        try {
            scheduler.resumeAll();
            log.info("Scheduler started");
        } catch (SchedulerException e) {
            log.error("Error starting scheduler");
            throw new RuntimeException(e);
        }
    }
}
