package fintech.bo.spain.asnef.api;

import fintech.bo.spain.asnef.model.ExportAsnefFileRequest;
import fintech.bo.spain.asnef.model.GenerateAsnefFileRequest;
import fintech.bo.spain.asnef.model.ImportAsnefFileRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AsnefApiClient {

    @POST("api/bo/asnef/generate")
    Call<Void> generateAsnefFile(@Body GenerateAsnefFileRequest request);

    @POST("api/bo/asnef/export")
    Call<Void> exportAsnefFile(@Body ExportAsnefFileRequest request);

    @POST("api/bo/asnef/import")
    Call<Void> importAsnefFile(@Body ImportAsnefFileRequest request);

    @DELETE("api/bo/asnef/{logId}")
    Call<Void> deleteAsnefFile(@Path("logId") Long logId);
}
