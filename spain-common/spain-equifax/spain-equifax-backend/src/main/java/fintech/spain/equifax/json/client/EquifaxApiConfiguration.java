package fintech.spain.equifax.json.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import fintech.retrofit.BasicAuthInterceptor;
import fintech.retrofit.HeaderInterceptor;
import fintech.spain.equifax.json.EquifaxJsonProviderBean;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnProperty(name = "spain.equifax.provider", havingValue = EquifaxJsonProviderBean.NAME)
public class EquifaxApiConfiguration {

    private static final String DPT_ORCHESTRATION_CODE_HEADER = "dptOrchestrationCode";

    private final String userId;
    private final String password;
    private final String orchestrationCode;
    private final String baseUrl;

    private final ObjectMapper objectMapper;

    public EquifaxApiConfiguration(@Value("${spain.equifax.userId:}") String userId,
                                   @Value("${spain.equifax.password:}") String password,
                                   @Value("${spain.equifax.orchestrationCode:}") String orchestrationCode,
                                   @Value("${spain.equifax.url:}") String baseUrl,
                                   ObjectMapper objectMapper) {
        this.userId = userId;
        this.password = password;
        this.orchestrationCode = orchestrationCode;
        this.baseUrl = baseUrl;
        this.objectMapper = objectMapper;
    }

    @Bean
    @Qualifier("equifaxRetrofit")
    Retrofit retrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(new BasicAuthInterceptor(userId, password))
            .addInterceptor(new HeaderInterceptor(DPT_ORCHESTRATION_CODE_HEADER, orchestrationCode))
            .readTimeout(300, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

        return new Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .build();
    }

    @Bean
    EquifaxApi equifaxApi(@Qualifier("equifaxRetrofit") Retrofit retrofit) {
        return retrofit.create(EquifaxApi.class);
    }


}
