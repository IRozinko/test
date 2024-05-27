package fintech.bo.api.client;

import fintech.bo.api.model.risk.checklist.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;

public interface ChecklistApiClient {

    @POST("api/bo/checklist/edit")
    Call<Void> updateChecklist(@Body UpdateChecklistRequest request);

    @POST("api/bo/checklist/delete")
    Call<Void> deleteChecklist(@Body DeleteChecklistRequest request);

    @POST("api/bo/checklist/add")
    Call<Void> addChecklist(@Body AddChecklistRequest request);

    @POST("api/bo/checklist/export")
    Call<ExportChecklistResponse> export(@Body ExportChecklistRequest request);

    @POST("api/bo/checklist/import")
    Call<Void> importChecklist(@Body ImportChecklistRequest request);
}
