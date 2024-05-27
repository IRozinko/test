package fintech.bo.api.client;

import fintech.bo.api.model.calendar.BusinessDaysRequest;
import fintech.bo.api.model.calendar.BusinessDaysResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CalendarApiClient {

    @POST("api/bo/calendar/business-time/resolve")
    Call<BusinessDaysResponse> resolveBusinessTime(@Body BusinessDaysRequest request);
}
