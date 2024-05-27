package fintech.bo.api.client;

import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.client.IdsRequest;
import fintech.bo.api.model.institution.UpdateInstitutionRequest;
import fintech.bo.api.model.loan.CloudFileResponse;
import fintech.bo.api.model.payments.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.List;

public interface PaymentApiClient {

    @POST("api/bo/payments/add-other-transaction")
    Call<IdResponse> addOtherTransaction(@Body AddOtherTransactionRequest request);

    @POST("api/bo/payments/add-repayment-transaction")
    Call<List<IdResponse>> addRepaymentTransaction(@Body AddRepaymentTransactionRequest request);

    @POST("api/bo/payments/add-overpayment-transaction")
    Call<IdResponse> addOverpaymentTransaction(@Body OperateOverpaymentTransactionRequest request);

    @POST("api/bo/payments/add-refund-overpayment-transaction")
    Call<IdResponse> addRefundOverpaymentTransaction(@Body OperateOverpaymentTransactionRequest request);

    @POST("api/bo/payments/add-fee-transaction")
    Call<IdResponse> addFeeTransaction(@Body AddFeeTransactionRequest request);

    @POST("api/bo/payments/add-extension-transaction")
    Call<IdResponse> addExtensionTransaction(@Body AddExtensionTransactionRequest request);

    @POST("api/bo/payments/void")
    Call<Void> voidPayment(@Body VoidPaymentRequest request);

    @POST("api/bo/payments/unvoid")
    Call<Void> unvoidPayment(@Body UnvoidPaymentRequest request);

    @POST("api/bo/payments/add-disbursement-settled-transaction")
    Call<IdResponse> addDisbursementSettledTransaction(@Body AddDisbursementSettledTransactionRequest request);

    @POST("api/bo/payments/update-institution")
    Call<Void> updateInstitution(@Body UpdateInstitutionRequest request);

    @POST("api/bo/payments/export-bank-statements")
    Call<CloudFileResponse> exportBankStatements(@Body IdsRequest loans);
}
