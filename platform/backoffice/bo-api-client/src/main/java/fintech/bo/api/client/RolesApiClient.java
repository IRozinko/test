package fintech.bo.api.client;

import fintech.bo.api.model.permissions.SaveRoleRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RolesApiClient {

    @POST("api/bo/roles")
    Call<Void> update(@Body SaveRoleRequest request);

    @DELETE("api/bo/roles/{name}")
    Call<Void> delete(@Path("name") String name);
}
