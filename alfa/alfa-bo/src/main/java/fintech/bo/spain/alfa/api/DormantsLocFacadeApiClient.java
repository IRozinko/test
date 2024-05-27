package fintech.bo.spain.alfa.api;

import fintech.spain.alfa.bo.model.SendCmsNotificationRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DormantsLocFacadeApiClient {

    @POST("api/bo/workflow/dormants/resendPreOfferEmail")
    Call<Void> resendPreOfferEmail(@Body SendCmsNotificationRequest request);

    @POST("api/bo/workflow/dormants/resendPreOfferSms")
    Call<Void> resendPreOfferSms(@Body SendCmsNotificationRequest request);

    @POST("api/bo/workflow/dormants/resendOfferEmail")
    Call<Void> resendOfferEmail(@Body SendCmsNotificationRequest request);

    @POST("api/bo/workflow/dormants/resendOfferSms")
    Call<Void> resendOfferSms(@Body SendCmsNotificationRequest request);

}
