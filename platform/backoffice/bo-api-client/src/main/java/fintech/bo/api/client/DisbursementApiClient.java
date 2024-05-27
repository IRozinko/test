package fintech.bo.api.client;

import fintech.bo.api.model.disbursements.DisbursementExportResponse;
import fintech.bo.api.model.disbursements.ExportDisbursementsRequest;
import fintech.bo.api.model.disbursements.ExportSingleDisbursementRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DisbursementApiClient {

    @POST("api/bo/disbursements/export")
    Call<DisbursementExportResponse> export(@Body ExportDisbursementsRequest request);

    @POST("api/bo/disbursements/export-single")
    Call<DisbursementExportResponse> exportSingle(@Body ExportSingleDisbursementRequest request);

    @POST("api/bo/disbursements/{disbursementId}/void-single")
    Call<Void> voidSingle(@Path("disbursementId") Long disbursementId);

    @POST("api/bo/disbursements/{disbursementId}/retry-single")
    Call<Void> retrySingle(@Path("disbursementId") Long disbursementId);
}
