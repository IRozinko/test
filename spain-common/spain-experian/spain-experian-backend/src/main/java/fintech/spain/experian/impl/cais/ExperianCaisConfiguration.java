package fintech.spain.experian.impl.cais;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import v2.cais.servicios.experian.InformeListaOperaciones;
import v2.cais.servicios.experian.InformeResumen;

@Component
@Getter
public class ExperianCaisConfiguration {
    private static final String WSDL_URL = "classpath:/wsdl/experian/cais/cais.wsdl";

    @Value("${spain.experian.cais.url:http://localhost:8301/CaisWebServiceUAT/ServicioInformes.svc}")
    private String serviceUrl;

    @Value("${spain.experian.cais.login:1111111}")
    private String serviceLogin;

    @Value("${spain.experian.cais.password:222222}")
    private String servicePassword;

    @Value("${spain.experian.cais.subscriberId:111}")
    private String serviceSubscriberId;

    @Value("${spain.experian.cais.debugEnabled:true}")
    private boolean debugEnabled;

    @Value("${spain.experian.cais.httpTimeout:10000}")
    private int httpTimeout;

    String getWsdlUrl() {
        return WSDL_URL;
    }

    /**
     * Constants for requesting CAIS service
     */
    static final String VERSION_REPORT = "2.0";

    /**
     * Requested Report Type and serialization class.
     */
    public static final ReportType<InformeResumen> RESUMEN =
        new ReportType<>("RESUMEN", InformeResumen.class);

    public static final ReportType<InformeListaOperaciones> LISTA_OPERACIONES =
        new ReportType<>("LISTA_OPERACIONES", InformeListaOperaciones.class);

    static final String COUNTRY_DOCUMENT_VALUE = "724";
    static final String TRANSACTION_ID_VALUE = "";
    static final String RETURN_RECORDS_FROM_INDEX_VALUE = "";
    static final String RETURN_RECORDS_COUNT_VALUE = "";


    @Data
    public static class ReportType<T> {
        private String type;
        private Class<T> clazz;

        public ReportType(String type, Class<T> clazz) {
            this.type = type;
            this.clazz = clazz;
        }
    }
}
