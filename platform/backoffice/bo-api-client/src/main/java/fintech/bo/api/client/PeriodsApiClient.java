package fintech.bo.api.client;

import fintech.bo.api.model.periods.ClosePeriodRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PeriodsApiClient {

    @POST("api/bo/periods/close")
    Call<Void> closePeriod(@Body ClosePeriodRequest request);

}
