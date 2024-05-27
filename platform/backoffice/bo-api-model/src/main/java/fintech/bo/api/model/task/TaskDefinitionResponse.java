package fintech.bo.api.model.task;

import lombok.Data;

import java.util.List;

@Data
public class TaskDefinitionResponse {

    private List<TaskResolutionData> resolutions;
    private boolean single;
    private List<String> possibleSubTasks;
    private String group;
    private String description;

    @Data
    public static class TaskResolutionData {
        private String resolution;
        private List<String> resolutionDetails;
        private List<Long> postponeHours;
        private boolean commentRequired;
        private boolean postpone;
    }

}
