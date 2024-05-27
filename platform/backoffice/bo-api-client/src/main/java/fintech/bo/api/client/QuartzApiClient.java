package fintech.bo.api.client;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface QuartzApiClient {

    @POST("api/bo/quartz/pause/{jobName}")
    Call<Void> pauseJob(@Path("jobName") String jobName);

    @POST("api/bo/quartz/resume/{jobName}")
    Call<Void> resumeJob(@Path("jobName") String jobName);

    @POST("api/bo/quartz/delete/{jobName}")
    Call<Void> deleteJob(@Path("jobName") String jobName);

    @POST("api/bo/quartz/pause")
    Call<Void> pauseScheduler();

    @POST("api/bo/quartz/resume")
    Call<Void> resumeScheduler();
}
