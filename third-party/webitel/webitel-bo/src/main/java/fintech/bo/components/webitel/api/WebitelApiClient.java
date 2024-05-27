package fintech.bo.components.webitel.api;

import fintech.webitel.model.WebitelAuthToken;
import fintech.webitel.model.WebitelCallResult;
import fintech.webitel.model.WebitelLoginCommand;
import fintech.webitel.model.WebitelCallCommand;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WebitelApiClient {

    @POST("api/bo/webitel/login")
    Call<WebitelAuthToken> login(@Body WebitelLoginCommand request);

    @POST("api/bo/webitel/call")
    Call<WebitelCallResult> call(@Body WebitelCallCommand request);
}
