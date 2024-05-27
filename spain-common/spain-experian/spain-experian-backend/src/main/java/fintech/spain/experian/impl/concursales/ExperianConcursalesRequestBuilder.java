package fintech.spain.experian.impl.concursales;

import concursales.servicios.experian.ArrayOfTParametro;
import concursales.servicios.experian.PeticionInforme;
import concursales.servicios.experian.TParametro;
import fintech.TimeMachine;
import fintech.spain.experian.model.ConcursalesRequest;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

import static org.apache.commons.lang3.RandomStringUtils.random;

@Component
public class ExperianConcursalesRequestBuilder {

    private static final String VERSION_REPORT = "1.0";
    private static final String TYPE_REPORT = "DETALLE";
    private static final String RETURN_RECORDS_FROM_INDEX_VALUE = null;
    private static final String RETURN_RECORDS_COUNT_VALUE = null;
    private static final String CREDITORS_CONTEST_WITHOUT_LIQUIDATION_REQUEST_VALUE = "1";
    private static final String CREDITORS_CONTEST_WITH_LIQUIDATION_REQUEST_VALUE = "1";
    private static final String CREDITORS_CONTEST_WITHOUT_BREACH_VALUE = "1";
    private static final String CREDITORS_CONTEST_WITH_BREACH_VALUE = "1";
    private static final String LIQUIDATION_VALUE = "1";
    private static final String SITUATION_NORMAL_VALUE = "1";
    private static final String DATE_FORMAT = "yyyyMMdd";

    private final ExperianConcursalesConfiguration configuration;

    public ExperianConcursalesRequestBuilder(ExperianConcursalesConfiguration configuration) {
        this.configuration = configuration;
    }

    public PeticionInforme prepareRequest(ConcursalesRequest request) {
        PeticionInforme serviceRequest = new PeticionInforme();
        serviceRequest.setParametros(new ArrayOfTParametro());
        serviceRequest.setIdUsuarioServicio(configuration.getServiceLogin());
        serviceRequest.setClaveServicio(configuration.getServicePassword());
        serviceRequest.setTipoInforme(TYPE_REPORT);
        serviceRequest.setVersion(VERSION_REPORT);
        // query reference. random text to identify the response.
        addParameterIfNotNull(serviceRequest, ExperianConcursalesRequestParameter.REFERENCE_CONSULTATION, generateReferenceConsultation());
        // Code of the subscriber making the query
        addParameterIfNotNull(serviceRequest, ExperianConcursalesRequestParameter.ID_SUBSCRIBER, configuration.getServiceSubscriberId());
        // Query start date. Format: yyyyMMdd
        String date = TimeMachine.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        addParameterIfNotNull(serviceRequest, ExperianConcursalesRequestParameter.DATE, date);
        // In bankruptcy without winding up	petition
        addParameterIfNotNull(serviceRequest, ExperianConcursalesRequestParameter.CREDITORS_CONTEST_WITHOUT_LIQUIDATION_REQUEST, CREDITORS_CONTEST_WITHOUT_LIQUIDATION_REQUEST_VALUE);
        // In bankruptcy with winding up petition
        addParameterIfNotNull(serviceRequest, ExperianConcursalesRequestParameter.CREDITORS_CONTEST_WITH_LIQUIDATION_REQUEST, CREDITORS_CONTEST_WITH_LIQUIDATION_REQUEST_VALUE);
        // In agreement with creditors without default
        addParameterIfNotNull(serviceRequest, ExperianConcursalesRequestParameter.CREDITORS_CONTEST_WITHOUT_BREACH, CREDITORS_CONTEST_WITHOUT_BREACH_VALUE);
        // In agreement with creditors with default
        addParameterIfNotNull(serviceRequest, ExperianConcursalesRequestParameter.CREDITORS_CONTEST_WITH_BREACH, CREDITORS_CONTEST_WITH_BREACH_VALUE);
        // In settlement
        addParameterIfNotNull(serviceRequest, ExperianConcursalesRequestParameter.LIQUIDATION, LIQUIDATION_VALUE);
        // In Normal / Low situation
        addParameterIfNotNull(serviceRequest, ExperianConcursalesRequestParameter.SITUATION_NORMAL, SITUATION_NORMAL_VALUE);
        // Order number of the first registry to obtain
        addParameterIfNotNull(serviceRequest, ExperianConcursalesRequestParameter.RETURN_RECORDS_FROM_INDEX, RETURN_RECORDS_FROM_INDEX_VALUE);
        // Number of registries requested
        addParameterIfNotNull(serviceRequest, ExperianConcursalesRequestParameter.RETURN_RECORDS_COUNT, RETURN_RECORDS_COUNT_VALUE);
        return serviceRequest;
    }

    private static void addParameterIfNotNull(PeticionInforme requestParameters, ExperianConcursalesRequestParameter parameterKey, String value) {
        if (value != null) {
            TParametro parameter = new TParametro();
            parameter.setNombre(parameterKey.getKey());
            parameter.setValor(value);
            requestParameters.getParametros().getTParametro().add(parameter);
        }
    }

    private static String generateReferenceConsultation() {
        return TimeMachine.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + random(6, true, true);
    }
}
