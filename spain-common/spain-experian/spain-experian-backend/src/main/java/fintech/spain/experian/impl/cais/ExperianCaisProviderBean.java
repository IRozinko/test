package fintech.spain.experian.impl.cais;

import cais.servicios.experian.Informe;
import cais.servicios.experian.PeticionInforme;
import cais.servicios.experian.ServicioInformes;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static cais.servicios.experian.ServicioInformes_Service.BasicHttpBindingServicioInformes;
import static cais.servicios.experian.ServicioInformes_Service.SERVICE;

@Slf4j
@Component(ExperianCaisProviderBean.NAME)
public class ExperianCaisProviderBean implements ExperianCaisProvider {

    static final String NAME = "spain-experian-cais-provider";

    private final Supplier<ServicioInformes> servicioInformes = this::initService;

    private final ExperianCaisConfiguration configuration;

    public ExperianCaisProviderBean(ExperianCaisConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Informe request(PeticionInforme reportRequest) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            return servicioInformes.get().generarInforme(reportRequest);
        } finally {
            log.info("Completed Experian request: [params: {}] in {} ms", reportRequest.getParametros().getTParametro(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    private ServicioInformes initService() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();

        factoryBean.setServiceClass(ServicioInformes.class);
        factoryBean.setServiceName(SERVICE);
        factoryBean.setEndpointName(BasicHttpBindingServicioInformes);

        factoryBean.setAddress(configuration.getServiceUrl());
        factoryBean.setWsdlURL(configuration.getWsdlUrl());

        if (configuration.isDebugEnabled()) {
            factoryBean.getInInterceptors().add(new LoggingInInterceptor());
            factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
            factoryBean.getInFaultInterceptors().add(new LoggingOutInterceptor());
            factoryBean.getOutFaultInterceptors().add(new LoggingOutInterceptor());
        }
        ServicioInformes informes = factoryBean.create(ServicioInformes.class);
        Client client = ClientProxy.getClient(informes);
        if (client != null) {
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = new HTTPClientPolicy();
            policy.setConnectionTimeout(configuration.getHttpTimeout());
            policy.setReceiveTimeout(configuration.getHttpTimeout());
            conduit.setClient(policy);
        }
        return informes;
    }

}
