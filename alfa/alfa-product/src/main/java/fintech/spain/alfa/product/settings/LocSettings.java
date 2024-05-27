package fintech.spain.alfa.product.settings;


import com.google.common.collect.ImmutableList;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.settings.SettingsService;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static fintech.BigDecimalUtils.amount;
import static fintech.spain.alfa.product.settings.AlfaSettings.CrosscheckRuleSettings;
import static fintech.spain.alfa.product.settings.AlfaSettings.ExperianRuleSettings;
import static fintech.spain.alfa.product.settings.AlfaSettings.IntegrationSettings;

@Component
public class LocSettings {

    public static final String LOC_INTEGRATION_SETTINGS = "LocIntegrationSettings";
    public static final String LOC_LENDING_RULES_CROSSCHECK = "LocLendingRulesCrosscheck";
    public static final String LOC_LENDING_RULES_EXPERIAN = "LocLendingRulesExperian";
    public static final String LOC_LENDING_RULES_EQUIFAX = "LocLendingRulesEquifax";
    public static final String LOC_LENDING_RULES_INSTANTOR = "LocLendingRulesInstantor";
    public static final String LOC_CREDIT_LIMIT_SETTINGS = "LocCreditLimitSettings";

    @Autowired
    private SettingsService settings;

    public void setUp() {
        settings.initProperty(LOC_INTEGRATION_SETTINGS, defaultIntegrationSettings(), "Loc Settings for 3rd party integrations", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, IntegrationSettings.class).getInstantor() != null));
        settings.initProperty(LOC_LENDING_RULES_CROSSCHECK, defaultCrosscheckRules(), "Loc Settings for Presto crosscheck", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, CrosscheckRuleSettings.class).getMaxDpd() != null));
        settings.initProperty(LOC_LENDING_RULES_EXPERIAN, defaultExperianRules(), "Loc Settings for Experian rules", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, ExperianRuleSettings.class).getRepeatedClientCheck() != null));
        settings.initProperty(LOC_LENDING_RULES_EQUIFAX, defaultEquifaxRules(), "Loc Settings for Equifax rules", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, AlfaSettings.EquifaxRuleSettings.class).getRepeatedClientCheck() != null));
        settings.initProperty(LOC_LENDING_RULES_INSTANTOR, defaultInstantorRules(), "Loc Settings for Instantor rules", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, AlfaSettings.InstantorRuleSettings.class).getRepeatedClientCheck().getNameSimilarityApproveThresholdInPercent() != null));
        settings.initProperty(LOC_CREDIT_LIMIT_SETTINGS, defaultLocCreditLimitSettings(), "Loc Credit limit settings ", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, LocSettings.LocCreditLimitSettings.class).getMaxCreditLimitAllowed() != null));
    }

    private String defaultIntegrationSettings() {
        IntegrationSettings settings = new IntegrationSettings();
        settings.setPrestoCrosscheck(IntegrationSettings.Integration.builder().maxAttempts(5).attemptTimeoutInSeconds(10).expiresInSeconds(-1).build());
        settings.setExperianRun1(IntegrationSettings.Integration.builder().maxAttempts(10).attemptTimeoutInSeconds(60).expiresInSeconds(-1).build());
        settings.setEquifaxRun1(IntegrationSettings.Integration.builder().maxAttempts(10).attemptTimeoutInSeconds(60).expiresInSeconds(-1).build());
        settings.setNordigen(IntegrationSettings.Integration.builder().maxAttempts(5).attemptTimeoutInSeconds(10).expiresInSeconds(-1).build());
        settings.setInstantor(IntegrationSettings.Integration.builder().expiresInSeconds(60 * 2).build());
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultCrosscheckRules() {
        CrosscheckRuleSettings settings = new CrosscheckRuleSettings();
        settings.setMaxDpd(20L);
        settings.setRejectOnActiveLoan(true);
        settings.setRejectOnBlacklisted(true);
        settings.setRejectOnActiveApplication(true);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultExperianRules() {
        List<String> paymentSituationBlacklist = ImmutableList.of("Entre 60 y 90 días", "Entre 90 y 120 días", "Entre 120 y 150 días", "Entre 150 y 180 días", "Mayor de 180 días", "Prejudicial", "Judicial", "Fallida");

        ExperianRuleSettings.Check repeatedClient = new ExperianRuleSettings.Check();
        repeatedClient.setMaxUnpaidDebtAmount(amount(301.00));
        repeatedClient.setMaxUnpaidDebtCount(999999L);
        repeatedClient.setExcludeDebtsWithProductoFinanciadoDescription(ImmutableList.of());
        repeatedClient.setRejectWhenSituacionPagoContains(paymentSituationBlacklist);
        repeatedClient.setExcludeDebtsWithEndDate(false);
        repeatedClient.setExcludeDebtsOlderThanDays(1000L);
        repeatedClient.setCheckResponseDays(80L);

        ExperianRuleSettings settings = new ExperianRuleSettings();
        settings.setRepeatedClientCheck(repeatedClient);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultEquifaxRules() {
        AlfaSettings.EquifaxRuleSettings.Check repeatedClient = new AlfaSettings.EquifaxRuleSettings.Check();
        repeatedClient.setMaxTotalUnpaidBalance(amount(301.00));
        repeatedClient.setMaxNumberOfCreditors(999999L);
        repeatedClient.setMaxDelincuencyDays(999999L);
        repeatedClient.setMaxNumberOfDaysOfWorstSituation(999999L);
        repeatedClient.setExcludeUnpaidBalanceOfTelco(false);
        repeatedClient.setCheckResponseDays(80L);

        AlfaSettings.EquifaxRuleSettings settings = new AlfaSettings.EquifaxRuleSettings();
        settings.setRepeatedClientCheck(repeatedClient);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultInstantorRules() {
        AlfaSettings.InstantorRuleSettings.Check repeatedClientCheck = new AlfaSettings.InstantorRuleSettings.Check();
        repeatedClientCheck.setDniSimilarityApproveThresholdInPercent(amount(100.00));
        repeatedClientCheck.setDniSimilarityManualThresholdInPercent(amount(0.00));
        repeatedClientCheck.setNameSimilarityApproveThresholdInPercent(amount(100.00));
        repeatedClientCheck.setNameSimilarityManualThresholdInPercent(amount(0.00));
        repeatedClientCheck.setRejectOnAccountNumberNoMatch(false);
        repeatedClientCheck.setMonthsAvailable(4);
        repeatedClientCheck.setTotalTransactionCount(50);
        repeatedClientCheck.setAverageAmountOfOutgoingTransactionsMonth(amount(600));
        repeatedClientCheck.setAverageAmountOfIncomingTransactionsMonth(amount(0.00));
        repeatedClientCheck.setAverageNumberOfTransactionsMonth(0);
        repeatedClientCheck.setAverageMinimumBalanceMonth(amount(-999999.00));
        repeatedClientCheck.setTotalBalance(amount(-999999.00));
        repeatedClientCheck.setCheckResponseDays(60L);

        AlfaSettings.InstantorRuleSettings settings = new AlfaSettings.InstantorRuleSettings();
        settings.setRepeatedClientCheck(repeatedClientCheck);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultLocCreditLimitSettings() {
        LocCreditLimitSettings settings = new LocCreditLimitSettings();
        settings.setMinCreditLimitAllowed(amount(500));
        settings.setMaxCreditLimitAllowed(amount(1000));
        settings.setCreditLimitCalculatedCoefficient(amount(1.6));
        return JsonUtils.writeValueAsString(settings);
    }

    @Data
    @Accessors(chain = true)
    public static class LocCreditLimitSettings {
        private BigDecimal maxCreditLimitAllowed;
        private BigDecimal minCreditLimitAllowed;
        private BigDecimal creditLimitCalculatedCoefficient;
    }


}
