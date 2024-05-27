package fintech.spain.inglobaly.impl;

import com.global.info.ws.soap.AccesoWebService;
import com.global.info.ws.soap.DomiciliosWebService;
import com.global.info.ws.soap.Exception_Exception;
import com.global.info.ws.soap.ListadoDomiciliosTelefonos;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component(InglobalyProviderBean.NAME)
public class InglobalyProviderBean implements InglobalyProvider {

    public static final String NAME = "spain-inglobaly-provider";

    public static final String NO_ACCESS_ERROR_MESSAGE = "El usuario no tiene permisos para este servicio";

    @Value("${spain.inglobaly.debug:false}")
    private boolean debugEnabled = true;

    @Value("${spain.inglobaly.login:test}")
    private String login = "test";

    @Value("${spain.inglobaly.password:password}")
    private String password = "password";

    @Value("${spain.inglobaly.accesoServiceUrl:https://ws.inglobaly.com:443/AccesoWebService}")
    private String accesoServiceUrl;

    @Value("${spain.inglobaly.domiciliosServiceUrl:https://ws.inglobaly.com:443/DomiciliosWebService}")
    private String domiciliosServiceUrl;


    private Supplier<AccesoWebService> accesoWebService = Suppliers.memoize(this::buildAccesoService);
    private Supplier<DomiciliosWebService> domiciliosWebService = Suppliers.memoize(this::buildDomiciliosService);
    private String token;

    private final RetryPolicy retryPolicy = new RetryPolicy()
        .retryOn(IngloballyAccessException.class)
        .withMaxRetries(1)
        .withDelay(1, TimeUnit.SECONDS);


    @Override
    public ListadoDomiciliosTelefonos request(String documentNumber) {
        return Failsafe.with(retryPolicy).get(() -> {
            Stopwatch stopwatch = Stopwatch.createStarted();
            try {
                return requestDomiciliosService(documentNumber);
            } catch (Exception_Exception e) {
                if (StringUtils.containsIgnoreCase(e.getMessage(), NO_ACCESS_ERROR_MESSAGE)) {
                    // reset token, looks like existing one is invalidated
                    token(true);
                    throw new IngloballyAccessException(e.getMessage());
                } else {
                    throw e;
                }
            } finally {
                log.info("Completed Inglobaly request [DNI: {}] in {} ms", documentNumber, stopwatch.elapsed(TimeUnit.MILLISECONDS));
            }

        });
    }

    private ListadoDomiciliosTelefonos requestDomiciliosService(String dni) throws Exception_Exception {
        log.info("Requesting DomiciliosWebService obtenerDomicilioNIF method with DNI: [{}]", dni);
        return domiciliosWebService.get().obtenerDomicilioNIF(dni, token(false));
    }

    @SneakyThrows
    private synchronized String token(boolean force) {
        if (token == null || force) {
            log.info("Requesting new token");
            token = accesoWebService.get().generarToken(login, password);
            log.info("Acquired new token: [{}]", token);
        }
        return token;
    }

    private AccesoWebService buildAccesoService() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setServiceClass(AccesoWebService.class);
        factoryBean.setAddress(accesoServiceUrl);
        if (debugEnabled) {
            factoryBean.getInInterceptors().add(new LoggingInInterceptor());
            factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
        }
        return factoryBean.create(AccesoWebService.class);
    }

    private DomiciliosWebService buildDomiciliosService() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setServiceClass(DomiciliosWebService.class);
        factoryBean.setAddress(domiciliosServiceUrl);
        if (debugEnabled) {
            factoryBean.getInInterceptors().add(new LoggingInInterceptor());
            factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
        }
        return factoryBean.create(DomiciliosWebService.class);
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAccesoServiceUrl(String accesoServiceUrl) {
        this.accesoServiceUrl = accesoServiceUrl;
    }

    public void setDomiciliosServiceUrl(String domiciliosServiceUrl) {
        this.domiciliosServiceUrl = domiciliosServiceUrl;
    }

    public static class IngloballyAccessException extends RuntimeException {
        public IngloballyAccessException(String message) {
            super(message);
        }
    }
}
