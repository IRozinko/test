package fintech.bo.spain.alfa.api;

import fintech.bo.api.model.marketing.ChangeMarketingConsentRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MarketingConsentApiClient {

    @POST("/api/bo/marketing/update")
    Call<Void> update(@Body ChangeMarketingConsentRequest req);
}
