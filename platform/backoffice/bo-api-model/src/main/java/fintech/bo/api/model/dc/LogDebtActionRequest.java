package fintech.bo.api.model.dc;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class LogDebtActionRequest {

    @NotNull
    private Long debtId;

    @NotNull
    private String actionName;

    private String comments;

    private String status;

    private String subStatus;

    private String resolution;

    private String nextAction;

    private LocalDateTime nextActionAt;

    private Map<String, BulkAction> bulkActions = new HashMap<>();

    @Data
    public static class BulkAction {
        private Map<String, Object> params = new HashMap<>();
    }
}
