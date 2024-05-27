package fintech.spain.alfa.product.affiliate;

import fintech.affiliate.AffiliateService;
import fintech.affiliate.model.EventType;
import fintech.affiliate.model.LeadReport;
import fintech.affiliate.model.ReportEventCommand;
import fintech.lending.core.loan.events.LoanIssuedEvent;
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows;
import fintech.workflow.Activity;
import fintech.workflow.Workflow;
import fintech.workflow.event.ActivityCompletedEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AffiliateEvents {

    private final AffiliateService affiliateService;

    @Autowired
    public AffiliateEvents(AffiliateService affiliateService) {
        this.affiliateService = affiliateService;
    }

    @EventListener
    public void activityCompleted(ActivityCompletedEvent event) {
        Workflow workflow = event.getWorkflow();

        // Should work only with UnderwritingWorkflows.FIRST_LOAN_AFFILIATE. Isn't it?
        if (!StringUtils.equals(workflow.getName(), UnderwritingWorkflows.FIRST_LOAN_AFFILIATE)) {
            return;
        }

        Activity activity = event.getActivity();
        Long clientId = workflow.getClientId();
        Long applicationId = workflow.getApplicationId();

        Optional<LeadReport> report = affiliateService.findLeadReportByClientIdAndApplicationId(clientId, applicationId);
        if (!report.isPresent()) {
            return;
        }
        if (!StringUtils.equalsIgnoreCase(report.get().getLeadConditionWorkflowActivityName(), activity.getName())) {
            return;
        }
        if (!StringUtils.equalsIgnoreCase(report.get().getLeadConditionWorkflowActivityResolution(), activity.getResolution())) {
            return;
        }
        ReportEventCommand command = new ReportEventCommand();
        command.setEventType(EventType.LEAD);
        command.setClientId(clientId);
        command.setApplicationId(workflow.getApplicationId());
        command.setLoanId(workflow.getLoanId());
        affiliateService.reportEvent(command);
    }

    @EventListener
    public void loanIssued(LoanIssuedEvent event) {
//        Long clientId = event.getLoan().getClientId();
//        Long applicationId = event.getLoan().getApplicationId();
//        Optional<LeadReport> report = affiliateService.findLeadReportByClientIdAndApplicationId(clientId, applicationId);
//        if (!report.isPresent()) {
//            return;
//        }
//        ReportEventCommand command = new ReportEventCommand();
//        command.setEventType(EventType.ACTION);
//        command.setClientId(clientId);
//        command.setApplicationId(event.getLoan().getApplicationId());
//        command.setLoanId(event.getLoan().getId());
//        affiliateService.reportEvent(command);
    }
}
