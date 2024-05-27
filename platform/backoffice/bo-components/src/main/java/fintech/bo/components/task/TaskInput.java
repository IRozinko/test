package fintech.bo.components.task;

import com.vaadin.ui.Component;
import fintech.bo.api.model.task.CompleteTaskRequest;
import fintech.bo.db.jooq.task.tables.records.TaskRecord;
import lombok.Data;

@Data
public class TaskInput {

    private TaskRecord task;
    private TaskResolution resolution;
    private String resolutionDetail;
    private String resolutionSubDetail;
    private String comment;
    private Long postponeByHours;
    private Component customComponent;

    public CompleteTaskRequest buildRequest() {
        return this.resolution.getRequestBuilder().apply(this);
    }

    public static CompleteTaskRequest complete(TaskInput input) {
        CompleteTaskRequest request = newRequest(input);
        request.setPostpone(false);
        return request;
    }

    public static CompleteTaskRequest postpone(TaskInput input) {
        CompleteTaskRequest request = newRequest(input);
        request.setPostpone(true);
        request.setPostponeByHours(input.getPostponeByHours());
        return request;
    }

    private static CompleteTaskRequest newRequest(TaskInput input) {
        CompleteTaskRequest request = new CompleteTaskRequest();
        request.setComment(input.getComment());
        request.setResolution(input.getResolution().getResolution());
        request.setResolutionDetail(input.getResolutionDetail());
        request.setResolutionSubDetail(input.getResolutionSubDetail());
        request.setTaskId(input.getTask().getId());
        return request;
    }
}
