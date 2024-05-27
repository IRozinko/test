package fintech.spain.alfa.web.config.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class WebJwtAuthentication extends UsernamePasswordAuthenticationToken {

	public WebJwtAuthentication(String token) {
		super(null, token);
	}

	public WebJwtAuthentication(UserDetails principal, Object credentials) {
		super(principal, credentials, principal.getAuthorities());
	}

	public String getToken() {
		return (String) getCredentials();
	}

}
