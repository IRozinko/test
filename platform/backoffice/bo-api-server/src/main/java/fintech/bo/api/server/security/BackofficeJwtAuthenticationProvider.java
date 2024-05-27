package fintech.bo.api.server.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


@Slf4j
public class BackofficeJwtAuthenticationProvider implements AuthenticationProvider {

    private final BackofficeJwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;

    public BackofficeJwtAuthenticationProvider(BackofficeJwtTokenService jwtTokenService, UserDetailsService userDetailsService) {
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public boolean supports(Class<?> authentication) {
        return (BackofficeJwtAuthentication.class.isAssignableFrom(authentication));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            BackofficeJwtAuthentication jwtAuthentication = (BackofficeJwtAuthentication) authentication;
            String username = jwtTokenService.getUsername(jwtAuthentication.getToken());
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            detailsChecker.check(userDetails);
            return new BackofficeJwtAuthentication(userDetails, jwtAuthentication.getToken());
        } catch (Exception e) {
            log.info("Invalid JWT token: {}", e.getMessage());
            return null;
        }
    }


}
