package fintech.spain.alfa.web.config.security;

import fintech.crm.logins.EmailLogin;
import fintech.crm.logins.EmailLoginService;
import fintech.spain.alfa.product.web.WebJwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;


@Component
@Slf4j
public class WebJwtAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private WebJwtTokenService jwtTokenService;

    @Autowired
    private EmailLoginService emailLoginService;

    private final AccountStatusUserDetailsChecker detailsChecker = new AccountStatusUserDetailsChecker();

    @Override
    public boolean supports(Class<?> authentication) {
        return (WebJwtAuthentication.class.isAssignableFrom(authentication));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            WebJwtAuthentication webJwtAuthentication = (WebJwtAuthentication) authentication;
            Jws<Claims> jwt = jwtTokenService.parse(webJwtAuthentication.getToken());
            UserDetails userDetails = userDetails(jwt);
            detailsChecker.check(userDetails);
            return new WebJwtAuthentication(userDetails, webJwtAuthentication.getToken());
        } catch (Exception e) {
            log.info("Invalid JWT token: {}", e.getMessage());
            return null;
        }
    }

    public UserDetails userDetails(Jws<Claims> jwt) throws UsernameNotFoundException {
        String email = WebJwtTokenService.userName(jwt);
        String role = WebJwtTokenService.role(jwt);
        String auditUser = WebJwtTokenService.auditUser(jwt);
        EmailLogin login = emailLoginService.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format("Client not found by email %s", email)));
        List<GrantedAuthority> grantedAuthorities = commaSeparatedStringToAuthorityList(role);
        return new WebApiUser(login.getClientId(), login.getEmail(), auditUser, login.getPassword(), grantedAuthorities);
    }

}
