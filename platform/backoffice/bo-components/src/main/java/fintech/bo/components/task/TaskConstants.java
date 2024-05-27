package fintech.bo.components.task;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class TaskConstants {

    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    public static final List<String> ALL_STATUSES = ImmutableList.of(STATUS_OPEN, STATUS_COMPLETED, STATUS_CANCELLED);
}
