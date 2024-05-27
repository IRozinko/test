package fintech.workflow;

import fintech.Validate;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Data
public class Workflow {

    private Long id;
    private Long clientId;
    private Long applicationId;
    private Long loanId;
    private String name;
    private Integer version;
    private WorkflowStatus status;
    private Map<String, String> attributes = new HashMap<>();
    private List<Activity> activities = new ArrayList<>();
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private String terminateReason;
    private Long parentWorkflowId;

    public String attribute(String key) {
        checkNotNull(key);
        String value = attributes.get(key);
        Validate.notBlank(value, "No attribute found by key: [%s]", key);
        return value;
    }

    public Long attributeAsLong(String key) {
        return Long.valueOf(attribute(key));
    }

    public Optional<Activity> getCurrentActivity() {
        return activities.stream()
            .filter(Activity::isActive)
            .findFirst();
    }

    public List<Activity> getActiveActivities() {
        return activities.stream()
            .filter(Activity::isActive)
            .collect(Collectors.toList());
    }


    public Activity activity(String name) {
        checkNotNull(name);
        return findActivity(name)
            .orElseThrow(() -> new IllegalArgumentException(String.format("Activity not found by name %s", name)));
    }

    public Optional<Activity> findActivity(String name) {
        return activities.stream()
            .filter((a) -> name.equals(a.getName()))
            .findFirst();
    }

    public boolean hasAttribute(String key) {
        checkNotNull(key);
        return this.attributes.containsKey(key);
    }

}
