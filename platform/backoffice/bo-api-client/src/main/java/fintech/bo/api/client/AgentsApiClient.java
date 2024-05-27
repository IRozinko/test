package fintech.bo.api.client;

import fintech.bo.api.model.agents.DisableAgentRequest;
import fintech.bo.api.model.agents.UpdateAgentRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AgentsApiClient {
    @POST("api/bo/agents")
    Call<Void> saveOrUpdate(@Body UpdateAgentRequest request);

    @POST("api/bo/agents/disable")
    Call<Void> disable(@Body DisableAgentRequest request);
}
