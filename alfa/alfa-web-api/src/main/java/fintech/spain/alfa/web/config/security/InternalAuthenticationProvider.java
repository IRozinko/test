package fintech.spain.alfa.web.config.security;

import fintech.spain.alfa.product.web.WebAuthorities;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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
public class InternalAuthenticationProvider implements AuthenticationProvider {

    @Value("${spain.api.internalApiKey:}")
    private String internalApiKey;

    @Override
    public boolean supports(Class<?> authentication) {
        return (InternalAuthentication.class.isAssignableFrom(authentication));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            InternalAuthentication internalAuthentication = (InternalAuthentication) authentication;
            UserDetails userDetails = userDetails(internalAuthentication.getApiKey());
            return new InternalAuthentication(userDetails, internalAuthentication.getApiKey());
        } catch (Exception e) {
            log.info("Invalid internal API authentication: {}", e.getMessage());
            return null;
        }
    }

    public UserDetails userDetails(String apiKey) throws UsernameNotFoundException {
        if (StringUtils.isBlank(internalApiKey)) {
            throw new UsernameNotFoundException("Internal API not enabled");
        }
        if (!StringUtils.equals(apiKey, internalApiKey)) {
            throw new UsernameNotFoundException("Invalid internal API key");
        }
        List<GrantedAuthority> grantedAuthorities = commaSeparatedStringToAuthorityList(WebAuthorities.INTERNAL);
        return new InternalApiUser("alfa", apiKey, grantedAuthorities);
    }


    public void setInternalApiKey(String internalApiKey) {
        this.internalApiKey = internalApiKey;
    }
}
