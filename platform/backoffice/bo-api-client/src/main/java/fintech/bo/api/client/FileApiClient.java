package fintech.bo.api.client;

import fintech.bo.api.model.DownloadCloudFileRequest;
import fintech.bo.api.model.IdResponse;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface FileApiClient {

    @POST("api/bo/files/download")
    Call<ResponseBody> download(@Body DownloadCloudFileRequest request);
    
    @Multipart
    @POST("api/bo/files/upload")
    Call<IdResponse> upload(@Part("file") RequestBody multiPart, @Query("directory") String directory);
    
}
