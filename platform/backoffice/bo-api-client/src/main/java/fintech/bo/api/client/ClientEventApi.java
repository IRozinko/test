package fintech.bo.api.client;


import fintech.bo.api.model.AddCommentRequest;
import fintech.bo.api.model.IdResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ClientEventApi {
    @POST("api/bo/client-events/save-comment")
    Call<IdResponse> addComment(@Body AddCommentRequest request);
}
