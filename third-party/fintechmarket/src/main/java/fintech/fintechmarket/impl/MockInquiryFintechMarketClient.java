package fintech.fintechmarket.impl;

import fintech.fintechmarket.InquiryFintechMarketClient;
import fintech.fintechmarket.dto.NewInquiryResponse;
import fintech.fintechmarket.dto.StartInquiryRequest;
import fintech.fintechmarket.dto.StartInquiryResponse;
import fintech.fintechmarket.exception.ScenarioVersionConflictException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class MockInquiryFintechMarketClient implements InquiryFintechMarketClient {

    public static final int LOCK_VERSION = 2;

    @Override
    public NewInquiryResponse newInquiry(String scenarioKey, String brand) {
        return newInquiry(scenarioKey, LOCK_VERSION);
    }

    @Override
    public StartInquiryResponse startInquiry(String scenarioKey, StartInquiryRequest request, String brand) {
        if (request.getData().getLockVersion() != LOCK_VERSION)
            throw new ScenarioVersionConflictException();

        return startInquiryResponse();
    }

    public static NewInquiryResponse newInquiry(String scenarioKey, int lockVersion) {
        return new NewInquiryResponse()
            .setData(new NewInquiryResponse.Data()
                .setLockVersion(lockVersion)
                .setScenario(new NewInquiryResponse.Scenario().setKey(scenarioKey))
                .setFields(Arrays.asList(new NewInquiryResponse.Field().setKey("test_field")))
                .setScenarioVersion(new NewInquiryResponse.ScenarioVersion().setId("8c198fe0-19b4-4608-a397-4ed6d77ed68a")));
    }

    public static StartInquiryResponse startInquiryResponse() {
        return new StartInquiryResponse()
            .setData(new StartInquiryResponse.Data()
                .setPrimaryResult(new StartInquiryResponse.PrimaryResult().setScore(BigDecimal.TEN)));
    }
}
