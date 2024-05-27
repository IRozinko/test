package fintech.iovation.impl;

import com.google.common.base.Stopwatch;
import com.google.common.base.Suppliers;
import com.iesnare.dra.api.checktransactiondetails.CheckTransactionDetails;
import com.iesnare.dra.api.checktransactiondetails.CheckTransactionDetailsResponse;
import com.iesnare.dra.api.checktransactiondetails.PortType;
import com.iesnare.dra.api.checktransactiondetails.Service;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.ws.Holder;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component(IovationProviderBean.NAME)
@Setter
public class IovationProviderBean implements IovationProvider {

    public static final String NAME = "iovation-provider";

    private static final String WSDL_URL = "classpath:wsdl/iovation/CheckTransactionDetails.wsdl";

    @Value("${iovation.serviceUrl:}")
    private String serviceUrl;

    @Value("${iovation.subscriberId:}")
    private String subscriberId;

    @Value("${iovation.account:}")
    private String account;

    @Value("${iovation.passCode:}")
    private String passCode;

    @Value("${iovation.type:}")
    private String type;

    @Value("${iovation.connectionTimeout:30000}")
    private long connectionTimeout = TimeUnit.SECONDS.toMillis(30);

    @Value("${iovation.debug:false}")
    private boolean debugEnabled = true;

    private Supplier<PortType> serviceSupplier = Suppliers.synchronizedSupplier(Suppliers.memoize(this::buildService))::get;

    @Override
    public IovationResponse request(IovationRequest request) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            Holder<String> resultHolder = new Holder<>();
            Holder<String> reasonHolder = new Holder<>();
            Holder<String> trackingNumberHolder = new Holder<>();
            Holder<String> endBlackboxHolder = new Holder<>();
            Holder<CheckTransactionDetailsResponse.Details> detailsHolder = new Holder<>();
            serviceSupplier.get().checkTransactionDetails(subscriberId, account, passCode,
                request.getIpAddress(), request.getAccountCode(), request.getBeginBlackBox(), type, buildTxProperties(request), resultHolder, reasonHolder, trackingNumberHolder,
                endBlackboxHolder, detailsHolder);

            IovationResponse response = new IovationResponse();
            response.setResult(resultHolder.value);
            response.setReason(reasonHolder.value);
            response.setEndBlackBox(endBlackboxHolder.value);
            response.setTrackingNumber(trackingNumberHolder.value);
            for (CheckTransactionDetailsResponse.Details.Detail detail : detailsHolder.value.getDetail()) {
                response.getDetails().put(detail.getName(), detail.getValue());
            }
            return response;
        } finally {
            log.info("Completed Iovation request: {} in {} ms", request.toString(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    private CheckTransactionDetails.TxnProperties buildTxProperties(IovationRequest request) {
        CheckTransactionDetails.TxnProperties txnProperties = new CheckTransactionDetails.TxnProperties();
        List<CheckTransactionDetails.TxnProperties.Property> txnProperty = txnProperties.getProperty();

        CheckTransactionDetails.TxnProperties.Property property = new CheckTransactionDetails.TxnProperties.Property();
        property.setName("eventId");
        property.setValue(UUID.randomUUID().toString());
        txnProperty.add(property);

        return txnProperties;
    }

    private PortType buildService() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setServiceClass(PortType.class);
        factoryBean.setAddress(serviceUrl);
        factoryBean.setWsdlURL(WSDL_URL);
        factoryBean.setServiceName(Service.SERVICE);
        factoryBean.setEndpointName(Service.CheckTransactionDetails);
        if (debugEnabled) {
            factoryBean.getInInterceptors().add(new LoggingInInterceptor());
            factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
        }
        PortType service = factoryBean.create(PortType.class);

        Client client = ClientProxy.getClient(service);
        HTTPConduit conduit = (HTTPConduit) client.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(connectionTimeout);
        httpClientPolicy.setAllowChunking(false);
        conduit.setClient(httpClientPolicy);
        return service;
    }
}
