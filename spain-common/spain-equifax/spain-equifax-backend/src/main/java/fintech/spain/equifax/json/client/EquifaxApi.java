package fintech.spain.equifax.json.client;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EquifaxApi {

    @POST("./")
    Call<ResponseBody> request(@Body EquifaxJsonRequest request);

}
