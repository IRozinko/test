package fintech.bo.spain.alfa.api;

import fintech.bo.api.model.client.PhoneContactRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PhoneContactApiClient {

    @POST("/api/bo/phone-contact/{id}/make-primary")
    Call<Void> makePrimary(@Path("id") long id);

    @POST("/api/bo/phone-contact/{id}/toggle-active")
    Call<Void> toggleActive(@Path("id") long id);

    @POST("/api/bo/phone-contact/{id}/toggle-legal-consent")
    Call<Void> toggleLegalConsent(@Path("id") long id);

    @POST("/api/bo/phone-contact")
    Call<Void> create(@Body PhoneContactRequest request);

    @POST("/api/bo/phone-contact/{id}")
    Call<Void> update(@Path("id") long id, @Body PhoneContactRequest request);

}
