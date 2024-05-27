package fintech.spain.equifax.xml;

import com.equifax.xml.xmlschema.interconnect.*;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import fintech.spain.equifax.impl.EquifaxProvider;
import fintech.spain.equifax.model.EquifaxRequest;
import fintech.spain.equifax.model.EquifaxResponse;
import fintech.spain.equifax.model.EquifaxStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static fintech.BigDecimalUtils.amount;

@Slf4j
@Component(EquifaxXmlProviderBean.NAME)
public class EquifaxXmlProviderBean implements EquifaxProvider {

    private static final String PRESENT_CODE_NOT_FOUND = "01";

    public static final String NAME = "spain-equifax-provider";

    private static final String WSDL_URL = "classpath:wsdl/equifax/IcTransactionService.wsdl";

    @Value("${spain.equifax.userId:}")
    private String userId;

    @Value("${spain.equifax.password:}")
    private String password;

    @Value("${spain.equifax.debug:false}")
    private boolean debugEnabled = true;

    @Value("${spain.equifax.orchestrationCode:}")
    private String orchestrationCode;

    @Value("${spain.equifax.organizationCode:}")
    private String organizationCode;

    @Value("${spain.equifax.url:}")
    private String serviceUrl;

    private Supplier<IcTransactionService> service = () -> {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setServiceClass(IcTransactionService.class);
        factoryBean.setAddress(serviceUrl);
        factoryBean.setWsdlURL(WSDL_URL);
        factoryBean.setServiceName(IcTransactionServiceService.SERVICE);
        factoryBean.setEndpointName(IcTransactionServiceService.IcTransactionService);
        if (debugEnabled) {
            factoryBean.getInInterceptors().add(new LoggingInInterceptor());
            factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
        }
        return factoryBean.create(IcTransactionService.class);
    };

    @Override
    public EquifaxResponse request(EquifaxRequest request) {
        InterConnectRequestType soapRequest = buildSoapRequest(request);
        String requestBody = ObjectSerializer.marshal(ObjectSerializer.wrapInRootElement("http://xml.equifax.com/XMLSchema/InterConnect", "InterConnectRequest", soapRequest));
        EquifaxResponse equifaxResponse = new EquifaxResponse();
        equifaxResponse.setRequestBody(requestBody);

        InterConnectResponseType response;
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            response = request(soapRequest);
        } catch (Exception e) {
            log.error("Error during Equifax request", e);
            equifaxResponse.setStatus(EquifaxStatus.ERROR);
            equifaxResponse.setError(e.getMessage());
            return equifaxResponse;
        } finally {
            log.info("Completed Equifax XML request: [{}] in {} ms", request.toString(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
        String responseBody = ObjectSerializer.marshal(ObjectSerializer.wrapInRootElement("http://xml.equifax.com/XMLSchema/InterConnect", "InterConnectResponse", response));
        equifaxResponse.setResponseBody(responseBody);
        equifaxResponse.setStatus(EquifaxStatus.NOT_FOUND);
        parse(response, equifaxResponse);
        return equifaxResponse;
    }


    private InterConnectResponseType request(InterConnectRequestType request) {
        return service.get().submit(request);
    }

    private void parse(InterConnectResponseType response, EquifaxResponse equifaxResponse) {
        if (response.getErrors() != null) {
            equifaxResponse.setStatus(EquifaxStatus.ERROR);
            if (!response.getErrors().getError().isEmpty()) {
                ErrorType firstError = response.getErrors().getError().get(0);
                equifaxResponse.setError(String.format("%s: %s", firstError.getCode(), firstError.getMessage()));
            } else {
                equifaxResponse.setError("Unknown");
            }
            return;
        }
        List<ConsumerSubjectResponseType> subjects = resolve(() -> response.getConsumerSubjects().getConsumerSubject()).orElse(ImmutableList.of());
        if (subjects.isEmpty()) {
            equifaxResponse.setStatus(EquifaxStatus.ERROR);
            equifaxResponse.setError("Empty consumer subjects element");
            return;
        }
        ConsumerSubjectResponseType subject = subjects.get(0);
        AsnefSeverityScoreType severityScore = resolve(() -> subject.getDataSourceResponses().getEquifaxProducts().getAsnef().getSeverityScore()).orElse(null);
        if (severityScore == null) {
            equifaxResponse.setStatus(EquifaxStatus.ERROR);
            equifaxResponse.setError("No ASNEF severity score element");
            return;
        }
        List<ErrorType> errors = resolve(() -> severityScore.getErrors().getError()).orElse(ImmutableList.of());
        if (!errors.isEmpty()) {
            ErrorType firstError = errors.get(0);
            equifaxResponse.setStatus(EquifaxStatus.ERROR);
            equifaxResponse.setError(String.format("%s: %s", firstError.getCode(), firstError.getMessage()));
            return;
        }

        if (PRESENT_CODE_NOT_FOUND.equals(severityScore.getPresent())) {
            equifaxResponse.setStatus(EquifaxStatus.NOT_FOUND);
            return;
        }

        equifaxResponse.setTotalNumberOfOperations(severityScore.getTotalNumberOfOperations());
        equifaxResponse.setNumberOfConsumerCreditOperations(severityScore.getNumberOfConsumerCreditOperations());
        equifaxResponse.setNumberOfMortgageOperations(severityScore.getNumberOfMortgageOperations());
        equifaxResponse.setNumberOfPersonalLoanOperations(severityScore.getNumberOfPersonalLoanOperations());
        equifaxResponse.setNumberOfCreditCardOperations(severityScore.getNumberOfCreditCardOperations());
        equifaxResponse.setNumberOfTelcoOperations(severityScore.getNumberOfTelcoOperations());
        equifaxResponse.setTotalNumberOfOtherUnpaid(severityScore.getTotalNumberOfOtherUnpaid());
        equifaxResponse.setTotalUnpaidBalance(amount(severityScore.getTotalUnpaidBalance()));
        equifaxResponse.setUnpaidBalanceOwnEntity(amount(severityScore.getUnpaidBalanceOwnEntity()));
        equifaxResponse.setUnpaidBalanceOfOther(amount(severityScore.getUnpaidBalanceOfOther()));
        equifaxResponse.setUnpaidBalanceOfConsumerCredit(amount(severityScore.getUnpaidBalanceOfConsumerCredit()));
        equifaxResponse.setUnpaidBalanceOfMortgage(amount(severityScore.getUnpaidBalanceOfMortgage()));
        equifaxResponse.setUnpaidBalanceOfPersonalLoan(amount(severityScore.getUnpaidBalanceOfPersonalLoan()));
        equifaxResponse.setUnpaidBalanceOfCreditCard(amount(severityScore.getUnpaidBalanceOfCreditCard()));
        equifaxResponse.setUnpaidBalanceOfTelco(amount(severityScore.getUnpaidBalanceOfTelco()));
        equifaxResponse.setUnpaidBalanceOfOtherProducts(amount(severityScore.getUnpaidBalanceOfOtherProducts()));
        equifaxResponse.setWorstUnpaidBalance(amount(severityScore.getWorstUnpaidBalance()));
        equifaxResponse.setWorstSituationCode(severityScore.getWorstSituationCode());
        equifaxResponse.setNumberOfDaysOfWorstSituation(severityScore.getNumberOfDaysOfWorstSituation());
        equifaxResponse.setNumberOfCreditors(severityScore.getNumberOfCreditors());
        equifaxResponse.setDelincuencyDays(severityScore.getDelincuencyDays());
        equifaxResponse.setScoringCategory(severityScore.getScoringCategory());
        equifaxResponse.setStatus(EquifaxStatus.FOUND);
    }

    private InterConnectRequestType buildSoapRequest(EquifaxRequest request) {
        RequestInput input = new RequestInput();
        input.setUserId(this.userId);
        input.setPassword(this.password);
        input.setOrganizationCode(this.organizationCode);
        input.setOrchestrationCode(this.orchestrationCode);
        input.setDocumentNumber(request.getDocumentNumber());
        return new EquifaxRequestBuilder(input).build();
    }

    public static <T> Optional<T> resolve(java.util.function.Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

}
