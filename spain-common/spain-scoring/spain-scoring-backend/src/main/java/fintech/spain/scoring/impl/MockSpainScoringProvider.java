package fintech.spain.scoring.impl;

import fintech.spain.scoring.model.ScoringModelType;
import fintech.spain.scoring.model.ScoringRequestCommand;
import fintech.spain.scoring.spi.ScoringResponse;
import fintech.spain.scoring.spi.SpainScoringProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

import static fintech.BigDecimalUtils.amount;

@Slf4j
@Component(MockSpainScoringProvider.NAME)
public class MockSpainScoringProvider implements SpainScoringProvider {

    public static final String NAME = "mock-spain-scoring-provider";
    private final Map<ScoringModelType, ScoringResponse> responseMap = new EnumMap<>(ScoringModelType.class);

    private boolean throwError = false;

    public MockSpainScoringProvider() {
        reset();
    }

    private ScoringResponse mockedResponse() {
        return ScoringResponse.ok(200, "MOCK", BigDecimal.ONE)
            .setDecisionEngineRequestId(1L)
            .setRating("A11111")
            .setDecision("Approved");
    }

    private ScoringResponse manualResponse() {
        return ScoringResponse.ok(200, "MOCK", BigDecimal.ONE)
            .setDecisionEngineRequestId(2L)
            .setRating("M11111")
            .setDecision("Manually");
    }


    private ScoringResponse interestRateResponse(BigDecimal score) {
        return ScoringResponse.ok(200, "MOCK", amount(score))
            .setDecisionEngineRequestId(3L)
            .setRating("A")
            .setDecision("A");
    }
    @Override
    public ScoringResponse request(ScoringRequestCommand command) {
        if (throwError) {
            throw new RuntimeException("Mock scoring failed");
        }
        log.warn("Returning mock scoring response: [{}]", responseMap.get(command.getType()));
        return responseMap.get(command.getType());
    }

    public void setResponse(ScoringModelType type, ScoringResponse response) {
        this.responseMap.put(type, response);
    }

    public void reset() {
        this.responseMap.clear();
        this.responseMap.put(ScoringModelType.CREDIT_LIMIT_MODEL, mockedResponse());
        this.responseMap.put(ScoringModelType.DEDICATED_MODEL, mockedResponse());
        this.responseMap.put(ScoringModelType.LINEAR_REGRESSION_MODEL, mockedResponse());
        this.responseMap.put(ScoringModelType.FINTECH_MARKET, mockedResponse());
        this.responseMap.put(ScoringModelType.PTI_VALIDATION, mockedResponse());
    }

    public void setThrowError(boolean throwError) {
        this.throwError = throwError;
    }

    public void useManualResponse() {
        this.responseMap.put(ScoringModelType.CREDIT_LIMIT_MODEL, manualResponse());
        this.responseMap.put(ScoringModelType.DEDICATED_MODEL, manualResponse());
        this.responseMap.put(ScoringModelType.LINEAR_REGRESSION_MODEL, manualResponse());
        this.responseMap.put(ScoringModelType.FINTECH_MARKET, manualResponse());
        this.responseMap.put(ScoringModelType.PTI_VALIDATION, manualResponse());
    }

    public void useInterestRateResponse(BigDecimal score) {
        this.responseMap.put(ScoringModelType.INTEREST_RATE_MODEL, interestRateResponse(score));
    }
}
