package fintech.bo.spain.asnef.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;

@Configuration
public class AsnefApiClientConfiguration {

    @Autowired
    private Retrofit retrofit;

    @Bean
    AsnefApiClient asnefApiClient() {
        return retrofit.create(AsnefApiClient.class);
    }
}
