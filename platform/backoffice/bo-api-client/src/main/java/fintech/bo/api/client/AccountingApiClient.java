package fintech.bo.api.client;

import fintech.bo.api.model.accounting.AccountTrialBalance;
import fintech.bo.api.model.accounting.AccountTrialBalanceExportResponse;
import fintech.bo.api.model.accounting.AccountingReportQuery;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.List;

public interface AccountingApiClient {

    @POST("api/bo/accounting/trial-balance")
    Call<List<AccountTrialBalance>> getTrialBalance(@Body AccountingReportQuery query);

    @POST("api/bo/accounting/trial-balance-export")
    Call<AccountTrialBalanceExportResponse> export(@Body AccountingReportQuery query);
}
