package fintech.spain.experian.impl.concursales;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import concursales.servicios.experian.GenerarInformeResponse;
import concursales.servicios.experian.Informe;
import concursales.servicios.experian.PeticionInforme;
import concursales.servicios.experian.ServicioInformes;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.stereotype.Component;

import static concursales.servicios.experian.ServicioInformes_Service.BasicHttpBindingServicioInformes;
import static concursales.servicios.experian.ServicioInformes_Service.SERVICE;


@Slf4j
@Component(ExperianConcursalesProviderBean.NAME)
public class ExperianConcursalesProviderBean implements ExperianConcursalesProvider {

    static final String NAME = "experian-concursales-provider";

    private final Supplier<ServicioInformes> servicioInformes = Suppliers.memoize(this::initService);
    private final ExperianConcursalesConfiguration configuration;

    public ExperianConcursalesProviderBean(ExperianConcursalesConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public GenerarInformeResponse request(PeticionInforme reportRequest) {
        Informe informe = servicioInformes.get().generarInforme(reportRequest);

        GenerarInformeResponse generarInformeResponse = new GenerarInformeResponse();
        generarInformeResponse.setGenerarInformeResult(informe);
        return generarInformeResponse;
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
        ServicioInformes servicioInformes = factoryBean.create(ServicioInformes.class);
        Client client = ClientProxy.getClient(servicioInformes);
        if (client != null) {
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = new HTTPClientPolicy();
            policy.setConnectionTimeout(configuration.getHttpTimeout());
            policy.setReceiveTimeout(configuration.getHttpTimeout());
            conduit.setClient(policy);
        }
        return servicioInformes;
    }
}
