package fintech.bo.api.client;

import fintech.bo.api.model.strategy.CreateStrategyRequest;
import fintech.bo.api.model.strategy.UpdateStrategyRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface StrategyApiClient {

    @POST("/api/bo/strategy/create")
    Call<Void> createStrategy(@Body CreateStrategyRequest request);

    @POST("/api/bo/strategy/update")
    Call<Void> updateStrategy(@Body UpdateStrategyRequest request);
}
