package fintech.affiliate.model;

import lombok.Data;

@Data
public class SavePartnerCommand {

    private String name;
    private boolean active;
    private String leadReportUrl;
    private String repeatedClientLeadReportUrl;
    private String actionReportUrl;
    private String repeatedClientActionReportUrl;
    private String leadConditionWorkflowActivityName;
    private String leadConditionWorkflowActivityResolution;
    private String apiKey;
}
