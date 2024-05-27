package fintech.bo.api.client;

import fintech.bo.api.model.product.UpdateProductSettingsRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ProductsApiClient {

    @POST("api/bo/products/update-settings")
    Call<Void> updateSettings(@Body UpdateProductSettingsRequest request);

}
