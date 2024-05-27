package fintech.spain.alfa.web.config.security;

import fintech.spain.alfa.product.web.WebAuthorities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Order(204)
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class AffiliateApiSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AffiliateAuthenticationProvider affiliateAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
            .antMatchers("/api/affiliate/**")
            .and()
            .addFilterBefore(new AffiliateAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(affiliateAuthenticationProvider)
            .authorizeRequests().anyRequest().hasAuthority(WebAuthorities.AFFILIATE)
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().csrf().disable()
            .headers()
            .contentTypeOptions().and()
            .xssProtection().and()
            .cacheControl().and()
            .httpStrictTransportSecurity();
    }
}
