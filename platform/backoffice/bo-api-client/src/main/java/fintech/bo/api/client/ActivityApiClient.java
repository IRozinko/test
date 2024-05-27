package fintech.bo.api.client;

import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.activity.ActivityResponse;
import fintech.bo.api.model.activity.AddActivityRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ActivityApiClient {

    @POST("api/bo/activity/add-activity")
    Call<IdResponse> addActivity(@Body AddActivityRequest request);

    @GET("api/bo/activity/{clientId}")
    Call<List<ActivityResponse>> findActivities(@Path("clientId") long clientId,
                                                @Query("action") String action);
}
