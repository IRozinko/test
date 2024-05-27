package fintech.spain.alfa.product.presto.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import fintech.spain.alfa.product.presto.api.LineOfCreditCrossApiClient;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Configuration
public class PrestoApiClients {

    public static final String LINE_OF_CREDIT_API_NAME = "line-of-credit-cross-api";

    @Value("${presto.apiKey:fake}")
    private String apiKey;
    @Value("${presto.url:http://localhost:8080}")
    private String baseUrl;

    @Bean(LINE_OF_CREDIT_API_NAME)
    public LineOfCreditCrossApiClient lineOfCreditCrossApiClient() {
        return retrofit().create(LineOfCreditCrossApiClient.class);
    }

    private Retrofit retrofit() {
        return new Retrofit.Builder().baseUrl(baseUrl).client(okHttpClient())
            .addConverterFactory(gsonConverterFactory()).build();
    }

    private OkHttpClient okHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return new OkHttpClient.Builder()
            .addInterceptor(authenticationInterceptor())
            .addInterceptor(loggingInterceptor)
            .readTimeout(300, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();
    }

    private Interceptor authenticationInterceptor() {
        return chain -> chain.proceed(chain.request().newBuilder().addHeader("Authorization", apiKey).build());
    }

    private GsonConverterFactory gsonConverterFactory() {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (date, type, context) -> context.serialize(DateTimeFormatter.ISO_DATE_TIME.format(date)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, context) -> LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_DATE_TIME))
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, type, context) -> context.serialize(DateTimeFormatter.ISO_DATE.format(date)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, context) -> LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_DATE))
            .create();
        return GsonConverterFactory.create(gson);
    }
}
