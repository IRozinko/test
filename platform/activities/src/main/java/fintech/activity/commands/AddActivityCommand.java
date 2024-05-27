package fintech.activity.commands;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString(of = {"clientId", "action"})
@Data
public class AddActivityCommand {

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

    private List<BulkAction> bulkActions = new ArrayList<>();

    @Data
    public static class BulkAction {
        private String type;
        private Map<String, Object> params = new HashMap<>();
    }
}
