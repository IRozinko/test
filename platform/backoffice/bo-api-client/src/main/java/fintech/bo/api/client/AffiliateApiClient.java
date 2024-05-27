package fintech.bo.api.client;

import fintech.bo.api.model.affiliate.SavePartnerRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AffiliateApiClient {

    @POST("api/bo/affiliate/save-partner")
    Call<Void> savePartner(@Body SavePartnerRequest query);

}
