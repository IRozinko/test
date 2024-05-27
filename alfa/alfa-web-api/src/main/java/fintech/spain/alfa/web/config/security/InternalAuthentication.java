package fintech.spain.alfa.web.config.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class InternalAuthentication extends UsernamePasswordAuthenticationToken {

	public InternalAuthentication(String apiKey) {
		super(null, apiKey);
	}

	public InternalAuthentication(UserDetails principal, Object credentials) {
		super(principal, credentials, principal.getAuthorities());
	}

	public String getApiKey() {
		return (String) getCredentials();
	}

}
