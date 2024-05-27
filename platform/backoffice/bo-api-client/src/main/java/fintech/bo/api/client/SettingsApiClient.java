package fintech.bo.api.client;

import fintech.bo.api.model.UpdatePropertyRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SettingsApiClient {

    @POST("api/bo/settings")
    Call<Void> update(@Body UpdatePropertyRequest request);
}
