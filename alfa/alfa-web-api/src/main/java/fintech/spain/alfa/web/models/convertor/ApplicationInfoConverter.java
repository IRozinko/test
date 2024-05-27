package fintech.spain.alfa.web.models.convertor;

import com.google.common.base.Converter;
import fintech.lending.core.application.LoanApplication;
import fintech.spain.alfa.web.models.ApplicationInfo;
import fintech.workflow.Activity;
import fintech.workflow.ActivityStatus;
import fintech.workflow.WorkflowService;

public class ApplicationInfoConverter extends Converter<LoanApplication, ApplicationInfo> {

    private final WorkflowService workflowService;

    public ApplicationInfoConverter(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @Override
    protected ApplicationInfo doForward(LoanApplication application) {
        ApplicationInfo info = new ApplicationInfo();
        info.setId(application.getId());
        info.setStatus(application.getStatus().name());
        info.setStatusDetail(application.getStatusDetail());
        info.setType(application.getType().name());
        info.setCloseDate(application.getCloseDate());
        info.setSubmittedAt(application.getSubmittedAt());
        info.setRequestedPrincipal(application.getRequestedPrincipal());
        info.setOfferedPrincipal(application.getOfferedPrincipal());

        if (application.getWorkflowId() != null) {
            workflowService.getWorkflow(application.getWorkflowId()).getActivities()
                .stream()
                .filter(a -> a.getStatus() == ActivityStatus.ACTIVE)
                .map(Activity::getName)
                .findFirst()
                .ifPresent(info::setCurrentActivity);
        }
        return info;
    }

    @Override
    protected LoanApplication doBackward(ApplicationInfo applicationInfo) {
        throw new UnsupportedOperationException();
    }
}
