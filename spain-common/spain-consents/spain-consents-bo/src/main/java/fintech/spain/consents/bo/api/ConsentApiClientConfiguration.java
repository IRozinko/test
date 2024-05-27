package fintech.spain.consents.bo.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;

@Configuration
public class ConsentApiClientConfiguration {

    @Bean
    public ConsentApiClient consentApiClient(Retrofit retrofit) {
        return retrofit.create(ConsentApiClient.class);
    }

}
