package fintech.bo.api.client;

import fintech.bo.api.model.transaction.VoidTransactionRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TransactionApiClient {

    @POST("api/bo/transactions/void")
    Call<Void> voidTransaction(@Body VoidTransactionRequest request);

}
