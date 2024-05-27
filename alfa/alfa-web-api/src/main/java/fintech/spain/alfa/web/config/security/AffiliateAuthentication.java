package fintech.spain.alfa.web.config.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class AffiliateAuthentication extends UsernamePasswordAuthenticationToken {

    public AffiliateAuthentication(String apiKey) {
        super(null, apiKey);
    }

    public AffiliateAuthentication(UserDetails principal, Object credentials) {
        super(principal, credentials, principal.getAuthorities());
    }

    public String getApiKey() {
        return (String) getCredentials();
    }
}
