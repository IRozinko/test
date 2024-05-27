package fintech.bo.api.server.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class BackofficeJwtAuthentication extends UsernamePasswordAuthenticationToken {

	public BackofficeJwtAuthentication(String token) {
		super(null, token);
	}

	public BackofficeJwtAuthentication(UserDetails principal, Object credentials) {
		super(principal, credentials, principal.getAuthorities());
	}

	public String getToken() {
		return (String) getCredentials();
	}

}
