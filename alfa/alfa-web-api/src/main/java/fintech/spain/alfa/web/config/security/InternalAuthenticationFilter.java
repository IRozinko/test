package fintech.spain.alfa.web.config.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class InternalAuthenticationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null && context.getAuthentication().isAuthenticated()) {
            throw new IllegalStateException("API should be stateless and no previous authentication should exist");
        }
        String apiKey = ((HttpServletRequest) request).getHeader("Authorization");
        if (isNotBlank(apiKey)) {
            context.setAuthentication(new InternalAuthentication(apiKey));
        }
        chain.doFilter(request, response);
    }

}
