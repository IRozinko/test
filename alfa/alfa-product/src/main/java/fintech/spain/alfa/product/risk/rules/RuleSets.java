package fintech.spain.alfa.product.risk.rules;

import com.google.common.collect.ImmutableList;
import fintech.rules.model.Rule;
import fintech.spain.alfa.product.risk.rules.basic.*;
import fintech.spain.alfa.product.risk.rules.creditlimit.LocCreditLimitAllowedMinRule;

import java.util.List;

public class RuleSets {

    public static final String MANDATORY_LENDING_RULES_NAME = "MandatoryLendingRules";
    public static final List<Class<? extends Rule>> MANDATORY_LENDING_RULES = ImmutableList.of(
        NoOpenLoanRule.class,
        ProvinceCodeBlacklistRule.class,
        DniBlacklistRule.class,
        EmailBlacklistRule.class,
        PhoneBlacklistRule.class,
        PrincipalSoldRule.class
    );

    public static final String BASIC_LENDING_RULES_NAME = "BasicLendingRules";
    public static final List<Class<? extends Rule>> BASIC_LENDING_RULES = ImmutableList.of(
        ApplicationCountWithin30DaysRule.class,
        RejectionCountIn30DaysRule.class,
        RejectionCountIn7DaysRule.class,
        FeePenaltyPaidRule.class,
        PrincipalDisbursedLessThanCashInRule.class,
        DaysSinceLastApplicationRejectAndRejectionReasonRule.class,
        TotalOverdueDaysRule.class,
        MaxOverdueDaysRule.class,
        MaxOverdueDaysIn12MonthsRule.class,
        LastLoanOverdueDaysRule.class,
        AgeRule.class
    );

//    public static final String CROSSCHECK_LENDING_RULES_NAME = "CrosscheckLendingRules";
//    public static final List<Class<? extends Rule>> CROSSCHECK_LENDING_RULES = ImmutableList.of(
//        CrosscheckActiveLoanRule.class,
//        CrosscheckBlacklistedRule.class,
//        CrosscheckMaxDpdRule.class,
//        CrosscheckActiveApplicationRule.class
//    );

//    public static final String IOVATION_RULES_NAME = "IovationRules";
//    public static final List<Class<? extends Rule>> IOVATION_RULES = ImmutableList.of(
//        IovationResultRule.class
//    );
//
//    public static final String INSTANTOR_RULES_NAME = "InstantorRules";
//    public static final List<Class<? extends Rule>> LOC_INSTANTOR_RULES = ImmutableList.of(
//        InstantorDniRule.class,
//        InstantorNameRule.class,
//        MonthsAvailableRule.class,
//        TotalTransactionCountRule.class,
//        AverageAmountOfOutgoingTransactionsPerMonthRule.class,
//        AverageAmountOfIncomingTransactionsPerMonthRule.class,
//        AverageNumberOfTransactionsMonthRule.class,
//        AverageMinimumBalanceMonthRule.class,
//        TotalBalanceRule.class
//    );
//
//    public static final List<Class<? extends Rule>> UNDERWRITING_INSTANTOR_RULES = ImmutableList.of(
//        InstantorDniRule.class,
//        InstantorNameRule.class,
//        MonthsAvailableRule.class,
//        TotalTransactionCountRule.class,
//        AverageAmountOfOutgoingTransactionsPerMonthRule.class,
//        AverageAmountOfIncomingTransactionsPerMonthRule.class,
//        AverageNumberOfTransactionsMonthRule.class,
//        AverageMinimumBalanceMonthRule.class,
//        TotalBalanceRule.class
//    );
//
//    public static final String EXPERIAN_RULES_NAME = "ExperianRules";
//    public static final List<Class<? extends Rule>> EXPERIAN_RULES = ImmutableList.of(
//        ExperianProvinceCodeBlacklistRule.class,
//        ExperianAmountRule.class,
//        ExperianDebtCountRule.class,
//        ExperianPaymentSitutationRule.class
//    );
//
//    public static final String EQUIFAX_RULES_NAME = "EquifaxRules";
//    public static final List<Class<? extends Rule>> EQUIFAX_RULES = ImmutableList.of(
//        EquifaxDebtRule.class,
//        EquifaxDelincuencyDaysRule.class,
//        EquifaxNumberOfDaysOfWorstSituationRule.class
//    );

    public static final String LOC_CREDIT_LIMIT_NAME = "LocCreditLimitRules";
    public static final List<Class<? extends Rule>> LOC_CREDIT_LIMIT_RULES = ImmutableList.of(
        LocCreditLimitAllowedMinRule.class
    );

//    public static final String ID_DOCUMENT_EXPIRATION_RULES_NAME = "IdDocumentExpirationRules";
//    public static final List<Class<? extends Rule>> ID_DOCUMENT_EXPIRATION_RULES = ImmutableList.of(
//        IdDocExpirationDateRule.class
//    );
}
