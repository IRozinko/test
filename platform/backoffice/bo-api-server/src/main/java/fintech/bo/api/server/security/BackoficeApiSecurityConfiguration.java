package fintech.bo.api.server.security;

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
@Order(300)
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class BackoficeApiSecurityConfiguration extends WebSecurityConfigurerAdapter {

    public static final String TOKEN_COOKIE_NAME = "bo_jwt";

    public static final String AUTHORITY_BACKOFFICE = "BACKOFFICE";

    @Autowired
    private BackofficeUserDetailsService backofficeUserDetailsService;

    @Autowired
    private BackofficeJwtTokenService jwtTokenService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
            .antMatchers("/api/public/bo/**", "/api/bo/**")
            .and()
            .addFilterBefore(new BackofficeJwtAuthenticationFilter(TOKEN_COOKIE_NAME), UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(new BackofficeJwtAuthenticationProvider(jwtTokenService, backofficeUserDetailsService))
            .authorizeRequests()
            .antMatchers("/api/public/bo/**").permitAll()
            .antMatchers("/api/bo/**").hasAuthority(AUTHORITY_BACKOFFICE)
            .anyRequest().denyAll()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().csrf().disable()
            .headers()
            .contentTypeOptions().and()
            .xssProtection().and()
            .cacheControl().and()
            .httpStrictTransportSecurity()
            .and().frameOptions().sameOrigin();
    }
}
