package fintech.spain.alfa.web.config.security;

import fintech.affiliate.AffiliateService;
import fintech.affiliate.model.AffiliatePartner;
import fintech.spain.alfa.product.web.WebAuthorities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AffiliateAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AffiliateService affiliateService;

    @Override
    public boolean supports(Class<?> authentication) {
        return (AffiliateAuthentication.class.isAssignableFrom(authentication));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            AffiliateAuthentication affiliateAuthentication = (AffiliateAuthentication) authentication;
            UserDetails userDetails = userDetails(affiliateAuthentication.getApiKey());
            return new AffiliateAuthentication(userDetails, affiliateAuthentication.getApiKey());
        } catch (Exception e) {
            log.info("Invalid affiliate authentication: {}", e.getMessage());
            return null;
        }
    }

    private UserDetails userDetails(String apiKey) throws UsernameNotFoundException {
        AffiliatePartner partner = affiliateService.findActivePartnerByApiKey(apiKey).orElseThrow(() -> new UsernameNotFoundException(String.format("Affiliate partner not found by api key %s", apiKey)));
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(WebAuthorities.AFFILIATE);
        return new AffiliateApiUser(partner.getName(), apiKey, grantedAuthorities);
    }
}
