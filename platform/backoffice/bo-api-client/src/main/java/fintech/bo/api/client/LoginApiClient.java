package fintech.bo.api.client;

import fintech.bo.api.model.LoginRequest;
import fintech.bo.api.model.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApiClient {

    @POST("api/public/bo/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
