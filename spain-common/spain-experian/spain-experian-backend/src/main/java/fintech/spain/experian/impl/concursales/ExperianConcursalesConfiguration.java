package fintech.spain.experian.impl.concursales;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ExperianConcursalesConfiguration {
    private static final String WSDL_URL = "classpath:/wsdl/experian/concursales/concursales.wsdl";

    @Value("${experian.concursales.serviceUrl:http://localhost:8301/ConcursalesWebServiceUAT/ServicioInformes.svc}")
    private String serviceUrl;

    @Value("${experian.concursales.serviceLogin:1111111}")
    private String serviceLogin;

    @Value("${experian.concursales.servicePassword:222222}")
    private String servicePassword;

    @Value("${experian.concursales.serviceSubscriberId:111}")
    private String serviceSubscriberId;

    @Value("${experian.concursales.debugEnabled:true}")
    private boolean debugEnabled;

    @Value("${experian.concursales.httpTimeout:10000}")
    private int httpTimeout;

    String getWsdlUrl() {
        return WSDL_URL;
    }

}
