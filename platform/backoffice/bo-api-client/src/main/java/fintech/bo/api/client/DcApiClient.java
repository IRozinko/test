package fintech.bo.api.client;

import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.dc.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DcApiClient {

    @POST("api/bo/dc/log-debt-action")
    Call<IdResponse> logDebtAction(@Body LogDebtActionRequest request);

    @POST("api/bo/dc/save-agent")
    Call<IdResponse> saveAgent(@Body SaveAgentRequest request);

    @POST("api/bo/dc/add-agent-absence")
    Call<IdResponse> addAgentAbsence(@Body AddAgentAbsenceRequest request);

    @POST("api/bo/dc/remove-agent-absence")
    Call<Void> removeAgentAbsence(@Body RemoveAgentAbsenceRequest request);

    @POST("api/bo/dc/auto-assign-debt")
    Call<Void> autoAssignDebt(@Body AutoAssignDebtRequest request);

    @POST("api/bo/dc/save-settings")
    Call<Void> saveSettings(@Body SaveDcSettingsRequest request);
    @POST("api/bo/dc/import-debts")
    Call<ImportDebtResponse> importDebts(@Body ImportDebtRequest request);
    @POST("api/bo/dc/save-debt-status")
    Call<DebtEditResponse> saveDebtStatus(@Body SaveDebtStatusRequest request);
}
