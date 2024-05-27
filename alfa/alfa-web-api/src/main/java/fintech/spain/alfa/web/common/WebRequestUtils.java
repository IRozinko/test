package fintech.spain.alfa.web.common;

import com.google.common.base.MoreObjects;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public abstract class WebRequestUtils {

    public static String resolveIpAddress() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            String xForwardedFor = request.getHeader("X-FORWARDED-FOR");
            String remoteAddr = request.getRemoteAddr();
            String ip = MoreObjects.firstNonNull(xForwardedFor, remoteAddr);
            return StringUtils.split(ip, ",")[0];
        } else {
            try {
                InetAddress addr = InetAddress.getLocalHost();
                return addr.getHostAddress();
            } catch (UnknownHostException e) {
                log.warn("Could not resolve local ip address: {}", Throwables.getRootCause(e).getMessage());
                return null;
            }
        }
    }

    public static String resolveReferer() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            return request.getHeader("referer");
        }
        return null;
    }

}
