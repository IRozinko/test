package fintech.payxpert.impl;

import com.payxpert.connect2pay.client.Connect2payClient;
import com.payxpert.connect2pay.client.requests.PaymentRequest;
import com.payxpert.connect2pay.client.response.PaymentResponse;
import com.payxpert.connect2pay.client.response.PaymentStatusResponse;
import com.payxpert.connect2pay.constants.ResultCode;
import fintech.ClasspathUtils;
import fintech.payxpert.PayxpertConstants;
import fintech.payxpert.PayxpertPaymentRequest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component(MockPayxpertProviderBean.NAME)
public class MockPayxpertProviderBean implements PayxpertProvider {

    public static final String NAME = "mock-payxpert";

    private RebillResponse rebillResponse = successRebillResponse();
    private boolean successfulCallback = true;

    @Override
    public PaymentResponse preparePayment(PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();
        response.setCode(ResultCode.SUCCESS);
        response.setServiceURL("https://payxpert");
        response.setCustomerToken("mock");
        return response;
    }

    @SneakyThrows
    @Override
    public PaymentStatusResponse handleCallback(String callbackJson) {
        Connect2payClient c2p = new Connect2payClient("https://mock", "mock", "mock");
        return c2p.handleCallbackStatus(callbackJson);
    }

    @Override
    public PaymentStatusResponse checkRequestStatus(PayxpertPaymentRequest request) {
        if (successfulCallback) {
            return handleCallback(prepareSuccessCallbackJson(request));
        } else {
            return handleCallback(prepareErrorCallbackJson(request));
        }
    }

    @Override
    public RebillResponse rebill(RebillRequest request) {
        return rebillResponse;
    }

    public static RebillResponse successRebillResponse() {
        return new RebillResponse()
            .setErrorCode(PayxpertConstants.SUCCESS_CODE)
            .setErrorMessage("Transaction successfully completed")
            .setTransactionID(RandomStringUtils.randomNumeric(12))
            .setStatementDescriptor("mock");
    }

    public static RebillResponse failedRebillResponse(String errorCode) {
        return new RebillResponse()
            .setErrorCode(errorCode)
            .setErrorMessage("Error")
            .setTransactionID(RandomStringUtils.randomNumeric(12))
            .setStatementDescriptor("mock");
    }

    public static String prepareSuccessCallbackJson(PayxpertPaymentRequest request) {
        String json = ClasspathUtils.resourceToString("payxpert/mock-success-callback.json");
        return StringUtils.replace(json, "#orderId#", request.getOrderId());
    }

    public static String prepareErrorCallbackJson(PayxpertPaymentRequest request) {
        String json = ClasspathUtils.resourceToString("payxpert/mock-error-callback.json");
        return StringUtils.replace(json, "#orderId#", request.getOrderId());
    }

    public void setRebillResponse(RebillResponse rebillResponse) {
        this.rebillResponse = rebillResponse;
    }

    public void setSuccessfulCallback(boolean successfulCallback) {
        this.successfulCallback = successfulCallback;
    }
}
