package fintech.bo.components.task;

import com.vaadin.ui.Component;
import fintech.bo.api.client.TaskApiClient;
import fintech.bo.api.model.StringRequest;
import fintech.bo.api.model.task.TaskDefinitionResponse;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.api.TaskHelper;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.db.jooq.task.tables.records.TaskRecord;
import fintech.retrofit.RetrofitHelper;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import static fintech.bo.components.task.TaskResolution.TaskResolutionBuilder.resolution;

public abstract class CommonTaskView implements TaskView {

    @Getter
    private TaskRecord task;

    @Getter
    private final TaskHelper helper;
    private final TaskApiClient taskApiClient;

    public CommonTaskView() {
        this.helper = ApiAccessor.gI().get(TaskHelper.class);
        this.taskApiClient = ApiAccessor.gI().get(TaskApiClient.class);
    }

    @Override
    public Component build(BusinessObjectLayout baseLayout, TaskRecord task) {
        this.task = task;
        return buildView(baseLayout);
    }

    public abstract Component buildView(BusinessObjectLayout baseLayout);

    public List<TaskResolution> resolutions() {
        TaskDefinitionResponse definitionResponse = RetrofitHelper.syncCall(taskApiClient.taskDefinition(new StringRequest(task.getTaskType()))).get();

        return definitionResponse.getResolutions().stream().map(
            r -> {
                TaskResolution.TaskResolutionBuilder taskResolutionBuilder = resolution(r.getResolution())
                    .withDetails(r.getResolutionDetails());
                if (r.isPostpone()) {
                    taskResolutionBuilder.postponeTask().withPostponeHours(r.getPostponeHours());
                } else {
                    taskResolutionBuilder.completeTask();
                }
                if (r.isCommentRequired()) {
                    taskResolutionBuilder.commentRequired();
                }
                return taskResolutionBuilder.build();
            }
        ).collect(Collectors.toList());
    }
}
