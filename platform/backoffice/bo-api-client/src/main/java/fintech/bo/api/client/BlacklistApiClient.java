package fintech.bo.api.client;

import fintech.bo.api.model.risk.blacklist.AddToBlacklistRequest;
import fintech.bo.api.model.risk.blacklist.RemoveFromBlacklistRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BlacklistApiClient {

    @POST("/api/bo/blacklist/add")
    Call<Long> addToBlacklist(@Body AddToBlacklistRequest request);

    @POST("/api/bo/blacklist/remove")
    Call<Void> removeFromBlacklist(@Body RemoveFromBlacklistRequest request);

}
