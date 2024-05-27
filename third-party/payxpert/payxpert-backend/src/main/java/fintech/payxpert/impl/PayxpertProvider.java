package fintech.payxpert.impl;

import com.payxpert.connect2pay.client.requests.PaymentRequest;
import com.payxpert.connect2pay.client.response.PaymentResponse;
import com.payxpert.connect2pay.client.response.PaymentStatusResponse;
import fintech.payxpert.PayxpertPaymentRequest;

public interface PayxpertProvider {

    PaymentResponse preparePayment(PaymentRequest request);

    PaymentStatusResponse handleCallback(String callbackJson);

    PaymentStatusResponse checkRequestStatus(PayxpertPaymentRequest request);

    RebillResponse rebill(RebillRequest request);
}
