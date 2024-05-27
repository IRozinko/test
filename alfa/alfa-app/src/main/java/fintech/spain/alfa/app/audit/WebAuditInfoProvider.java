package fintech.spain.alfa.app.audit;

import fintech.Validate;
import fintech.bo.api.server.security.BackofficeUser;
import fintech.db.AuditInfoProvider;
import fintech.spain.alfa.web.common.WebRequestUtils;
import fintech.spain.alfa.web.config.security.AffiliateApiUser;
import fintech.spain.alfa.web.config.security.AnonymousApiUser;
import fintech.spain.alfa.web.config.security.InternalApiUser;
import fintech.spain.alfa.web.config.security.WebApiUser;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
@Primary
@Component
public class WebAuditInfoProvider implements AuditInfoProvider, AuditorAware<String> {

    @Override
    public AuditInfo getInfo() {
        String requestId = MDC.get(LoggingAndAuditMdcFilter.REQUEST_ID);
        String requestUri = MDC.get(LoggingAndAuditMdcFilter.REQUEST_URI);
        String userName = resolveUsername();
        String ipAddress = WebRequestUtils.resolveIpAddress();
        String referer = WebRequestUtils.resolveReferer();
        return new AuditInfo(userName, requestId, requestUri, ipAddress, referer);
    }

    @Override
    public String getCurrentAuditor() {
        return resolveUsername();
    }

    private String resolveUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            Validate.notNull(principal, "Null principal");
            if (principal instanceof WebApiUser) {
                WebApiUser webApiUser = (WebApiUser) principal;
                return webApiUser.getAuditUser();
            } else if (principal instanceof AffiliateApiUser) {
                return "affiliate:" + authentication.getName();
            } else if (principal instanceof BackofficeUser) {
                return "bo:" + authentication.getName();
            } else if (principal instanceof InternalApiUser) {
                return "internal:" + authentication.getName();
            } else if (principal instanceof AnonymousApiUser) {
                return "anonymous:" + authentication.getName();
            } else if (principal instanceof String) {
                return systemUsername();
            } else {
                log.error("Unknown authentication type: {}, principal: {}", authentication, authentication.getPrincipal());
                return systemUsername();
            }
        } else {
            return systemUsername();
        }
    }

    private String systemUsername() {
        return RequestContextHolder.getRequestAttributes() == null ? "system:backend" : "system:web";
    }

}
