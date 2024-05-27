package fintech.dc.commands;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class LogDebtActionCommand {

    private Long debtId;

    private String actionName;

    private String comments;

    private String agent;

    private String status;

    private String subStatus;

    private String resolution;

    private LocalDateTime nextActionAt;

    private String nextAction;

    private Map<String, BulkAction> bulkActions = new HashMap<>();

    @Data
    public static class BulkAction {
        private Map<String, Object> params = new HashMap<>();
    }
}
