package fintech.bo.api.model.activity;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class AddActivityRequest {

    private Long clientId;
    private String action;
    private String resolution;
    private String topic;
    private String comments;
    private List<BulkAction> bulkActions = new ArrayList<>();

    @Data
    public static class BulkAction {
        private String type;
        private Map<String, Object> params = new HashMap<>();
    }
}
