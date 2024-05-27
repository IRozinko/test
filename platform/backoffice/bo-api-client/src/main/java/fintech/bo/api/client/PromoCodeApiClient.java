package fintech.bo.api.client;

import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.loan.CreatePromoCodeRequest;
import fintech.bo.api.model.loan.EditPromoCodeRequest;
import fintech.bo.api.model.loan.UpdatePromoCodeClientsRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PromoCodeApiClient {

    @POST("/api/bo/promo-codes")
    Call<IdResponse> createPromoCode(@Body CreatePromoCodeRequest request);

    @POST("/api/bo/promo-codes/edit")
    Call<Void> editPromoCode(@Body EditPromoCodeRequest request);

    @POST("/api/bo/promo-codes/delete")
    Call<Void> delete(@Body IdRequest request);

    @POST("/api/bo/promo-codes/activate")
    Call<Void> activate(@Body IdRequest request);

    @POST("/api/bo/promo-codes/deactivate")
    Call<Void> deactivate(@Body IdRequest request);

    @POST("/api/bo/promo-codes/update-clients")
    Call<Void> updateClients(@Body UpdatePromoCodeClientsRequest request);

}
