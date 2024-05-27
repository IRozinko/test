package fintech.spain.alfa.product.presto.api;


import fintech.JsonUtils;
import fintech.TimeMachine;
import fintech.crm.client.model.PrestoDormantsResponse;
import fintech.mock.MockCall;
import fintech.mock.MockResponseBody;
import fintech.spain.platform.web.model.DormantsData;
import fintech.web.api.models.AmortizationPayment;
import fintech.web.api.models.AmortizationPreviewResponse;
import fintech.web.api.models.ContractAgreementRequest;
import fintech.web.api.models.WithdrawalRequest;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import retrofit2.Call;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static fintech.BigDecimalUtils.amount;

@Component(MockLineOfCreditCrossApiClient.NAME)
public class MockLineOfCreditCrossApiClient implements LineOfCreditCrossApiClient {

    public static final String NAME = "mock-line-of-credit-cross-api";

    public static final String TEST_AGREEMENT_FILE_NAME = "test-agreement-44444.pdf";
    public static final String TEST_STANDARD_INFO_FILE_NAME = "test-standard-info-44444.pdf";
    public static final String FAKE_TOKEN = "fake-token";
    public static final String FAKE_LINK = "fake-link";

    @Override
    public Call<AmortizationPreviewResponse> amortizationPreview(WithdrawalRequest request) {
        List<AmortizationPayment> payments = new ArrayList<>();
        AmortizationPayment payment1 = new AmortizationPayment();
        payment1.setDate(TimeMachine.today());
        payment1.setInterest(new BigDecimal(18));
        payment1.setNumber(1);
        payment1.setPrincipal(new BigDecimal(1000));
        payment1.setTotal(new BigDecimal(1000));
        payment1.setDueDate(TimeMachine.today().plusDays(30));
        AmortizationPayment payment2 = new AmortizationPayment();
        payment2.setDate(TimeMachine.today().plusDays(30));
        payment2.setInterest(new BigDecimal(18));
        payment2.setNumber(1);
        payment2.setPrincipal(new BigDecimal(1000));
        payment2.setTotal(new BigDecimal(1000));
        payment2.setDueDate(TimeMachine.today().plusDays(60));
        payments.add(payment1);
        payments.add(payment2);

        AmortizationPreviewResponse response = new AmortizationPreviewResponse(
            TimeMachine.today().plusMonths(1),
            amount(240),
            amount(20),
            payments
        );
        return new MockCall<>(response, new Headers.Builder().build());
    }

    @Override
    public Call<ResponseBody> generateAgreementDocument(ContractAgreementRequest request) {
        String fileContent = JsonUtils.writeValueAsString(request);
        return new MockCall<>(
            new MockResponseBody(fileContent, MediaType.APPLICATION_OCTET_STREAM_VALUE),
            new Headers.Builder().add("Content-Disposition", "attachment; filename=" + TEST_AGREEMENT_FILE_NAME).build());
    }

    @Override
    public Call<ResponseBody> generateStandardInfoDocument() {
        String fileContent = "Test content";
        return new MockCall<>(
            new MockResponseBody(fileContent, MediaType.APPLICATION_OCTET_STREAM_VALUE),
            new Headers.Builder().add("Content-Disposition", "attachment; filename=" + TEST_STANDARD_INFO_FILE_NAME).build());
    }

    @Override
    public Call<PrestoDormantsResponse> sendClient(DormantsData data) {
        return new MockCall<>(new PrestoDormantsResponse().setLink(FAKE_LINK).setToken(FAKE_TOKEN));
    }

}
