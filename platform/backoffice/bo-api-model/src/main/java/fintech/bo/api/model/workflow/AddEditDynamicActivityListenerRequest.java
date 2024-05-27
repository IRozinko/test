package fintech.bo.api.model.workflow;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@Accessors(chain = true)
@Data
public class AddEditDynamicActivityListenerRequest {
    public static final String STATUS_STARTED = "STARTED";
    public static final String STATUS_COMPLETED = "COMPLETED";

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String workflowName;
    @NotNull
    private Integer version;

    @NotNull
    private String listenerStatus;
    @NotNull
    private String triggerName;

    private String resolution;

    @NotNull
    private String activityName;

    @NotNull
    private String[] params;
    private Integer delaySec;

    private Boolean fromMidnight;

    @AssertTrue
    public boolean isStatusValid() {
        return listenerStatus.equals(STATUS_STARTED) || listenerStatus.equals(STATUS_COMPLETED);
    }

    @AssertTrue
    public boolean isResolutionValid() {
        return (listenerStatus.equals(STATUS_STARTED) && resolution == null) || (listenerStatus.equals(STATUS_COMPLETED) && resolution != null);
    }

}
