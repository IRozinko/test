package fintech.marketing.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;

@Configuration
public class MarketingApiClients {

    @Autowired
    private Retrofit retrofit;

    @Bean
    MarketingApiClient marketingTemplatesApiClient() {
        return retrofit.create(MarketingApiClient.class);
    }


}
