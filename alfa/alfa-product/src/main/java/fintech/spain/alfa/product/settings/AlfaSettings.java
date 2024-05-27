package fintech.spain.alfa.product.settings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import fintech.BigDecimalUtils;
import fintech.ClasspathUtils;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.decision.spi.DecisionEngineStrategy;
import fintech.fintechmarket.settings.FintechMarketSettings;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.lending.ExpiredLoanApplicationReminderSettings;
import fintech.spain.alfa.product.lending.ExtensionSaleCallSettings;
import fintech.spain.alfa.product.referral.ReferralLendingCompanySettings;
import fintech.spain.notification.NotificationConfig;
import fintech.spain.scoring.settings.FintechMarketScoringSettings;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fintech.BigDecimalUtils.amount;
import static fintech.fintechmarket.settings.FintechMarketSettings.FINTECT_MARKET_SETTINGS;

@Component
public class AlfaSettings {

    public static final String ACTIVITY_SETTINGS = "ActivitySettings";
    public static final String NOTIFICATION_SETTINGS = "NotificationSettings";
    public static final String COMPANY_CONTACT_DETAILS = "CompanyContactDetails";
//    public static final String PHONE_VERIFICATION = "PhoneVerification";
//    public static final String INTEGRATION_SETTINGS = "IntegrationSettings";
//    public static final String OFFER_PROCESS_SETTINGS = "OfferProcessSettings";
//    public static final String TASK_SETTINGS = "TaskSettings";
    public static final String DISCOUNT_SETTINGS = "DiscountSettings";
//    public static final String CREDIT_LIMIT_SETTINGS = "CreditLimitSettings";
//    public static final String SCORING_SETTINGS = "ScoringSettings";
    public static final String WEALTHINESS_CALCULATION_SETTINGS = "WealthinessCalculationSettings";
//    public static final String VIVENTOR_SETTINGS = "ViventorSettings";
//    public static final String GOOD_CLIENT_SETTINGS = "GoodClientSettings";
//    public static final String UPSELL_WITHIN_NEW_LOAN_APPLICATION_SETTINGS = "UpsellWithinNewLoanApplicationSettings";
//    public static final String APPLICATION_OF_UPSELL_SETTINGS = "ApplicationOfUpsellSettings";
    public static final String LENDING_RULES_BASIC = "LendingRulesBasic";
//    public static final String LENDING_RULES_IOVATION = "LendingRulesIovation";
//    public static final String LENDING_RULES_INSTANTOR = "LendingRulesInstantor";
//    public static final String LENDING_RULES_EXPERIAN = "LendingRulesExperian";
//    public static final String LENDING_RULES_EQUIFAX = "LendingRulesEquifax";
//    public static final String LENDING_RULES_CROSSCHECK = "LendingRulesCrosscheck";
//    public static final String LOC_CLIENT_BATCH = "LocClientBatch";
//    public static final String FINTECH_MARKET_SCORING_SETTINGS = "FintechScoringSettings";
//    public static final String UNRESPONSIVE_BUREAU_SETTINGS = "UnresponsiveBureauSettings";
    public static final String ENABLE_DNI_UPLOADING = "Enable_dni_uploading";
//    public static final String ID_DOCUMENT_VALIDITY_SETTINGS = "CheckValidIdDocumentRules";
//    public static final String EXTENSION_SALE_CALL_SETTINGS = "ExtensionSaleCallSettings";
    public static final String REFERRAL_LENDING_COMPANY_SETTINGS = "ReferralLendingCompanySettings";
//    public static final String EXPIRED_APPLICATION_REMINDER_SETTINGS = "ExpiredApplicationReminderSettings";
    public static final String DECISION_ENGINE_STRATEGIES_SCENARIO = "DeStrategiesScenario";
//    public static final String INSTANTOR_SETTINGS = "InstantorSettings";
//    public static final String DOWJONES_SETTINGS = "DowjonesSettings";
//    public static final String IOVATION_SETTINGS = "IovationSettings";


    @Autowired
    private SettingsService settings;

    public void setUp() {
        settings.initProperty(ACTIVITY_SETTINGS, defaultActivitySettings(), "Settings for CRM activities", v -> Validate.isTrue(!StringUtils.isBlank(v)));
        settings.initProperty(NOTIFICATION_SETTINGS, defaultNotificationSettings(), "Settings for notification sending", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, NotificationSettings.class).getCustomerService() != null && JsonUtils.readValue(v, NotificationSettings.class).getDebtCollection() != null));
        settings.initProperty(COMPANY_CONTACT_DETAILS, defaultCompanyContactDetails(), "Settings for company contact details", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, CompanyContactDetails.class).getName() != null));
//        settings.initProperty(PHONE_VERIFICATION, defaultPhoneVerification(), "Setting for phone verification", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, PhoneVerificationSettings.class).getSmsCodeLength() >= 0L));
//        settings.initProperty(INTEGRATION_SETTINGS, defaultIntegrationSettings(), "Settings for 3rd party integrations", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, IntegrationSettings.class).getInstantor() != null));
        settings.initProperty(LENDING_RULES_BASIC, defaultBasicRules(), "Setting for basisc rules", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, BasicRuleSettings.class) != null));
//        settings.initProperty(LENDING_RULES_IOVATION, defaultIovationRules(), "Settings for Iovation rules", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, IovationRuleSettings.class).getRejectOnResults() != null));
//        settings.initProperty(LENDING_RULES_INSTANTOR, defaultInstantorRules(), "Settings for Instantor rules", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, InstantorRuleSettings.class).getNewClientCheck().getNameSimilarityApproveThresholdInPercent() != null));
//        settings.initProperty(LENDING_RULES_EXPERIAN, defaultExperianRules(), "Settings for Experian rules", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, ExperianRuleSettings.class).getNewClientCheck() != null));
//        settings.initProperty(LENDING_RULES_EQUIFAX, defaultEquifaxRules(), "Settings for Equifax rules", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, EquifaxRuleSettings.class).getNewClientCheck() != null));
//        settings.initProperty(LENDING_RULES_CROSSCHECK, defaultCrosscheckRules(), "Settings for Presto crosscheck", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, CrosscheckRuleSettings.class).getMaxDpd() != null));
//        settings.initProperty(OFFER_PROCESS_SETTINGS, defaultOfferProcessSettings(), "Settings for loan offer process", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, OfferProcessSettings.class).getOfferCallDelayInMinutes() >= 0));
//        settings.initProperty(TASK_SETTINGS, defaultTaskSettings(), "Settings for tasks", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, TaskSettings.class).getDefaultTaskExpirationInDays() >= 0));
//        settings.initProperty(CREDIT_LIMIT_SETTINGS, defaultCreditLimitSettings(), "Settings for credit limit value", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, CreditLimitSettings.class).getFirstLoan() != null));
//        settings.initProperty(SCORING_SETTINGS, defaultScoringSettings(), "Settings for client scoring", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, ScoringSettings.class).getNewClient().getDedicatedUpperThreshold() >= 0));
        settings.initProperty(WEALTHINESS_CALCULATION_SETTINGS, defaultWealthinessSettings(), "Settings for Wealthiness calculation", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, WealthinessCalculationSettings.class).getMonthsToCheck() > 0));
//        settings.initProperty(VIVENTOR_SETTINGS, defaultViventorSettings(), "Settings for Viventor integration", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, ViventorSettings.class).getInterestRate() != null));
        settings.initProperty(DISCOUNT_SETTINGS, defaultDiscountSettings(), "Settings for client discounts", v -> Validate.isTrue(v != null && !JsonUtils.readValue(v, DiscountSettings.class).getItems().isEmpty()));
//        settings.initProperty(GOOD_CLIENT_SETTINGS, defaultGoodClientSettings(), "Conditions of good client", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, GoodClientSettings.class).getMinNumberOfPaidLoans() >= 0));
//        settings.initProperty(UPSELL_WITHIN_NEW_LOAN_APPLICATION_SETTINGS, defaultUpsellWithinNewLoanApplicationSettings(), "Conditions of upsell", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, UpsellWithinNewLoanApplicationSettings.class) != null));
//        settings.initProperty(APPLICATION_OF_UPSELL_SETTINGS, defaultApplicationOfUpsellSettings(), "Conditions of upsell", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, ApplicationOfUpsellSettings.class) != null));
//        settings.initProperty(LOC_CLIENT_BATCH, defaultLocClientBatchSettings(), "Conditions for running LoC WF", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, LocClientBatch.class) != null));
//        settings.initProperty(UNRESPONSIVE_BUREAU_SETTINGS, defaultUnresponsiveBureauSettings(), "Settings for unresponsive bureau", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, UnresponsiveBureauSettings.class) != null));
//        settings.initProperty(ID_DOCUMENT_VALIDITY_SETTINGS, defaultIdDocumentValiditySettings(), "Settings for ID document checks", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, IdDocumentValiditySettings.class) != null));
        settings.initProperty(ENABLE_DNI_UPLOADING, false, "Enable include AML doc upload into workflow feature", aBoolean -> settings.initProperty(DECISION_ENGINE_STRATEGIES_SCENARIO, defaultScenarioStrategiesSettings(), "Settings for get strategies scenario values from decision-engine", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, ScenarioStrategiesSettings.class) != null)));
//        settings.initProperty(EXTENSION_SALE_CALL_SETTINGS, defaultExtensionSaleCallSettings(), "Extension sale call configuration", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, ExtensionSaleCallSettings.class).getDpd() <= 0));
//        settings.initProperty(REFERRAL_LENDING_COMPANY_SETTINGS, defaultReferralLendingCompanySettings(), "Referral lending company info", v -> Validate.isTrue(v != null && JsonUtils.readValue(v, ReferralLendingCompanySettings.class).getLink() != null));
//        settings.initProperty(FINTECH_MARKET_SCORING_SETTINGS, defaultFintechMarketScoringSettings(), "Settings for fintech market scoring", v -> Validate.isTrue(!StringUtils.isBlank(v)));
//        settings.initProperty(EXPIRED_APPLICATION_REMINDER_SETTINGS, defaultExpiredApplicationReminderSettings(), "Settings for expired applications reminder", v -> Validate.isTrue(!StringUtils.isBlank(v)));

//        settings.initProperty(FINTECT_MARKET_SETTINGS, defaultFintechMarketSettings(), "Settings for fintech market client", v -> Validate.isTrue(!StringUtils.isBlank(v)));
//        settings.initProperty(INSTANTOR_SETTINGS, defaultInstantorSettings(), "Settings for fake instantor response", v -> Validate.isTrue(!StringUtils.isBlank(v)));
//        settings.initProperty(DOWJONES_SETTINGS, defaultDowjonesSettings(), "Dowjones settings in the workflows", v -> Validate.isTrue(!StringUtils.isBlank(v)));
//        settings.initProperty(IOVATION_SETTINGS, defaultIovationSettings(), "Integration Iovation settings", v -> Validate.isTrue(!StringUtils.isBlank(v)));
    }

    @SneakyThrows
    private String defaultInstantorSettings() {
        InputStream input = new ClassPathResource("instantor-insight-fake.json").getInputStream();
        return IOUtils.toString(input, StandardCharsets.UTF_8);
    }

    private String defaultFintechMarketScoringSettings() {
        return JsonUtils.writeValueAsString(
            FintechMarketScoringSettings.builder()
                .mainScenarioKey("card1")
                .build());
    }

    private String defaultFintechMarketSettings() {
        return JsonUtils.writeValueAsString(
            FintechMarketSettings.builder()
                .brand("alfa")
                .build());
    }

    private String defaultCrosscheckRules() {
        CrosscheckRuleSettings settings = new CrosscheckRuleSettings();
        settings.setMaxDpd(90L);
        settings.setRejectOnActiveLoan(true);
        settings.setRejectOnBlacklisted(true);
        settings.setRejectOnActiveApplication(true);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultActivitySettings() {
        return ClasspathUtils.resourceToString("default-settings/activity-settings.json");
    }

    private String defaultNotificationSettings() {
        NotificationConfig customerService = new NotificationConfig();
        customerService.setEmailFrom("info@alfa.es");
        customerService.setEmailFromName("ALFA");
        customerService.setSmsSenderId("Alfa");

        NotificationConfig debtCollection = new NotificationConfig();
        debtCollection.setEmailFrom("cobros@alfa.es");
        debtCollection.setEmailFromName("ALFA");
        debtCollection.setSmsSenderId("Alfa");

        NotificationConfig preLegal = new NotificationConfig();
        preLegal.setEmailFrom("prejudicial@alfa.es");
        preLegal.setEmailFromName("ALFA");
        preLegal.setSmsSenderId("Alfa");

        NotificationConfig legal = new NotificationConfig();
        legal.setEmailFrom("judicial@alfa.es");
        legal.setEmailFromName("ALFA");
        legal.setSmsSenderId("Alfa");

        NotificationConfig extraLegal = new NotificationConfig();
        extraLegal.setEmailFrom("extrajudicial@alfa.es");
        extraLegal.setEmailFromName("ALFA");
        extraLegal.setSmsSenderId("Alfa");

        NotificationConfig marketing = new NotificationConfig();
        marketing.setEmailFrom("news@alfa.es");
        marketing.setEmailFromName("ALFA");
        marketing.setSmsSenderId("Alfa");
        marketing.setEmailReplyTo("news@alfa.es");

        NotificationSettings settings = new NotificationSettings();
        settings.setPaymentSpecialLinkExpiresInDays(5);
        settings.setCustomerService(customerService);
        settings.setDebtCollection(debtCollection);
        settings.setPreLegal(preLegal);
        settings.setPreLegal(legal);
        settings.setExtraLegal(extraLegal);
        settings.setMarketingService(marketing);

        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultCompanyContactDetails() {
        CompanyContactDetails settings = new CompanyContactDetails();
        settings.setName("Alfa S.L.U");
        settings.setNumber("B-98378201");
        settings.setAddressLine1("Calle Tarragona 161, planta 13");
        settings.setAddressLine2("08014 Barcelona");
        settings.setPhone("935475888");
        settings.setEmail("info@alfa.es");
        settings.setWebSite("www.alfa.es");
        settings.setIncomingSmsNumber("911067476");
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultBasicRules() {
        BasicRuleSettings.Check newClientCheck = new BasicRuleSettings.Check()
            .setApplicationCountWithin30DaysFirstLoan(7)
            .setApplicationCountWithin30DaysRepeatedLoan(7)
            .setRejectionCountIn30Days(4)
            .setRejectionCountIn7Days(3)
            .setFeePenaltyPaid(amount(25))
            .setPrincipalSold(amount(0))
            .setDaysSinceLastApplicationRejection(30)
            .setLastLoanApplicationRejectionReason(Lists.newArrayList(
                UnderwritingWorkflows.Activities.EQUIFAX_RULES_RUN_1,
                UnderwritingWorkflows.Activities.EXPERIAN_RULES_RUN_1,
                UnderwritingWorkflows.Activities.EQUIFAX_RULES_RUN_2,
                UnderwritingWorkflows.Activities.EXPERIAN_RULES_RUN_2
            ))
            .setTotalOverdueDays(100)
            .setMaxOverdueDays(45)
            .setMaxOverdueDaysInLast12Months(30)
            .setLastLoanOverdueDays(20)
            .setMinAge(21)
            .setMaxAge(69);

        BasicRuleSettings.Check repeatedClientCheck = new BasicRuleSettings.Check()
            .setApplicationCountWithin30DaysFirstLoan(7)
            .setApplicationCountWithin30DaysRepeatedLoan(7)
            .setRejectionCountIn30Days(4)
            .setRejectionCountIn7Days(3)
            .setFeePenaltyPaid(amount(25))
            .setPrincipalSold(amount(0))
            .setDaysSinceLastApplicationRejection(30)
            .setLastLoanApplicationRejectionReason(Lists.newArrayList(
                UnderwritingWorkflows.Activities.EQUIFAX_RULES_RUN_1,
                UnderwritingWorkflows.Activities.EXPERIAN_RULES_RUN_1,
                UnderwritingWorkflows.Activities.EQUIFAX_RULES_RUN_2,
                UnderwritingWorkflows.Activities.EXPERIAN_RULES_RUN_2
            ))
            .setTotalOverdueDays(100)
            .setMaxOverdueDays(45)
            .setMaxOverdueDaysInLast12Months(30)
            .setLastLoanOverdueDays(20)
            .setMinAge(21)
            .setMaxAge(69);

        BasicRuleSettings settings = new BasicRuleSettings();
        settings.setNewClientCheck(newClientCheck);
        settings.setRepeatedClientCheck(repeatedClientCheck);

        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultPhoneVerification() {
        PhoneVerificationSettings.MaxAttempts maxAttempts = new PhoneVerificationSettings.MaxAttempts();
        maxAttempts.setMaxAttemptsCount(5);
        maxAttempts.setMaxAttemptsCountExpiresInMinutes(5);
        PhoneVerificationSettings settings = new PhoneVerificationSettings();
        settings.setMaxAttempts(maxAttempts);
        settings.setSmsCodeLength(0);
        settings.setSmsCodeExpiresInMinutes(15);
        settings.setReVerificationPeriod(12);
        settings.setWorkflowStepExpirationInMinutes(1440); // 24H
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultIntegrationSettings() {
        IntegrationSettings settings = new IntegrationSettings();
        settings.setPrestoCrosscheck(IntegrationSettings.Integration.builder().maxAttempts(5).attemptTimeoutInSeconds(10).expiresInSeconds(-1).build());
        settings.setExperianRun1(IntegrationSettings.Integration.builder().maxAttempts(1).attemptTimeoutInSeconds(5).expiresInSeconds(-1).build());
        settings.setEquifaxRun1(IntegrationSettings.Integration.builder().maxAttempts(1).attemptTimeoutInSeconds(5).expiresInSeconds(-1).build());
        settings.setExperianRun2(IntegrationSettings.Integration.builder().maxAttempts(5).attemptTimeoutInSeconds(10).expiresInSeconds(-1).build());
        settings.setEquifaxRun2(IntegrationSettings.Integration.builder().maxAttempts(5).attemptTimeoutInSeconds(10).expiresInSeconds(-1).build());
        settings.setNordigen(IntegrationSettings.Integration.builder().maxAttempts(5).attemptTimeoutInSeconds(10).expiresInSeconds(-1).build());
        settings.setScoring(IntegrationSettings.Integration.builder().maxAttempts(5).attemptTimeoutInSeconds(10).expiresInSeconds(-1).build());
        settings.setIovation(IntegrationSettings.Integration.builder().maxAttempts(5).attemptTimeoutInSeconds(10).expiresInSeconds(-1).build());
        settings.setInstantor(IntegrationSettings.Integration.builder().expiresInSeconds(60 * 2).maxAttempts(4).attemptTimeoutInSeconds(10).build());
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultIovationRules() {
        IovationRuleSettings settings = new IovationRuleSettings();
        settings.getRejectOnResults().add("D");
        settings.setAutoApproveRepeaters(true);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultEquifaxRules() {
        EquifaxRuleSettings.Check newClient = new EquifaxRuleSettings.Check();
        newClient.setMaxTotalUnpaidBalance(amount(50.00));
        newClient.setMaxNumberOfCreditors(10000L);
        newClient.setMaxDelincuencyDays(10000L);
        newClient.setMaxNumberOfDaysOfWorstSituation(10000L);
        newClient.setExcludeUnpaidBalanceOfTelco(true);

        EquifaxRuleSettings.Check repeatedClient = new EquifaxRuleSettings.Check();
        repeatedClient.setMaxTotalUnpaidBalance(amount(300.00));
        repeatedClient.setMaxNumberOfCreditors(10000L);
        repeatedClient.setMaxDelincuencyDays(10000L);
        repeatedClient.setMaxNumberOfDaysOfWorstSituation(10000L);
        repeatedClient.setExcludeUnpaidBalanceOfTelco(true);

        EquifaxRuleSettings settings = new EquifaxRuleSettings();
        settings.setNewClientCheck(newClient);
        settings.setRepeatedClientCheck(repeatedClient);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultExperianRules() {
        List<String> paymentSituationBlacklist = ImmutableList.of("Entre 60 y 90 días", "Entre 90 y 120 días", "Entre 120 y 150 días", "Entre 150 y 180 días", "Mayor de 180 días", "Prejudicial", "Judicial", "Fallida");

        ExperianRuleSettings.Check newClient = new ExperianRuleSettings.Check();
        newClient.setMaxUnpaidDebtAmount(amount(50.00));
        newClient.setMaxUnpaidDebtCount(10000L);
        newClient.setExcludeDebtsWithProductoFinanciadoDescription(ImmutableList.of("Telecomunicaciones"));
        newClient.setRejectWhenSituacionPagoContains(paymentSituationBlacklist);
        newClient.setExcludeDebtsWithEndDate(true);
        newClient.setExcludeDebtsOlderThanDays(2000L);
        newClient.setCheckResponseDays(80L);

        ExperianRuleSettings.Check repeatedClient = new ExperianRuleSettings.Check();
        repeatedClient.setMaxUnpaidDebtAmount(amount(300.00));
        repeatedClient.setMaxUnpaidDebtCount(10000L);
        repeatedClient.setExcludeDebtsWithProductoFinanciadoDescription(ImmutableList.of("Telecomunicaciones"));
        repeatedClient.setRejectWhenSituacionPagoContains(paymentSituationBlacklist);
        repeatedClient.setExcludeDebtsWithEndDate(true);
        repeatedClient.setExcludeDebtsOlderThanDays(2000L);
        repeatedClient.setCheckResponseDays(80L);

        ExperianRuleSettings settings = new ExperianRuleSettings();
        settings.setNewClientCheck(newClient);
        settings.setRepeatedClientCheck(repeatedClient);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultOfferProcessSettings() {
        OfferProcessSettings settings = new OfferProcessSettings();
        settings.setSmsNotificationDelayInMinutes(15);
        settings.setOfferCallDelayInMinutes(30);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultTaskSettings() {
        TaskSettings settings = new TaskSettings();
        settings.setDefaultTaskExpirationInDays(14);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultInstantorRules() {
        InstantorRuleSettings.Check newClientCheck = new InstantorRuleSettings.Check();
        newClientCheck.setDniSimilarityApproveThresholdInPercent(amount(100.00));
        newClientCheck.setDniSimilarityManualThresholdInPercent(amount(66.00));
        newClientCheck.setNameSimilarityApproveThresholdInPercent(amount(100.00));
        newClientCheck.setNameSimilarityManualThresholdInPercent(amount(0.00));
        newClientCheck.setRejectOnAccountNumberNoMatch(true);
        newClientCheck.setMonthsAvailable(4);
        newClientCheck.setTotalTransactionCount(100);
        newClientCheck.setAverageAmountOfOutgoingTransactionsMonth(amount(600));
        newClientCheck.setAverageAmountOfIncomingTransactionsMonth(amount(701));
        newClientCheck.setAverageNumberOfTransactionsMonth(17);
        newClientCheck.setAverageMinimumBalanceMonth(amount(-60));
        newClientCheck.setTotalBalance(amount(100));
        newClientCheck.setAmountOfLoansThisMonthRejectMin(amount(-1));
        newClientCheck.setAmountOfLoansThisMonthRejectMax(amount(0));
        newClientCheck.setAmountOfLoansLastMonthRejectMin(amount(0.01));
        newClientCheck.setAmountOfLoansLastMonthRejectMax(amount(450));

        InstantorRuleSettings.Check repeatedClientCheck = new InstantorRuleSettings.Check();
        repeatedClientCheck.setDniSimilarityApproveThresholdInPercent(amount(100.00));
        repeatedClientCheck.setDniSimilarityManualThresholdInPercent(amount(66.00));
        repeatedClientCheck.setNameSimilarityApproveThresholdInPercent(amount(100.00));
        repeatedClientCheck.setNameSimilarityManualThresholdInPercent(amount(0.00));
        repeatedClientCheck.setRejectOnAccountNumberNoMatch(true);
        repeatedClientCheck.setMonthsAvailable(4);
        repeatedClientCheck.setTotalTransactionCount(100);
        repeatedClientCheck.setAverageAmountOfOutgoingTransactionsMonth(amount(600));
        repeatedClientCheck.setAverageAmountOfIncomingTransactionsMonth(amount(701));
        repeatedClientCheck.setAverageNumberOfTransactionsMonth(17);
        repeatedClientCheck.setAverageMinimumBalanceMonth(amount(-60));
        repeatedClientCheck.setTotalBalance(amount(100));
        repeatedClientCheck.setAmountOfLoansThisMonthRejectMin(amount(-1));
        repeatedClientCheck.setAmountOfLoansThisMonthRejectMax(amount(0));
        repeatedClientCheck.setAmountOfLoansLastMonthRejectMin(amount(0.01));
        repeatedClientCheck.setAmountOfLoansLastMonthRejectMax(amount(450));

        InstantorRuleSettings settings = new InstantorRuleSettings();
        settings.setNewClientCheck(newClientCheck);
        settings.setRepeatedClientCheck(repeatedClientCheck);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultCreditLimitSettings() {
        CreditLimitSettings settings = new CreditLimitSettings()
            .setMaxCreditLimit(amount(600))
            .setDefaultCreditLimit(amount(300))
            .setScenario("credit_limit")
            .setFirstLoan(new CreditLimitSettings.FirstLoan()
                .setAgeRanges(Lists.newArrayList(
                    new CreditLimitSettings.Age()
                        .setToInclusive(24L)
                        .setAverageAmountOfOutgoingTransactionsMonthRanges(Lists.newArrayList(
                            new CreditLimitSettings.AverageAmountOfOutgoingTransactionsMonth()
                                .setToInclusive(amount(1199.99))
                                .setCreditLimit(amount(100)),
                            new CreditLimitSettings.AverageAmountOfOutgoingTransactionsMonth()
                                .setFromInclusive(amount(1200.00))
                                .setToInclusive(amount(2200.00))
                                .setCreditLimit(amount(150)),
                            new CreditLimitSettings.AverageAmountOfOutgoingTransactionsMonth()
                                .setFromInclusive(amount(2200.01))
                                .setCreditLimit(amount(200))
                        )),
                    new CreditLimitSettings.Age()
                        .setFromInclusive(25L)
                        .setToInclusive(35L)
                        .setAverageAmountOfOutgoingTransactionsMonthRanges(Lists.newArrayList(
                            new CreditLimitSettings.AverageAmountOfOutgoingTransactionsMonth()
                                .setToInclusive(amount(1199.99))
                                .setCreditLimit(amount(100)),
                            new CreditLimitSettings.AverageAmountOfOutgoingTransactionsMonth()
                                .setFromInclusive(amount(1200.00))
                                .setToInclusive(amount(2200.00))
                                .setCreditLimit(amount(200)),
                            new CreditLimitSettings.AverageAmountOfOutgoingTransactionsMonth()
                                .setFromInclusive(amount(2200.01))
                                .setCreditLimit(amount(250))
                        )),
                    new CreditLimitSettings.Age()
                        .setFromInclusive(36L)
                        .setAverageAmountOfOutgoingTransactionsMonthRanges(Lists.newArrayList(
                            new CreditLimitSettings.AverageAmountOfOutgoingTransactionsMonth()
                                .setToInclusive(amount(1199.99))
                                .setCreditLimit(amount(100)),
                            new CreditLimitSettings.AverageAmountOfOutgoingTransactionsMonth()
                                .setFromInclusive(amount(1200.00))
                                .setToInclusive(amount(2200.00))
                                .setCreditLimit(amount(250)),
                            new CreditLimitSettings.AverageAmountOfOutgoingTransactionsMonth()
                                .setFromInclusive(amount(2200.01))
                                .setCreditLimit(amount(300))
                        ))
                )));
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultScoringSettings() {
        ScoringSettings.Config newClient = new ScoringSettings.Config();
        newClient.setDedicatedLowerThreshold(0.5);
        newClient.setDedicatedUpperThreshold(0.9);
        newClient.setAvgDeviationThresholdOfGreenScore(100);
        newClient.setAvgDeviationThresholdOfRedScore(0);

        ScoringSettings.Config repeatedClient = new ScoringSettings.Config();
        repeatedClient.setDedicatedLowerThreshold(0.4);
        repeatedClient.setDedicatedUpperThreshold(0.8);
        repeatedClient.setAvgDeviationThresholdOfGreenScore(100);
        repeatedClient.setAvgDeviationThresholdOfRedScore(0);

        ScoringSettings settings = new ScoringSettings();
        settings.setNewClient(newClient);
        settings.setRepeatedClient(repeatedClient);

        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultWealthinessSettings() {
        final String salary = "Salary";
        final String pension = "Pension";
        final String loans = "Loans";
        final String other = "Cash,Unemp,Transf,Gambl";

        WealthinessCalculationSettings settings = new WealthinessCalculationSettings();
        settings.setMonthsToCheck(2);

        List<String> allCategories = ImmutableList.of(salary, pension, loans, other);

        {
            WealthinessCalculationSettings.Threshold green = new WealthinessCalculationSettings.Threshold();
            green.setScoringBuckets(ImmutableList.of(Resolutions.GREEN));
            green.setApproveThreshold(345.640475d);
            green.setRejectThreshold(-10000.00);
            green.setCategories(allCategories);
            settings.getThresholds().add(green);
        }

        {
            WealthinessCalculationSettings.Threshold yellow = new WealthinessCalculationSettings.Threshold();
            yellow.setScoringBuckets(ImmutableList.of(Resolutions.YELLOW));
            yellow.setApproveThreshold(345.640475d * 2);
            yellow.setRejectThreshold(0.00);
            yellow.setCategories(allCategories);
            settings.getThresholds().add(yellow);
        }

        settings.setFragmentToExcludeFromTransactionDetails(ImmutableList.of("Description:", "Type: Transferencias;"));

        {
            WealthinessCalculationSettings.Category category = new WealthinessCalculationSettings.Category();
            category.setName(salary);
            category.setWeightInPercent(50.339);
            category.setNordigenCategories(ImmutableList.of(85, 9191));
            settings.getCategories().add(category);
        }
        {
            WealthinessCalculationSettings.Category category = new WealthinessCalculationSettings.Category();
            category.setName(pension);
            category.setWeightInPercent(31.325);
            category.setNordigenCategories(ImmutableList.of(5));
            settings.getCategories().add(category);
        }
        {
            WealthinessCalculationSettings.Category category = new WealthinessCalculationSettings.Category();
            category.setName(loans);
            category.setWeightInPercent(-15.344);
            category.setNordigenCategories(ImmutableList.of(100, 80, 91, 302, 93, 795, 80, 82, 94, 562, 95, 96, 796));
            settings.getCategories().add(category);
        }
        {
            WealthinessCalculationSettings.Category category = new WealthinessCalculationSettings.Category();
            category.setName(other);
            category.setWeightInPercent(9.039);
            category.setNordigenCategories(ImmutableList.of(20, 19, 83, 86, 236, 476, 409, 408, 711, 714, 548, 549, 551, 359, 23, 84, 918, 917));
            settings.getCategories().add(category);
        }

        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultViventorSettings() {
        ViventorSettings settings = new ViventorSettings();
        settings.setInterestRate(amount(10.50));
        settings.setBuyback(true);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultDiscountSettings() {
        DiscountSettings settings = new DiscountSettings();
        settings.setDpdThreshold(2L);
        settings.setItems(ImmutableList.of(
            new DiscountSettings.Item().setTotalRepaidPrincipalFromInclusive(amount(0)).setTotalRepaidPrincipalToExclusive(amount(300)).setDiscountPercent(amount(0)),
            new DiscountSettings.Item().setTotalRepaidPrincipalFromInclusive(amount(300)).setTotalRepaidPrincipalToExclusive(amount(750)).setDiscountPercent(amount(15)),
            new DiscountSettings.Item().setTotalRepaidPrincipalFromInclusive(amount(750)).setTotalRepaidPrincipalToExclusive(amount(1350)).setDiscountPercent(amount(20)),
            new DiscountSettings.Item().setTotalRepaidPrincipalFromInclusive(amount(1350)).setTotalRepaidPrincipalToExclusive(amount(1_000_000)).setDiscountPercent(amount(25))
        ));
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultGoodClientSettings() {
        GoodClientSettings settings = new GoodClientSettings();
        settings.setMaxDpd(59);
        settings.setMonthsToCheckDpd(12);
        settings.setMinNumberOfPaidLoans(4);
        settings.setMinRepaidPrincipalAmount(amount(301));
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultUpsellWithinNewLoanApplicationSettings() {
        UpsellWithinNewLoanApplicationSettings settings = new UpsellWithinNewLoanApplicationSettings()
            .setMaxPrincipalRepaid(amount(600))
            .setMaxOverdueDays(20)
            .setMinAge(31L)
            .setMaxCreditLimitMinusRequestedAtLeast(amount(100));
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultApplicationOfUpsellSettings() {
        ApplicationOfUpsellSettings settings = new ApplicationOfUpsellSettings()
            .setMinDaysSinceLoanIssue(3)
            .setMinDaysBeforeEndOfTerm(10)
            .setMaxDaysUpsellOfferCallTaskActive(2L)
            .setMaxDaysUpsellApproveOfferActivityActive(2L);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultUnresponsiveBureauSettings() {
        UnresponsiveBureauSettings settings = new UnresponsiveBureauSettings();
        settings.setMaxDaysSinceLastLoanPaid(60);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultIdDocumentValiditySettings() {
        IdDocumentValiditySettings settings = new IdDocumentValiditySettings();
        settings.setRequestIdUploadForFirstLoan(false);
        settings.setRequestIdUploadForSecondAndLaterLoan(false);
        settings.setDocExpirationAllowedPeriodInDays(30);
        settings.setIdDocumentManualTextExtractionExpiryInDays(3L);
        settings.setIdDocumentManualValidationExpiryInDays(3L);
        settings.setIdValidationScenarioKey(DecisionEngineStrategy.ID_VALIDATION_SCENARIO);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultLocClientBatchSettings() {
        return JsonUtils.writeValueAsString(
            new LocClientBatch().setMaxWorkflowsToRun(50)
        );
    }

    private String defaultExtensionSaleCallSettings() {
        ExtensionSaleCallSettings settings = new ExtensionSaleCallSettings();
        settings.setDpd(-3);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultReferralLendingCompanySettings() {
        ReferralLendingCompanySettings settings = new ReferralLendingCompanySettings();
        settings.setLink("http://tracking.adcredy.com/SHiN?aff_sub={{application.number}}");
        settings.setName("Credy");
        settings.setExcludeTraffic(ImmutableList.of("Crezy", "LeadsForFinance"));
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultExpiredApplicationReminderSettings() {
        ExpiredLoanApplicationReminderSettings settings = new ExpiredLoanApplicationReminderSettings();
        settings.setHours(2);
        settings.setNewClients(true);
        settings.setRepeatedClients(true);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultScenarioStrategiesSettings() {
        ScenarioStrategiesSettings settings = new ScenarioStrategiesSettings();
        settings.setScenarioName(DecisionEngineStrategy.STRATEGY_SCENARIO);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultDowjonesSettings() {
        DowjonesSettings settings = new DowjonesSettings();
        settings.setEnabled(true);
        return JsonUtils.writeValueAsString(settings);
    }

    private String defaultIovationSettings() {
        IovationSettings settings = new IovationSettings();
        settings.setEnabled(true);
        return JsonUtils.writeValueAsString(settings);
    }

    @Data
    public static class IovationSettings {
        private boolean enabled;
    }

    @Data
    public static class DowjonesSettings {
        private boolean enabled;
    }

    @Data
    public static class OfferProcessSettings {
        private int smsNotificationDelayInMinutes;
        private int offerCallDelayInMinutes;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IntegrationSettings {

        private Integration prestoCrosscheck;
        private Integration experianRun1;
        private Integration equifaxRun1;
        private Integration experianRun2;
        private Integration equifaxRun2;
        private Integration nordigen;
        private Integration scoring;
        private Integration instantor;
        private Integration iovation;

        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        @Data
        public static class Integration {
            private Integer maxAttempts;
            private Integer attemptTimeoutInSeconds;
            private Integer expiresInSeconds;
        }
    }

    @Data
    public static class CompanyContactDetails {
        private String name;
        private String number;
        private String addressLine1;
        private String addressLine2;
        private String phone;
        private String email;
        private String webSite;
        private String incomingSmsNumber;
    }

    @Data
    public static class NotificationSettings {

        private Integer paymentSpecialLinkExpiresInDays;
        private NotificationConfig customerService;
        private NotificationConfig debtCollection;
        private NotificationConfig preLegal;
        private NotificationConfig legal;
        private NotificationConfig extraLegal;
        private NotificationConfig marketingService;

    }

    @Data
    @Accessors(chain = true)
    public static class BasicRuleSettings {
        private BasicRuleSettings.Check newClientCheck;
        private BasicRuleSettings.Check repeatedClientCheck;

        @Data
        public static class Check {
            private int applicationCountWithin30DaysFirstLoan;
            private int applicationCountWithin30DaysRepeatedLoan;
            private int rejectionCountIn30Days;
            private int rejectionCountIn7Days;
            private BigDecimal principalSold;
            private BigDecimal feePenaltyPaid;
            private long daysSinceLastApplicationRejection;
            private List<String> lastLoanApplicationRejectionReason = Lists.newArrayList();
            private int totalOverdueDays;
            private int maxOverdueDays;
            private int maxOverdueDaysInLast12Months;
            private int lastLoanOverdueDays;
            private long minAge;
            private long maxAge;
        }
    }

    @Data
    public static class IovationRuleSettings {
        private List<String> rejectOnResults = new ArrayList<>();
        private boolean autoApproveRepeaters;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class InstantorRuleSettings {
        private InstantorRuleSettings.Check newClientCheck;
        private InstantorRuleSettings.Check repeatedClientCheck;

        @Data
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Check {
            private boolean rejectOnAccountNumberNoMatch;
            private BigDecimal dniSimilarityApproveThresholdInPercent;
            private BigDecimal dniSimilarityManualThresholdInPercent;
            private BigDecimal nameSimilarityApproveThresholdInPercent;
            private BigDecimal nameSimilarityManualThresholdInPercent;
            private int monthsAvailable;
            private int totalTransactionCount;
            private BigDecimal averageAmountOfOutgoingTransactionsMonth;
            private BigDecimal averageAmountOfIncomingTransactionsMonth;
            private int averageNumberOfTransactionsMonth;
            private BigDecimal averageMinimumBalanceMonth;
            private BigDecimal totalBalance;
            private Long checkResponseDays;
            private BigDecimal amountOfLoansLastMonthRejectMin;
            private BigDecimal amountOfLoansLastMonthRejectMax;
            private BigDecimal amountOfLoansThisMonthRejectMin;
            private BigDecimal amountOfLoansThisMonthRejectMax;
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ExperianRuleSettings {
        private Check newClientCheck;
        private Check repeatedClientCheck;

        @Data
        public static class Check {
            private BigDecimal maxUnpaidDebtAmount;
            private Long maxUnpaidDebtCount;
            private List<String> excludeDebtsWithProductoFinanciadoDescription = new ArrayList<>();
            private boolean excludeDebtsWithEndDate;
            private Long excludeDebtsOlderThanDays;
            private List<String> rejectWhenSituacionPagoContains = new ArrayList<>();
            private Long checkResponseDays;
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EquifaxRuleSettings {
        private Check newClientCheck;
        private Check repeatedClientCheck;

        @Data
        public static class Check {
            private BigDecimal maxTotalUnpaidBalance;
            private Long maxNumberOfCreditors;
            private Long maxDelincuencyDays;
            private Long maxNumberOfDaysOfWorstSituation;
            private boolean excludeUnpaidBalanceOfTelco;
            private Long checkResponseDays;
        }
    }

    @Data
    public static class CrosscheckRuleSettings {
        private boolean rejectOnActiveLoan;
        private boolean rejectOnBlacklisted;
        private boolean rejectOnActiveApplication;
        private Long maxDpd;
    }

    @Data
    public static class PhoneVerificationSettings {
        private int smsCodeLength;
        private int smsCodeExpiresInMinutes;
        private Integer reVerificationPeriod;
        private int workflowStepExpirationInMinutes;

        private MaxAttempts maxAttempts;

        @Data
        public static class MaxAttempts {
            private int maxAttemptsCount;
            private int maxAttemptsCountExpiresInMinutes;
        }
    }

    @Data
    public static class TaskSettings {
        int defaultTaskExpirationInDays;
    }

    @Data
    public static class ScoringSettings {

        private Config newClient;
        private Config repeatedClient;

        @Data
        public static class Config {
            private double dedicatedLowerThreshold;
            private double dedicatedUpperThreshold;

            private double avgDeviationThresholdOfGreenScore;
            private double avgDeviationThresholdOfRedScore;
        }
    }

    @Data
    public static class WealthinessCalculationSettings {

        private int monthsToCheck;

        private List<Category> categories = new ArrayList<>();
        private List<Threshold> thresholds = new ArrayList<>();
        private List<String> fragmentToExcludeFromTransactionDetails = new ArrayList<>();

        @Data
        public static class Category {
            private String name;
            private List<Integer> nordigenCategories = new ArrayList<>();
            private double weightInPercent;
        }

        @Data
        public static class Threshold {
            private List<String> categories = new ArrayList<>();
            private List<String> scoringBuckets = new ArrayList<>();
            private double rejectThreshold;
            private double approveThreshold;
        }

    }

    @Data
    public static class ViventorSettings {
        private BigDecimal interestRate;
        private int gracePeriodDays = 0;
        private boolean buyback = true;
        private boolean loanAutoSendEnabled = false;
    }

    @Data
    @Accessors(chain = true)
    public static class CreditLimitSettings {

        private BigDecimal defaultCreditLimit;
        private BigDecimal maxCreditLimit;
        private String scenario;

        private FirstLoan firstLoan;

        private static <C extends Comparable> Range<C> closedRange(C from, C to) {
            Preconditions.checkState(from != null || to != null);

            if (from != null && to != null) {
                return Range.closed(from, to);
            }

            if (from != null) {
                return Range.atLeast(from);
            }

            return Range.atMost(to);
        }

        @Data
        @Accessors(chain = true)
        public static class FirstLoan {
            private List<Age> ageRanges = Lists.newArrayList();

            public Optional<Age> byAge(Long age) {
                return ageRanges.stream().filter(ageRange -> closedRange(ageRange.getFromInclusive(), ageRange.getToInclusive()).contains(age)).findFirst();
            }
        }

        @Data
        @Accessors(chain = true)
        public static class Age {
            private Long fromInclusive;
            private Long toInclusive;

            private List<AverageAmountOfOutgoingTransactionsMonth> averageAmountOfOutgoingTransactionsMonthRanges = Lists.newArrayList();

            public Optional<AverageAmountOfOutgoingTransactionsMonth> byAverageAmountOfOutgoingTransactionsMonth(BigDecimal averageAmountOfOutgoingTransactionsMonth) {
                return averageAmountOfOutgoingTransactionsMonthRanges.stream()
                    .filter(averageAmountOfOutgoingTransactionsMonthRange -> closedRange(averageAmountOfOutgoingTransactionsMonthRange.getFromInclusive(), averageAmountOfOutgoingTransactionsMonthRange.getToInclusive()).contains(averageAmountOfOutgoingTransactionsMonth))
                    .findFirst();
            }
        }

        @Data
        @Accessors(chain = true)
        public static class AverageAmountOfOutgoingTransactionsMonth {
            private BigDecimal fromInclusive;
            private BigDecimal toInclusive;

            private BigDecimal creditLimit;
        }
    }

    @Data
    @Accessors(chain = true)
    public static class DiscountSettings {

        private List<Item> items = new ArrayList<>();
        private Long dpdThreshold;

        @Data
        @Accessors(chain = true)
        public static class Item {
            private BigDecimal totalRepaidPrincipalFromInclusive;
            private BigDecimal totalRepaidPrincipalToExclusive;
            private BigDecimal discountPercent;
        }

        public BigDecimal findDiscount(BigDecimal totalRepaidPrincipal) {
            return items.stream().filter(i -> BigDecimalUtils.goe(totalRepaidPrincipal, i.totalRepaidPrincipalFromInclusive)
                && BigDecimalUtils.lt(totalRepaidPrincipal, i.totalRepaidPrincipalToExclusive)).map(Item::getDiscountPercent).findFirst().orElse(amount(0));
        }
    }

    @Data
    @Accessors(chain = true)
    public static class GoodClientSettings {
        private int monthsToCheckDpd;
        private int maxDpd;
        private int minNumberOfPaidLoans;
        private BigDecimal minRepaidPrincipalAmount;
    }

    @Data
    @Accessors(chain = true)
    @ToString
    public static class UpsellWithinNewLoanApplicationSettings {
        private BigDecimal maxPrincipalRepaid;
        private Integer maxOverdueDays;
        private Long minAge;
        private BigDecimal maxCreditLimitMinusRequestedAtLeast;
    }

    @Data
    @Accessors(chain = true)
    @ToString
    public static class ApplicationOfUpsellSettings {
        private Integer minDaysSinceLoanIssue;
        private Integer minDaysBeforeEndOfTerm;
        private Long maxDaysUpsellOfferCallTaskActive;
        private Long maxDaysUpsellApproveOfferActivityActive;
    }

    @Data
    @Accessors(chain = true)
    public static class LocClientBatch {
        private int maxWorkflowsToRun;
    }

    @Data
    @Accessors(chain = true)
    public static class UnresponsiveBureauSettings {
        private Integer maxDaysSinceLastLoanPaid;
    }

    @Data
    @Accessors(chain = true)
    public static class IdDocumentValiditySettings {
        private boolean requestIdUploadForFirstLoan;
        private boolean requestIdUploadForSecondAndLaterLoan;
        private Integer docExpirationAllowedPeriodInDays;
        private Long idDocumentManualTextExtractionExpiryInDays;
        private Long idDocumentManualValidationExpiryInDays;
        private String idValidationScenarioKey;
    }

    @Data
    @Accessors(chain = true)
    public static class ScenarioStrategiesSettings {
        private String scenarioName;
    }

}
