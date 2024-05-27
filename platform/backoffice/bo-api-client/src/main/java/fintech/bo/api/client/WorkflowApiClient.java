package fintech.bo.api.client;

import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.workflow.AddEditDynamicActivityListenerRequest;
import fintech.bo.api.model.workflow.TerminateWorkflowRequest;
import fintech.bo.api.model.workflow.WorkflowInfoResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

public interface WorkflowApiClient {

    @POST("api/bo/workflows/terminate")
    Call<Void> terminate(@Body TerminateWorkflowRequest request);

    @GET("api/bo/workflows/list")
    Call<List<WorkflowInfoResponse>> listWorkflows();

    @POST("api/bo/workflows/add-edit-listener")
    Call<Void> addEditDynamicActivityListener(@Body AddEditDynamicActivityListenerRequest request);

    @POST("api/bo/workflows/remove-listener")
    Call<Void> removeListener(@Body IdRequest request);
}
