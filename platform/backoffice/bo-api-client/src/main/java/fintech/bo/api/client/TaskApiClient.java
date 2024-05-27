package fintech.bo.api.client;

import fintech.bo.api.model.StringRequest;
import fintech.bo.api.model.task.AssignTaskRequest;
import fintech.bo.api.model.task.CompleteTaskRequest;
import fintech.bo.api.model.task.TakeNextTaskResponse;
import fintech.bo.api.model.task.TaskCountResponse;
import fintech.bo.api.model.task.TaskDefinitionResponse;
import fintech.bo.api.model.task.TaskTypesResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface TaskApiClient {

    @POST("api/bo/tasks/count")
    Call<TaskCountResponse> count();

    @POST("api/bo/tasks/take-next")
    Call<TakeNextTaskResponse> takeNext();

    @GET("api/bo/tasks/task-types")
    Call<TaskTypesResponse> taskTypes();

    @POST("api/bo/tasks/task-definition")
    Call<TaskDefinitionResponse> taskDefinition(@Body StringRequest request);

    @POST("api/bo/tasks/complete")
    Call<TaskCountResponse> complete(@Body CompleteTaskRequest request);

    @POST("api/bo/tasks/assign")
    Call<Void> assign(@Body AssignTaskRequest request);

}
