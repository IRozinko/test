package fintech.bo.api.client;

import fintech.bo.api.model.StringRequest;
import fintech.bo.api.model.cms.*;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CmsApiClient {

    @POST("/api/bo/cms/preview-notification")
    Call<RenderNotificationResponse> previewNotification(@Body RenderCmsItemRequest request);

    @POST("/api/bo/cms/render-notification")
    Call<RenderNotificationResponse> renderNotification(@Body StringRequest request);

    @POST("/api/bo/cms/get-notification")
    Call<GetNotificationResponse> getNotification(@Body GetNotificationRequest request);

    @POST("/api/bo/cms/save-item")
    Call<Void> saveItem(@Body UpdateCmsItemRequest request);

    @POST("/api/bo/cms/add-item")
    Call<Void> addItem(@Body AddCmsItemRequest request);

    @POST("/api/bo/cms/delete-item")
    Call<Void> deleteItem(@Body DeleteCmsItemRequest request);

    @POST("/api/bo/cms/render-pdf")
    Call<ResponseBody> renderPdf(@Body RenderCmsItemRequest request);

    @GET("/api/bo/cms/documentation")
    Call<CmsDocumentationResponse> getDocumentation();

    @POST("/api/bo/cms/add-locale")
    Call<Void> addNewLocale(@Body AddNewLocaleRequest request);

    @POST("/api/bo/cms/delete-locale")
    Call<Void> deleteLocale(@Body DeleteLocaleRequest request);

}
