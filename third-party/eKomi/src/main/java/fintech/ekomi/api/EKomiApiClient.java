package fintech.ekomi.api;

import fintech.ekomi.api.json.Snapshot;
import retrofit2.Call;
import retrofit2.http.GET;

public interface EKomiApiClient {

    @GET("getSnapshot")
    Call<Snapshot> getSnapshot();

}
