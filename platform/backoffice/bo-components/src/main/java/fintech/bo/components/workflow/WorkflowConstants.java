package fintech.bo.components.workflow;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class WorkflowConstants {

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_TERMINATED = "TERMINATED";
    public static final List<String> ALL_STATUSES = ImmutableList.of(STATUS_ACTIVE, STATUS_COMPLETED, STATUS_TERMINATED);

    public static final String ACTIVITY_STATUS_WAITING = "WAITING";
    public static final String ACTIVITY_STATUS_ACTIVE = "ACTIVE";
    public static final String ACTIVITY_STATUS_COMPLETED = "COMPLETED";
    public static final String ACTIVITY_STATUS_FAILED = "FAILED";
    public static final String ACTIVITY_STATUS_CANCELLED = "CANCELLED";
}
