package fintech.bo.spain.alfa.api;

import fintech.spain.alfa.bo.model.UpdateLoanUpsellAmountRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoanApplicationApiClient {

    @POST("api/bo/loan-application/update-loan-upsell-amount")
    Call<Void> updateLoanUpsellAmount(@Body UpdateLoanUpsellAmountRequest request);
}
