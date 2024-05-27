package fintech.bo.components.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import fintech.bo.components.security.LoginService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class RetrofitConfig {

    public static final String JWT_COOKIE_NAME = "bo_jwt";

    @Value("${bo.api.baseUrl:http://localhost:8080}")
    private String baseUrl;

    @Bean
    @ConditionalOnMissingBean(Retrofit.class)
    public Retrofit retrofit() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(authenticationInterceptor())
            .addInterceptor(loggingInterceptor)
            .readTimeout(300, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (date, type, context) -> context.serialize(DateTimeFormatter.ISO_DATE_TIME.format(date)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, context) -> LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_DATE_TIME))
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, type, context) -> context.serialize(DateTimeFormatter.ISO_DATE.format(date)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, context) -> LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_DATE))
            .create();
        GsonConverterFactory converterFactory = GsonConverterFactory.create(gson);
        return new Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient)
            .addConverterFactory(converterFactory).build();
    }

    private Interceptor authenticationInterceptor() {
        return chain -> {
            Request.Builder builder = chain.request().newBuilder();
            if (LoginService.isLoggedIn()) {
                String cookie = String.format("%s=%s", JWT_COOKIE_NAME, LoginService.getLoginData().getJwtToken());
                builder.addHeader("Cookie", cookie);
            }
            Request request = builder.build();
            return chain.proceed(request);
        };
    }

}
