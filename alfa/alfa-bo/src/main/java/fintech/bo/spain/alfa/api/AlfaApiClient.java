package fintech.bo.spain.alfa.api;

import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.client.IdsRequest;
import fintech.bo.api.model.dc.ChangeCompanyRequest;
import fintech.bo.api.model.dc.DebtEditResponse;
import fintech.bo.api.model.dc.EditDebtRequest;
import fintech.bo.api.model.dc.RescheduleLoanRequest;
import fintech.bo.api.model.dc.ReschedulingPreviewRequest;
import fintech.bo.api.model.dc.ReschedulingPreviewResponse;
import fintech.bo.api.model.loan.CloudFileResponse;
import fintech.spain.alfa.bo.model.*;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AlfaApiClient {

    @POST("api/bo/spain/alfa/send-offer-sms")
    Call<Void> sendOfferSms(@Body SendCmsNotificationRequest request);

    @POST("api/bo/spain/alfa/update-client-data")
    Call<Void> updateClientData(@Body UpdateClientDataRequest request);

    @POST("api/bo/spain/alfa/save-identification-document")
    Call<Void> saveIdentificationDocument(@Body SaveIdentificationDocumentRequest request);

    @POST("api/bo/spain/alfa/save-transaction-category")
    Call<Void> saveTransactionCategory(@Body SaveTransactionCategoryRequest request);

    @POST("api/bo/spain/alfa/document-check-update")
    Call<Void> documentCheckUpdate(@Body DocumentCheckUpdateRequest request);

    @POST("api/bo/spain/alfa/client-web-login")
    Call<ClientWebLoginResponse> clientWebLogin(@Body ClientWebLoginRequest request);

    @POST("api/bo/spain/alfa/renounce-loan")
    Call<Void> renounceLoan(@Body RenounceLoanRequest request);

    @POST("api/bo/spain/alfa/reschedule-loan")
    Call<Void> rescheduleLoan(@Body RescheduleLoanRequest request);

    @POST("api/bo/spain/alfa/break-rescheduled-loan")
    Call<Void> breakRescheduledLoan(@Body IdRequest request);

    @POST("api/bo/spain/alfa/calculate-prepayment")
    Call<CalculatePrepaymentResponse> calculatePrepayment(@Body CalculatePrepaymentRequest request);

    @POST("api/bo/spain/alfa/calculate-penalty")
    Call<CalculatePenaltyResponse> calculatePenalty(@Body CalculatePenaltyRequest request);

    @POST("api/bo/spain/alfa/viventor/send-loan")
    Call<Void> sendLoanToViventor(@Body IdRequest request);

    @POST("api/bo/spain/alfa/viventor/close-loan")
    Call<Void> closeViventorLoan(@Body IdRequest request);

    @POST("/api/bo/spain/alfa/viventor/sync-loan")
    Call<Void> syncViventorLoan(@Body IdRequest request);

    @POST("api/bo/spain/alfa/generate-penalties")
    Call<Void> generatePenalty(@Body IdRequest request);

    @POST("api/bo/spain/alfa/soft-delete-client")
    Call<Void> softDeleteClient(@Body IdRequest request);

    @POST("api/bo/spain/alfa/hard-delete-client")
    Call<Void> hardDeleteClient(@Body IdRequest request);

    @POST("/api/bo/spain/alfa/retry-application")
    Call<Void> retryApplication(@Body RetryLoanApplicationRequest request);

    @POST("api/bo/spain/alfa/dc-rescheduling-preview")
    Call<ReschedulingPreviewResponse> generateReschedulingPreview(@Body ReschedulingPreviewRequest request);

    @POST("api/bo/spain/alfa/add-client-address")
    Call<Void> addClientAddress(@Body AddClientAddressRequest request);

    @POST("api/bo/spain/alfa/dc/externalize-debt")
    Call<DebtEditResponse> externalizeDebt(@Body ChangeCompanyRequest request);

    @POST("api/bo/spain/alfa/dc/sell-debt")
    Call<DebtEditResponse> sellDebt(@Body ChangeCompanyRequest request);

    @POST("api/bo/spain/alfa/dc/recover-debt")
    Call<DebtEditResponse> recoverExternalDebt(@Body EditDebtRequest request);

    @POST("api/bo/spain/alfa/dc/repurchase-debt")
    Call<DebtEditResponse> repurchaseDebt(@Body EditDebtRequest request);

    @POST("api/bo/spain/alfa/dc/reassign-debt")
    Call<DebtEditResponse> reassignDebt(@Body EditDebtRequest request);

    @POST("/api/bo/spain/alfa/preview-strategy-cms-item")
    Call<ResponseBody> renderStrategyCmsItem(@Body PreviewCalculationStrategyCmsItemRequest request);

    @POST("/api/bo/spain/alfa/blacklist-client")
    Call<Void> blacklistClient(@Body BlacklistClientRequest request);

    @POST("/api/bo/spain/alfa/unblacklist-client")
    Call<Void> unBlacklistClient(@Body IdRequest request);

    @POST("api/bo/alfa/export-debts")
    Call<CloudFileResponse> exportDebts(@Body IdsRequest request);
}
