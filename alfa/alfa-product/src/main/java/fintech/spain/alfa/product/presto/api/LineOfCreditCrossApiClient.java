package fintech.spain.alfa.product.presto.api;

import fintech.crm.client.model.PrestoDormantsResponse;
import fintech.spain.platform.web.model.DormantsData;
import fintech.web.api.models.AmortizationPreviewResponse;
import fintech.web.api.models.ContractAgreementRequest;
import fintech.web.api.models.WithdrawalRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LineOfCreditCrossApiClient {

    @POST("/api/internal/withdrawal/amortization-preview")
    Call<AmortizationPreviewResponse> amortizationPreview(@Body WithdrawalRequest request);

    @POST("/api/internal/document/generate-agreement")
    Call<ResponseBody> generateAgreementDocument(@Body ContractAgreementRequest request);

    @POST("/api/internal/document/generate-standard-info")
    Call<ResponseBody> generateStandardInfoDocument();

    @POST("/api/internal/dormants")
    Call<PrestoDormantsResponse> sendClient(@Body DormantsData data);
}
