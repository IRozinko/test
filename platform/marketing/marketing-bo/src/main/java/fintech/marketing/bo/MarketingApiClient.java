package fintech.marketing.bo;

import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.StringResponse;
import fintech.bo.api.model.marketing.MarketingSettings;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

import static fintech.marketing.bo.EditMarketingCampaignDialog.MarketingAudienceSettings;

public interface MarketingApiClient {

    @POST("/api/bo/spain/marketing/save-marketing-campaign")
    Call<Void> saveMarketingCampaign(@Body MultipartBody params);

    @POST("/api/bo/spain/marketing/resend-marketing-campaign")
    Call<Void> resendMarketingCampaign(@Body MultipartBody params);

    @POST("/api/bo/spain/marketing/save-marketing-template")
    Call<Void> saveMarketingTemplate(@Body MultipartBody params);

    @POST("/api/bo/spain/marketing/campaign-preview")
    Call<StringResponse> campaignPreview(@Body MultipartBody params);

    @FormUrlEncoded
    @POST("/api/bo/spain/marketing/sms-preview")
    Call<StringResponse> smsPreview(@Field("promoCodeId") Long promoCodeId, @Field("sms") String sms);

    @POST("/api/bo/spain/marketing/template-preview")
    Call<StringResponse> templatePreview(@Body IdRequest request);

    @POST("/api/bo/spain/marketing/audience-preview")
    Call<IdResponse> exportAudiencePreview(@Body MarketingAudienceSettings settings);

    @POST("/api/bo/spain/marketing/toggle-campaign-status")
    Call<Void> toggleCampaignStatus(@Body IdRequest idRequest);

    @GET("/api/bo/spain/marketing/documentation")
    Call<StringResponse> getDocumentation();

    @GET("/api/bo/spain/marketing/settings")
    Call<MarketingSettings> getSettings();

    @POST("/api/bo/spain/marketing/settings")
    Call<Void> saveSettings(@Body MarketingSettings settings);

}
