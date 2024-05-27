package fintech.bo.spain.alfa.api;

import fintech.bo.api.model.address.AddAddressCatalogEntryRequest;
import fintech.bo.api.model.address.DeleteAddressCatalogEntryRequest;
import fintech.bo.api.model.address.EditAddressCatalogEntryRequest;
import fintech.bo.api.model.address.ExportAddressCatalogResponse;
import fintech.bo.api.model.address.ImportAddressCatalogRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AddressApiClient {

    @POST("api/bo/address/add")
    Call<Void> addAddress(@Body AddAddressCatalogEntryRequest request);

    @POST("api/bo/address/edit")
    Call<Void> editAddress(@Body EditAddressCatalogEntryRequest request);

    @POST("api/bo/address/delete")
    Call<Void> deleteAddress(@Body DeleteAddressCatalogEntryRequest request);

    @POST("api/bo/address/export")
    Call<ExportAddressCatalogResponse> exportAddressCatalog();

    @POST("api/bo/address/import")
    Call<Void> importAddressCatalog(@Body ImportAddressCatalogRequest request);
}
