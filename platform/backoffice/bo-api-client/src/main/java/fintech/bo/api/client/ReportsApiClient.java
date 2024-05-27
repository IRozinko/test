package fintech.bo.api.client;

import fintech.bo.api.model.reports.ReportFileResponse;
import fintech.bo.api.model.reports.ReportRequest;
import fintech.bo.api.model.reports.ReportResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

public interface ReportsApiClient {

    @GET("api/bo/reports")
    Call<List<ReportResponse>> getReports();

    @POST("api/bo/reports")
    Call<ReportFileResponse> generateReport(@Body ReportRequest query);

}
