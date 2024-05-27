package fintech.bo.api.server.security;

import fintech.Validate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class BackofficeJwtAuthenticationFilter extends GenericFilterBean {

    private final String tokenCookieName;

    public BackofficeJwtAuthenticationFilter(String tokenCookieName) {
        Validate.notBlank(tokenCookieName);
        this.tokenCookieName = tokenCookieName;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null && context.getAuthentication().isAuthenticated()) {
            throw new IllegalStateException("API should be stateless and no previous authentication should exist");
        }
        String authToken = ((HttpServletRequest) request).getHeader("Authorization");
        if (isNotBlank(authToken)) {
            context.setAuthentication(new BackofficeJwtAuthentication(authToken));
        } else {
            findJWTCookie(((HttpServletRequest) request))
                .ifPresent(jwtCookie -> context.setAuthentication(new BackofficeJwtAuthentication(jwtCookie.getValue())));
        }
        chain.doFilter(request, response);
    }

    private Optional<Cookie> findJWTCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        } else {
            return Arrays.stream(request.getCookies()).filter(c -> tokenCookieName.equals(c.getName())).findFirst();
        }
    }
}
