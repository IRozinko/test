package fintech.activity.model;

import lombok.Data;

@Data
public class Activity {

    private Long id;
    private Long clientId;
    private String agent;
    private String action;
    private String resolution;
    private String source;
    private String topic;
    private String comments;
    private String details;
    private Long applicationId;
    private Long loanId;
    private Long taskId;
    private Long debtId;
    private Long debtActionId;
    private Long paymentId;
}
