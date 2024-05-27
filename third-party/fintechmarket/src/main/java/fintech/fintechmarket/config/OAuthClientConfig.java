package fintech.fintechmarket.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty(name = "fintechmarket.mock", havingValue = "false")
public class OAuthClientConfig {

    @Bean
    @Qualifier("fintechMarketAuthDetails")
    @ConfigurationProperties("fintechmarket.client")
    protected ClientCredentialsResourceDetails oAuthDetails() {
        return new ClientCredentialsResourceDetails();
    }

    @Bean
    @Primary
    @Qualifier("fintechMarketRestTemplate")
    public RestTemplate restTemplate(@Qualifier("fintechMarketAuthDetails") OAuth2ProtectedResourceDetails details) {
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(details);
        restTemplate.setAuthenticator(new FintechMarketAuthenticator());
        restTemplate.setErrorHandler(new ConflictErrorHandler());
        return restTemplate;
    }

}
