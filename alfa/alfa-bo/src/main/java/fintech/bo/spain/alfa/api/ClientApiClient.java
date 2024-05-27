package fintech.bo.spain.alfa.api;

import fintech.bo.api.model.client.InitiateChangingBankAccountRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ClientApiClient {

    @POST("api/bo/client/initiate-changing-bank-account")
    Call<Void> initiateChangingBankAccount(@Body InitiateChangingBankAccountRequest request);
}
