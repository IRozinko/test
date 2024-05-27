package fintech.bo.spain.alfa.api;

import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.IdResponse;
import fintech.spain.alfa.bo.model.CreateExtensionDiscountRequest;
import fintech.spain.alfa.bo.model.EditExtensionDiscountRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ExtensionDiscountApiClient {

    @POST("/api/bo/extension-discount")
    Call<IdResponse> createExtensionDiscount(@Body CreateExtensionDiscountRequest request);

    @POST("/api/bo/extension-discount/edit")
    Call<Void> editExtensionDiscount(@Body EditExtensionDiscountRequest request);

    @POST("/api/bo/extension-discount/delete")
    Call<Void> deleteExtensionDiscount(@Body IdRequest request);

    @POST("/api/bo/extension-discount/deactivate")
    Call<Void> deactivateExtensionDiscount(@Body IdRequest request);
}
