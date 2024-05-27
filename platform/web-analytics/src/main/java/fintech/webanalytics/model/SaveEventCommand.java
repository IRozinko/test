package fintech.webanalytics.model;

import lombok.Data;

@Data
public class SaveEventCommand {

    private Long clientId;
    private Long applicationId;
    private Long loanId;
    private String ipAddress;
    private String eventType;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private String utmTerm;
    private String utmContent;
    private String gclid;
}
