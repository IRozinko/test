package fintech.ekomi.config;

import com.google.common.cache.CacheBuilder;
import fintech.ekomi.EKomiService;
import fintech.ekomi.api.EKomiApiClient;
import fintech.ekomi.impl.EKomiServiceBean;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class EKomiConfig {

    public static final String EKOMI_SNAPSHOT_CACHE_NAME = "EKOMI_SNAPSHOT";

    @Autowired
    private EkomiApiProperties ekomiApi;

    @Autowired(required = false)
    private TestInterceptor testInterceptor;

    @Bean
    public CacheManager ekomiCacheManager() {
        GuavaCacheManager cacheManager = new GuavaCacheManager(EKOMI_SNAPSHOT_CACHE_NAME);
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(30, TimeUnit.MINUTES);
        cacheManager.setCacheBuilder(cacheBuilder);
        return cacheManager;
    }

    @Bean
    public EKomiApiClient eKomiApiClient() {
        return new Retrofit.Builder()
            .baseUrl(ekomiApi.getUrl())
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(EKomiApiClient.class);
    }

    @Bean
    public EKomiService eKomiService() {
        return new EKomiServiceBean(eKomiApiClient());
    }

    private OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .addInterceptor(createAddRequestParamsInterceptor());
        if (testInterceptor != null) {
            builder.addInterceptor(testInterceptor);
        }
        return builder.build();
    }

    private Interceptor createAddRequestParamsInterceptor() {
        return chain -> {
            Request originalRequest = chain.request();
            Request newRequest = originalRequest.newBuilder()
                .url(buildHttpUrl(originalRequest))
                .build();
            return chain.proceed(newRequest);
        };
    }

    private HttpUrl buildHttpUrl(Request original) {
        HttpUrl originalHttpUrl = original.url();
        return originalHttpUrl.newBuilder()
            .addQueryParameter("auth", getAuth())
            .addQueryParameter("type", ekomiApi.getType())
            .addQueryParameter("version", ekomiApi.getVersion())
            .build();
    }

    private String getAuth() {
        return Stream.of(ekomiApi.getId(), ekomiApi.getKey()).collect(Collectors.joining("|"));
    }

    private HttpLoggingInterceptor createLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return loggingInterceptor;
    }


}
