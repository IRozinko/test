package fintech.affiliate.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LeadReport {

    private Long leadId;
    private Long partnerId;
    private boolean unknownPartner;
    private Long clientId;
    private List<EventType> reportedEventTypes = new ArrayList<>();

    private String leadConditionWorkflowActivityName;
    private String leadConditionWorkflowActivityResolution;

    private boolean repeatedClient;
}
