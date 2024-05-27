package fintech.spain.experian.impl.cais;

import cais.servicios.experian.ArrayOfTParametro;
import cais.servicios.experian.PeticionInforme;
import cais.servicios.experian.TParametro;
import fintech.TimeMachine;
import fintech.spain.experian.model.CaisDocumentType;
import fintech.spain.experian.model.CaisRequest;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

import static org.apache.commons.lang3.RandomStringUtils.random;

@Component
public class ExperianCaisRequestBuilder {

    private ExperianCaisConfiguration configuration;

    public ExperianCaisRequestBuilder(ExperianCaisConfiguration configuration) {
        this.configuration = configuration;
    }

    public PeticionInforme prepareRequest(CaisRequest caisRequest, ExperianCaisConfiguration.ReportType reportType) {
        PeticionInforme serviceRequest = new PeticionInforme();
        serviceRequest.setParametros(new ArrayOfTParametro());
        // request configuration params
        setRequestConfigurationParams(serviceRequest, reportType);
        // request target params
        setRequestTargetParams(caisRequest, serviceRequest);
        return serviceRequest;
    }

    private void setRequestTargetParams(CaisRequest caisRequest, PeticionInforme serviceRequest) {
        CaisDocumentType documentType = CaisDocumentType.getTypeOfDocumentNumber(caisRequest.getDocumentNumber());
        addParameterIfNotNull(serviceRequest, ExperianCaisRequestParameter.TYPE_DOCUMENT, documentType.getCode());
        addParameterIfNotNull(serviceRequest, ExperianCaisRequestParameter.NUMBER_DOCUMENT, caisRequest.getDocumentNumber());

        // country of issue	code of the	document to be queried
        addParameterIfNotNull(serviceRequest, ExperianCaisRequestParameter.COUNTRY_DOCUMENT, ExperianCaisConfiguration.COUNTRY_DOCUMENT_VALUE);
        // Transaction identifier. If this field is left blank, the detail of all transactions corresponding to the document to query shall be returned.
        addParameterIfNotNull(serviceRequest, ExperianCaisRequestParameter.TRANSACTION_ID, ExperianCaisConfiguration.TRANSACTION_ID_VALUE);
        // order number of the first registry to obtain
        addParameterIfNotNull(serviceRequest, ExperianCaisRequestParameter.RETURN_RECORDS_FROM_INDEX, ExperianCaisConfiguration.RETURN_RECORDS_FROM_INDEX_VALUE);
        // Number of registries to obtain
        addParameterIfNotNull(serviceRequest, ExperianCaisRequestParameter.RETURN_RECORDS_COUNT, ExperianCaisConfiguration.RETURN_RECORDS_COUNT_VALUE);
    }

    private void setRequestConfigurationParams(PeticionInforme serviceRequest, ExperianCaisConfiguration.ReportType reportType) {
        serviceRequest.setIdUsuarioServicio(configuration.getServiceLogin());
        serviceRequest.setClaveServicio(configuration.getServicePassword());
        serviceRequest.setTipoInforme(reportType.getType());
        serviceRequest.setVersion(ExperianCaisConfiguration.VERSION_REPORT);

        // query reference. random text to identify the response.
        addParameterIfNotNull(serviceRequest, ExperianCaisRequestParameter.REFERENCE_CONSULTATION, generateReferenceConsultation());
        // Code of the subscriber making the query
        addParameterIfNotNull(serviceRequest, ExperianCaisRequestParameter.ID_SUBSCRIBER, configuration.getServiceSubscriberId());
    }

    private static void addParameterIfNotNull(PeticionInforme requestParameters, ExperianCaisRequestParameter parameterKey, String value) {
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
