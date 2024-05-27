package fintech.fintechmarket.config;

import org.springframework.http.client.ClientHttpRequest;
import org.springframework.security.oauth2.client.DefaultOAuth2RequestAuthenticator;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

public class FintechMarketAuthenticator extends DefaultOAuth2RequestAuthenticator {

    @Override
    public void authenticate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext clientContext, ClientHttpRequest request) {
        OAuth2AccessToken accessToken = clientContext.getAccessToken();
        if (accessToken == null) {
            throw new AccessTokenRequiredException(resource);
        }
        // By default DefaultOAuth2RequestAuthenticator get token type from OAuth2AccessToken,
        // but there type starts with a low first letter: `bearer`,
        // but authentication server supports type only with capitalized first letter: `Bearer`
        String tokenType = OAuth2AccessToken.BEARER_TYPE;
        request.getHeaders().set("Authorization", String.format("%s %s", tokenType, accessToken.getValue()));
    }
}
