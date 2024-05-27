package fintech.bo.api.client;

import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.client.IdsRequest;
import fintech.bo.api.model.loan.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoanApiClient {

    @POST("api/bo/loan/void")
    Call<Void> voidLoan(@Body VoidLoanRequest request);

    @POST("api/bo/loan/break")
    Call<Void> breakLoan(@Body BreakLoanRequest request);

    @POST("api/bo/loan/un-break")
    Call<Void> unBreakLoan(@Body IdRequest request);

    @POST("api/bo/loan/close-paid-loan")
    Call<Void> closePaidLoan(@Body IdRequest idRequest);

    @POST("api/bo/loan/write-off")
    Call<Void> writeOff(@Body WriteOffLoanAmountRequest request);

    @POST("/api/bo/loan/use-overpayment")
    Call<Void> useOverpayment(@Body UseOverpaymentRequest request);

    @POST("/api/bo/loan/extension-prices")
    Call<GetExtensionPricesResponse> getExtensionPrices(@Body GetExtensionPricesRequest request);

    @POST("/api/bo/loan/agreements/export")
    Call<CloudFileResponse> exportAgreements(@Body IdsRequest request);
}
