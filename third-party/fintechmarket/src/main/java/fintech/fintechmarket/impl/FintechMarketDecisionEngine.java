package fintech.fintechmarket.impl;

import com.google.common.base.Stopwatch;
import fintech.JsonUtils;
import fintech.decision.model.DecisionEngineRequest;
import fintech.decision.model.DecisionRequestStatus;
import fintech.decision.model.DecisionResult;
import fintech.decision.spi.DecisionEngine;
import fintech.fintechmarket.InquiryFintechMarketClient;
import fintech.fintechmarket.dto.NewInquiryResponse;
import fintech.fintechmarket.dto.StartInquiryRequest;
import fintech.fintechmarket.dto.StartInquiryResponse;
import fintech.fintechmarket.settings.FintechMarketSettings;
import fintech.scoring.values.model.ScoringValue;
import fintech.settings.SettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.function.CheckedFunction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fintech.fintechmarket.impl.FintechMarketDecisionEngine.NAME;
import static fintech.fintechmarket.settings.FintechMarketSettings.FINTECT_MARKET_SETTINGS;
import static java.lang.String.valueOf;

@Slf4j
@RequiredArgsConstructor
@Component(NAME)
public class FintechMarketDecisionEngine implements DecisionEngine {

    public static final String NAME = "fintechmarket-decision-engine";

    private static final RetryPolicy RETRY_POLICY = new RetryPolicy()
        .retryOn(Exception.class)
        .withMaxRetries(3)
        .withDelay(1, TimeUnit.SECONDS);

    private final InquiryFintechMarketClient client;
    private final SettingsService settingsService;

    @Override
    public DecisionResult getDecision(DecisionEngineRequest request) {
        return Failsafe.with(RETRY_POLICY)
            .withFallback((CheckedFunction<? extends Throwable, ?>) ex -> {
                log.error(ex.getMessage(), ex);
                return DecisionResult.error(ex.getMessage());
            })
            .get(() -> getDecisionInternal(request));
    }

    private DecisionResult getDecisionInternal(DecisionEngineRequest request) {
        FintechMarketSettings settings = settingsService.getJson(FINTECT_MARKET_SETTINGS, FintechMarketSettings.class);

        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            NewInquiryResponse newInquiry = client.newInquiry(request.getScenario(), settings.getBrand());
            List<NewInquiryResponse.Field> requiredFields = newInquiry.getData().getFields();

            Map<String, ScoringValue> fields = getRequiredValues(requiredFields, request.getValues());
            Map<String, Object> requestFields = fields.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getValue()));

            StartInquiryRequest startInquiryRequest = new StartInquiryRequest()
                .setData(new StartInquiryRequest.Data()
                    .setBrandKey(settings.getBrand())
                    .setEntityExternalId(valueOf(request.getScoringModelId()))
                    .setPersonExternalId(valueOf(request.getClientId()))
                    .setLockVersion(newInquiry.getData().getLockVersion())
                    .setFields(requestFields)
                );

            StartInquiryResponse response = client.startInquiry(request.getScenario(), startInquiryRequest, settings.getBrand());
            StartInquiryResponse.PrimaryResult primaryResult = response.getData().getPrimaryResult();

            DecisionResult result = toDecisionResult(primaryResult);
            result.setResponse(JsonUtils.writeValueAsString(response));
            result.setUsedFields(new ArrayList<>(fields.values()));
            return result;
        } finally {
            log.info("Completed FintechMarketDecision request: modelId {} in {} ms", request.getScoringModelId(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    private Map<String, ScoringValue> getRequiredValues(List<NewInquiryResponse.Field> requiredFields,
                                                        List<ScoringValue> values) {
        Map<String, ScoringValue> valueMap = values.stream().collect(Collectors.toMap(ScoringValue::getKey, Function.identity()));

        return requiredFields.stream()
            .map(f -> valueMap.get(f.getKey()))
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(ScoringValue::getKey, Function.identity()));
    }

    private DecisionResult toDecisionResult(StartInquiryResponse.PrimaryResult primaryResult) {
        return new DecisionResult()
            .setStatus(DecisionRequestStatus.OK)
            .setDecision(primaryResult.getDecision())
            .setRating(primaryResult.getRating())
            .setScore(primaryResult.getScore())
            .setArrayResult(primaryResult.getArrayResult())
            .setVariablesResult(primaryResult.getScenarioVariablesResult());
    }

}
