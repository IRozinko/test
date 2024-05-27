package fintech.bo.api.client;

import fintech.bo.api.model.attachement.SaveAttachmentRequest;
import fintech.bo.api.model.attachement.UpdateStatusRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.List;

public interface AttachmentApiClient {
    @POST("/api/bo/attachments/save")
    Call<Void> saveAttachment(@Body SaveAttachmentRequest request);

    @POST("/api/bo/attachments/status")
    Call<Void> updateStatuses(@Body List<UpdateStatusRequest> request);
}
