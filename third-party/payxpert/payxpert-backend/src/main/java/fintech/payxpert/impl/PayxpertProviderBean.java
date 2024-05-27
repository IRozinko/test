package fintech.payxpert.impl;

import com.google.common.base.Stopwatch;
import com.payxpert.connect2pay.client.Connect2payClient;
import com.payxpert.connect2pay.client.requests.PaymentRequest;
import com.payxpert.connect2pay.client.requests.PaymentStatusRequest;
import com.payxpert.connect2pay.client.response.PaymentResponse;
import com.payxpert.connect2pay.client.response.PaymentStatusResponse;
import com.payxpert.connect2pay.utils.Connect2payRESTClient;
import fintech.JsonUtils;
import fintech.payxpert.PayxpertPaymentRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component(PayxpertProviderBean.NAME)
public class PayxpertProviderBean implements PayxpertProvider {

    public static final String NAME = "live-payxpert";

    @Value("${payxpert.serviceUrl:https://connect2.payxpert.com/}")
    private String serviceUrl;

    @Value("${payxpert.gatewayServiceUrl:https://api.payxpert.com/}")
    private String gatewayServiceUrl;

    @Value("${payxpert.originator:104772}")
    private String originator;

    @Value("${payxpert.apiKey:invalid}")
    private String apiKey;

    @Value("${payxpert.apiKeyBase64Encoded:false}")
    private boolean apiKeyBase64Encoded;

    @Value("${payxpert.timeoutInMs:15000}")
    private int timeoutInMs;

    @SneakyThrows
    @Override
    public PaymentResponse preparePayment(PaymentRequest request) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            Connect2payClient c2p = newClient();
            return c2p.preparePayment(request);
        } finally {
            log.info("Completed Payxpert preparePayment request: [orderId: {}] in {} ms", request.getOrderId(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    @SneakyThrows
    @Override
    public PaymentStatusResponse handleCallback(String callbackJson) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            return newClient().handleCallbackStatus(callbackJson);
        } finally {
            log.info("Completed Payxpert handleCallback request: [callbackJson: {}] in {} ms", callbackJson, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }

    }

    @SneakyThrows
    @Override
    public PaymentStatusResponse checkRequestStatus(PayxpertPaymentRequest request) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            PaymentStatusRequest statusRequest = new PaymentStatusRequest();
            statusRequest.setMerchantToken(request.getMerchantToken());
            return newClient().getPaymentStatus(statusRequest);
        } finally {
            log.info("Completed Payxpert getPaymentStatus request: [clientId: {}] in {} ms", request.getClientId(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }


    }

    @SneakyThrows
    @Override
    public RebillResponse rebill(RebillRequest request) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Connect2payRESTClient httpClient = (new Connect2payRESTClient()).addBasicAuthentication(originator, apiKey());
        httpClient.setBody(JsonUtils.writeValueAsString(request));
        httpClient.setUrl(gatewayServiceUrl + "transaction/" + request.getTransactionID() + "/rebill");
        String body = httpClient.post();
        log.info("Rebill request [{}] response: [{}] request time {} ms:", request, body, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return JsonUtils.readValue(body, RebillResponse.class);
    }

    private Connect2payClient newClient() {
        Connect2payClient c2p = new Connect2payClient(serviceUrl, originator, apiKey());
        c2p.setTimeOutInMilliSeconds(timeoutInMs);
        return c2p;
    }

    private String apiKey() {
        if (apiKeyBase64Encoded) {
            return new String(Base64.getDecoder().decode(apiKey), StandardCharsets.UTF_8);
        } else {
            return apiKey;
        }
    }
}
