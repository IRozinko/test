package fintech.bo.api.client;

import fintech.bo.api.model.IdRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DiscountApiClient {

    @POST("api/bo/discounts/apply-batch")
    Call<Void> apply(@Body IdRequest request);
}
