package fintech.quartz;

public interface QuartzService {
    void pauseJob(String jobName);

    void resumeJob(String jobName);

    void deleteJob(String jobName);

    void triggerJob(String jobName);

    void pauseScheduler();

    void resumeScheduler();
}
