package fintech.rules.model;


import fintech.Validate;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class RuleContext {

    private LocalDateTime when;
    private Long clientId;
    private Long applicationId;
    private Long loanId;
    private Long workflowId;
    private String workflowName;
    private Map<String, String> attributes = new HashMap<>();

    public boolean hasAttribute(String key) {
        return attributes.get(key) != null;
    }

    public String getAttribute(String key) {
        return Validate.notNull(attributes.get(key), "Attribute not found by key [%s]", key);
    }
}
