package fintech.bo.api.server;

import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.quartz.QuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Secured({BackofficePermissions.ADMIN})
public class QuartzApiController {

    private final QuartzService quartzService;

    @Autowired
    public QuartzApiController(QuartzService quartzService) {
        this.quartzService = quartzService;
    }

    @PostMapping("api/bo/quartz/pause/{jobName}")
    public void pauseJob(@PathVariable String jobName) {
        quartzService.pauseJob(jobName);
    }

    @PostMapping("api/bo/quartz/resume/{jobName}")
    public void resumeJob(@PathVariable String jobName) {
        quartzService.resumeJob(jobName);
    }

    @PostMapping("api/bo/quartz/delete/{jobName}")
    public void deleteJob(@PathVariable String jobName) {
        quartzService.deleteJob(jobName);
    }

    @PostMapping("api/bo/quartz/pause")
    public void pauseScheduler() {
        quartzService.pauseScheduler();
    }

    @PostMapping("api/bo/quartz/resume")
    public void resumeScheduler() {
        quartzService.resumeScheduler();
    }
}
