package fintech.bo.api.client;

import fintech.admintools.ScenarioInfo;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.admintools.ExecuteAdminActionRequest;
import fintech.bo.api.model.admintools.ListAdminActionsResponse;
import fintech.bo.api.model.admintools.RunDemoScenarioRequest;
import fintech.bo.api.model.admintools.TriggerSchedulerRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

public interface AdminToolsApiClient {

    @GET("api/bo/admin-tools/list-actions")
    Call<ListAdminActionsResponse> listAdminActions();

    @POST("api/bo/admin-tools/execute-action")
    Call<IdResponse> executeAction(@Body ExecuteAdminActionRequest request);

    @GET("api/bo/admin-tools/list-demo-scenarios")
    Call<List<ScenarioInfo>> listDemoScenarios();

    @POST("api/bo/admin-tools/run-demo-scenario")
    Call<Void> runDemoScenario(@Body RunDemoScenarioRequest request);
    @POST("api/bo/admin-tools/trigger-scheduler")
    Call<Void> triggerScheduler(@Body TriggerSchedulerRequest request);

}
