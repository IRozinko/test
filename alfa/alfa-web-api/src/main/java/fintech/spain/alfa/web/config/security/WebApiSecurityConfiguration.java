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
@Order(200)
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebApiSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private WebJwtAuthenticationProvider authenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
            .antMatchers("/api/public/web/**", "/api/web/**")
            .and()
            .addFilterBefore(new WebJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(authenticationProvider)
            .authorizeRequests()
            .antMatchers("/api/public/web/**").permitAll()
            .antMatchers("/api/web/profile/loans/**", "/api/web/profile/personal-details").hasAnyAuthority(WebAuthorities.WEB_PAYMENT_ONLY, WebAuthorities.WEB_FULL, WebAuthorities.WEB_READ_ONLY)
            .antMatchers("/api/web/**").hasAnyAuthority(WebAuthorities.WEB_FULL, WebAuthorities.WEB_READ_ONLY)
            .anyRequest().denyAll()
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
