package fintech.bo.components.webitel.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;

@Configuration
public class WebitelApiClientConfiguration {

    @Autowired
    private Retrofit retrofit;

    @Bean
    WebitelApiClient webitelApiClient() {
        return retrofit.create(WebitelApiClient.class);
    }
}
