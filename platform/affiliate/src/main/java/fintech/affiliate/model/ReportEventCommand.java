package fintech.affiliate.model;

import lombok.Data;

@Data
public class ReportEventCommand {

    private EventType eventType;
    private Long clientId;
    private Long applicationId;
    private Long loanId;
}
