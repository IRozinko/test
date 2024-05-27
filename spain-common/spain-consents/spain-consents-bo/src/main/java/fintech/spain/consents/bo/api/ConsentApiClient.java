package fintech.spain.consents.bo.api;

import fintech.spain.consents.bo.api.model.UpdateTermsRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ConsentApiClient {

    @POST("api/bo/consents/update-terms")
    Call<Void> updateTerms(@Body UpdateTermsRequest request);

}
