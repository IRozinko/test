package fintech.bo.spain.alfa.api;

import fintech.spain.alfa.bo.model.UploadLocClientsRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LocBatchApiClient {

    @POST("/api/bo/loc-batch/{batchNumber}/trigger")
    Call<Void> trigger(@Path("batchNumber") Long batchNumber);

    @POST("/api/bo/loc-batch/upload")
    Call<Void> upload(@Body UploadLocClientsRequest request);

    @POST("/api/bo/loc-batch/trigger-start-workflows")
    Call<Void> triggerStartWorkflows();
}
