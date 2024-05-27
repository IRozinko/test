package fintech.spain.alfa.app.audit;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LoggingAndAuditMdcFilter extends GenericFilterBean {

    public static final String USER_ID = "userId";
    public static final String REQUEST_ID = "requestId";
    public static final String REQUEST_URI = "requestUri";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MDC.put(REQUEST_ID, UUID.randomUUID().toString());
        String requestURI = httpRequest.getRequestURI();
        MDC.put(REQUEST_URI, requestURI);
        if (authentication != null) {
            String name = authentication.getName();
            MDC.put(USER_ID, name);
        }
        try {
            chain.doFilter(request, response);
        } finally {
            if (authentication != null) {
                MDC.remove(USER_ID);
                MDC.remove(REQUEST_ID);
                MDC.remove(REQUEST_URI);
            }
        }
    }
}
