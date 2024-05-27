package fintech.instantor.parser.impl;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import fintech.IbanUtils;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.db.SystemEnvironment;
import fintech.instantor.db.InstantorAccountEntity;
import fintech.instantor.db.InstantorResponseEntity;
import fintech.instantor.db.InstantorTransactionEntity;
import fintech.instantor.json.insight.*;
import fintech.instantor.model.CashflowAttributes;
import fintech.instantor.model.InstantorResponseStatus;
import fintech.instantor.model.SaveInstantorResponseCommand;
import fintech.instantor.parser.InstantorDataResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.amount;
import static fintech.IbanUtils.isIbanValid;
import static fintech.IbanUtils.normalizeIban;
import static fintech.PojoUtils.npeSafe;
import static fintech.instantor.model.InstantorResponseAttributes.*;
import static java.lang.String.join;

@Slf4j
public class InsightInstantorParser extends AbstractInstantorParser {

    private final InstantorDataResolver<InstantorInsightResponse> dataResolver;
    private final SystemEnvironment systemEnvironment;

    public InsightInstantorParser(InstantorDataResolver<InstantorInsightResponse> dataResolver, SystemEnvironment systemEnvironment) {
        this.dataResolver = dataResolver;
        this.systemEnvironment = systemEnvironment;
    }

    protected void parseJsonInstantorData(InstantorResponseEntity entity, SaveInstantorResponseCommand command) {
        Validate.notBlank(command.getPayloadJson(), "Payload json is empty: [%s]", command);
        InstantorInsightResponse response = JsonUtils.readValue(command.getPayloadJson(), InstantorInsightResponse.class);
        entity.setClientId(dataResolver.resolveClientId(response));

        // Is PRODUCTION and we got Fake Instantor response
        if (systemEnvironment.isProd() && dataResolver.resolveIsFakeType(response)) {
            entity.setStatus(InstantorResponseStatus.FAILED);
            entity.setError("Got Fake Instantor response on PRODUCTION environment");
            log.error("Got Fake Instantor response on PRODUCTION environment. RequestId: {}", response.getInstantorRequestId());
        } else {
            parseJsonInstantorData(entity, response);
        }
    }

    private void parseJsonInstantorData(InstantorResponseEntity entity, InstantorInsightResponse response) {
        String processStatus = npeSafe(response::getProcessStatus).orElse(StringUtils.EMPTY);
        if (!StringUtils.equalsIgnoreCase("ok", processStatus)) {
            entity.setStatus(InstantorResponseStatus.FAILED);
            entity.setError("Process status: " + processStatus);
        } else {
            if (entity.getClientId() == null) {
                entity.setStatus(InstantorResponseStatus.FAILED);
                entity.setError("Client id not resolved");
            } else {
                entity.setAccountNumbers(getAccountNumbers(response));
                entity.setNameForVerification(dataResolver.resolveName(response));
                entity.setPersonalNumberForVerification(dataResolver.resolvePersonalNumber(response));
                entity.setLatest(true);
                entity.setAttributes(parseInstantorAttributes(response, dataResolver.primaryBankAccount(entity.getClientId())));
                entity.setAccounts(parseInstantorAccounts(entity, response));
            }
        }
    }

    private String getAccountNumbers(InstantorInsightResponse response) {
        return response.getAccountList().stream().map(Account::getIban).map(IbanUtils::normalizeIban).collect(Collectors.joining(","));
    }

    private Map<String, String> parseInstantorAttributes(InstantorInsightResponse response, String primaryIban) {
        Map<String, String> attributes = new HashMap<>();
        getAccountReport(response, primaryIban).map(this::parseAccountReportsAttributes).ifPresent(attributes::putAll);
        npeSafe(response::getInstantorRequestId).ifPresent(v -> attributes.put(INSTANTOR_REQUEST_ID.name(), v));
        npeSafe(() -> response.getBankInfo().getName()).ifPresent(v -> attributes.put(BANK_NAME.name(), v));
        npeSafe(response::getUserDetails).map(this::parseUserDetails).ifPresent(attributes::putAll);
        npeSafe(response::getIncomeVerification).map(this::parseIncomeVerificationAttributes).ifPresent(attributes::putAll);
        npeSafe(response::getInsightsRiskFeatures).map(this::parseInsightsRiskFeaturesAttributes).ifPresent(attributes::putAll);
        return attributes;
    }

    private Map<String, String> parseUserDetails(UserDetails userDetails) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(userDetails::getName).ifPresent(v -> attributes.put(NAME.name(), v));
        npeSafe(() -> join(",", userDetails.getAddress())).ifPresent(v -> attributes.put(ADDRESS.name(), v));
        npeSafe(() -> join(",", userDetails.getEmail())).ifPresent(v -> attributes.put(EMAIL.name(), v));
        npeSafe(() -> join(",", userDetails.getPhone())).ifPresent(v -> attributes.put(PHONE.name(), v));
        return attributes;
    }

    private Map<String, String> parseAccountReportsAttributes(AccountReport accountReport) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(() -> amount(accountReport.getAverageAmountOfOutgoingTransactionsWholeMonth()).abs()).ifPresent(v -> attributes.put(AVERAGE_AMOUNT_OF_OUTGOING_TRANSACTIONS_PER_MONTH.name(), v.toString()));
        npeSafe(() -> amount(accountReport.getAverageAmountOfIncomingTransactionsWholeMonth()).abs()).ifPresent(v -> attributes.put(AVERAGE_AMOUNT_OF_INCOMING_TRANSACTIONS_PER_MONTH.name(), v.toString()));
        npeSafe(() -> amount(accountReport.getAverageMinimumBalanceWholeMonth())).ifPresent(v -> attributes.put(AVERAGE_MINIMUM_BALANCE_PER_MONTH.name(), v.toString()));
        npeSafe(accountReport::getAverageNumberOfTransactionsWholeMonth).ifPresent(v -> attributes.put(AVERAGE_AMOUNT_OF_TRANSACTIONS_PER_MONTH.name(), v.toString()));
        npeSafe(accountReport::getWholeMonthsAvailable).ifPresent(v -> attributes.put(MONTHS_AVAILABLE.name(), v.toString()));
        npeSafe(accountReport::getTotalNumberOfTransactions).ifPresent(v -> attributes.put(TOTAL_NUMBER_OF_TRANSACTIONS.name(), v.toString()));
        attributes.putAll(parseAccountReportCashflow(accountReport));
        return attributes;
    }

    private Map<String, String> parseAccountReportCashflow(AccountReport accountReport) {
        Map<String, String> attributes = new HashMap<>();
        CashflowAttributes cashflowAttributes = accountReport.getCashFlows().stream()
            .map(CashflowAttributes::new)
            .reduce(new CashflowAttributes(), CashflowAttributes::add);

        attributes.put(ACCOUNT_REPORT_CASHFLOW_CALENDAR_MONTH.name(), JsonUtils.writeValueAsString(cashflowAttributes.getCalendarMonth()));
        attributes.put(ACCOUNT_REPORT_CASHFLOW_INCOMING.name(), JsonUtils.writeValueAsString(cashflowAttributes.getIncoming()));
        attributes.put(ACCOUNT_REPORT_CASHFLOW_OUTGOING.name(), JsonUtils.writeValueAsString(cashflowAttributes.getOutgoing()));
        attributes.put(ACCOUNT_REPORT_CASHFLOW_MIN_BALANCE.name(), JsonUtils.writeValueAsString(cashflowAttributes.getMinBalance()));
        attributes.put(ACCOUNT_REPORT_CASHFLOW_MAX_BALANCE.name(), JsonUtils.writeValueAsString(cashflowAttributes.getMaxBalance()));
        attributes.put(ACCOUNT_REPORT_CASHFLOW_AVG_BALANCE.name(), JsonUtils.writeValueAsString(cashflowAttributes.getAvgBalance()));
        attributes.put(ACCOUNT_REPORT_CASHFLOW_IS_WHOLE_MONTH.name(), JsonUtils.writeValueAsString(cashflowAttributes.getIsWholeMonth()));
        return attributes;
    }

    private Map<String, String> parseIncomeVerificationAttributes(IncomeVerification incomeVerification) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(() -> incomeVerification.getPrimaryIncome().getMeanAmountPayment()).ifPresent(v -> attributes.put(PRIMARY_INCOME_MEAN_AMOUNT_PAYMENT.name(), v.toString()));
        npeSafe(() -> incomeVerification.getPrimaryIncome().getLast45dFound()).ifPresent(v -> attributes.put(PRIMARY_INCOME_LAST_45D_FOUND.name(), v.toString()));
        npeSafe(() -> incomeVerification.getPrimaryIncome().getAvg6m()).ifPresent(v -> attributes.put(PRIMARY_INCOME_AVG_6M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getPrimaryIncome().getAvg3m()).ifPresent(v -> attributes.put(PRIMARY_INCOME_AVG_3M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getPrimaryIncome().getMeanTimeBetweenPayments()).ifPresent(v -> attributes.put(PRIMARY_INCOME_MEAN_TIME_BETWEEN_PAYMENTS.name(), v.toString()));
        npeSafe(() -> incomeVerification.getPrimaryIncome().getTrend3m()).ifPresent(v -> attributes.put(PRIMARY_INCOME_TREND_3M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getPrimaryIncome().getDaysAgoLastPayment()).ifPresent(v -> attributes.put(PRIMARY_INCOME_DAYS_AGO_LAST_PAYMENT.name(), v.toString()));
        npeSafe(() -> incomeVerification.getPrimaryIncome().getTrend6m()).ifPresent(v -> attributes.put(PRIMARY_INCOME_TREND_6M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getPrimaryIncome().getAmountLastPayment()).ifPresent(v -> attributes.put(PRIMARY_INCOME_AMOUNT_LAST_PAYMENT.name(), v.toString()));
        npeSafe(() -> incomeVerification.getPrimaryIncome().getSpanMonths()).ifPresent(v -> attributes.put(PRIMARY_INCOME_SPAN_MONTHS.name(), v.toString()));
        npeSafe(() -> incomeVerification.getPrimaryIncome().getTrend12m()).ifPresent(v -> attributes.put(PRIMARY_INCOME_TREND_12M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getPrimaryIncome().getAvg12m()).ifPresent(v -> attributes.put(PRIMARY_INCOME_AVG_12M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getPrimaryIncome().getNumPayments()).ifPresent(v -> attributes.put(PRIMARY_INCOME_NUM_PAYMENTS.name(), v.toString()));
        npeSafe(() -> incomeVerification.getPrimaryIncome().getDescriptions()).map(d -> join(",", d)).ifPresent(v -> attributes.put(PRIMARY_INCOME_DESCRIPTIONS.name(), v.toString()));

        npeSafe(() -> incomeVerification.getSecondaryIncomes().getMeanAmountPayment()).ifPresent(v -> attributes.put(SECONDARY_INCOME_MEAN_AMOUNT_PAYMENT.name(), v.toString()));
        npeSafe(() -> incomeVerification.getSecondaryIncomes().getLast45dFound()).ifPresent(v -> attributes.put(SECONDARY_INCOME_LAST_45D_FOUND.name(), v.toString()));
        npeSafe(() -> incomeVerification.getSecondaryIncomes().getAvg6m()).ifPresent(v -> attributes.put(SECONDARY_INCOME_AVG_6M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getSecondaryIncomes().getAvg3m()).ifPresent(v -> attributes.put(SECONDARY_INCOME_AVG_3M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getSecondaryIncomes().getMeanTimeBetweenPayments()).ifPresent(v -> attributes.put(SECONDARY_INCOME_MEAN_TIME_BETWEEN_PAYMENTS.name(), v.toString()));
        npeSafe(() -> incomeVerification.getSecondaryIncomes().getTrend3m()).ifPresent(v -> attributes.put(SECONDARY_INCOME_TREND_3M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getSecondaryIncomes().getDaysAgoLastPayment()).ifPresent(v -> attributes.put(SECONDARY_INCOME_DAYS_AGO_LAST_PAYMENT.name(), v.toString()));
        npeSafe(() -> incomeVerification.getSecondaryIncomes().getTrend6m()).ifPresent(v -> attributes.put(SECONDARY_INCOME_TREND_6M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getSecondaryIncomes().getAmountLastPayment()).ifPresent(v -> attributes.put(SECONDARY_INCOME_AMOUNT_LAST_PAYMENT.name(), v.toString()));
        npeSafe(() -> incomeVerification.getSecondaryIncomes().getSpanMonths()).ifPresent(v -> attributes.put(SECONDARY_INCOME_SPAN_MONTHS.name(), v.toString()));
        npeSafe(() -> incomeVerification.getSecondaryIncomes().getTrend12m()).ifPresent(v -> attributes.put(SECONDARY_INCOME_TREND_12M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getSecondaryIncomes().getAvg12m()).ifPresent(v -> attributes.put(SECONDARY_INCOME_AVG_12M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getSecondaryIncomes().getNumPayments()).ifPresent(v -> attributes.put(SECONDARY_INCOME_NUM_PAYMENTS.name(), v.toString()));
        npeSafe(() -> incomeVerification.getSecondaryIncomes().getDescriptions()).map(d -> join(",", d)).ifPresent(v -> attributes.put(SECONDARY_INCOME_DESCRIPTIONS.name(), v.toString()));

        npeSafe(() -> incomeVerification.getOtherRecurIncome().getMeanAmountPayment()).ifPresent(v -> attributes.put(OTHER_RECUR_MEAN_AMOUNT_PAYMENT.name(), v.toString()));
        npeSafe(() -> incomeVerification.getOtherRecurIncome().getLast45dFound()).ifPresent(v -> attributes.put(OTHER_RECUR_LAST_45D_FOUND.name(), v.toString()));
        npeSafe(() -> incomeVerification.getOtherRecurIncome().getAvg6m()).ifPresent(v -> attributes.put(OTHER_RECUR_AVG_6M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getOtherRecurIncome().getAvg3m()).ifPresent(v -> attributes.put(OTHER_RECUR_AVG_3M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getOtherRecurIncome().getMeanTimeBetweenPayments()).ifPresent(v -> attributes.put(OTHER_RECUR_MEAN_TIME_BETWEEN_PAYMENTS.name(), v.toString()));
        npeSafe(() -> incomeVerification.getOtherRecurIncome().getTrend3m()).ifPresent(v -> attributes.put(OTHER_RECUR_TREND_3M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getOtherRecurIncome().getDaysAgoLastPayment()).ifPresent(v -> attributes.put(OTHER_RECUR_DAYS_AGO_LAST_PAYMENT.name(), v.toString()));
        npeSafe(() -> incomeVerification.getOtherRecurIncome().getTrend6m()).ifPresent(v -> attributes.put(OTHER_RECUR_TREND_6M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getOtherRecurIncome().getAmountLastPayment()).ifPresent(v -> attributes.put(OTHER_RECUR_AMOUNT_LAST_PAYMENT.name(), v.toString()));
        npeSafe(() -> incomeVerification.getOtherRecurIncome().getSpanMonths()).ifPresent(v -> attributes.put(OTHER_RECUR_SPAN_MONTHS.name(), v.toString()));
        npeSafe(() -> incomeVerification.getOtherRecurIncome().getTrend12m()).ifPresent(v -> attributes.put(OTHER_RECUR_TREND_12M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getOtherRecurIncome().getAvg12m()).ifPresent(v -> attributes.put(OTHER_RECUR_AVG_12M.name(), v.toString()));
        npeSafe(() -> incomeVerification.getOtherRecurIncome().getNumPayments()).ifPresent(v -> attributes.put(OTHER_RECUR_NUM_PAYMENTS.name(), v.toString()));
        npeSafe(() -> incomeVerification.getOtherRecurIncome().getDescriptions()).map(d -> join(",", d)).ifPresent(v -> attributes.put(OTHER_RECUR_DESCRIPTIONS.name(), v.toString()));
        return attributes;
    }

    private Map<String, String> parseInsightsRiskFeaturesAttributes(InsightsRiskFeatures riskFeatures) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(riskFeatures::getSavings).map(this::parseRiskSavings).ifPresent(attributes::putAll);
        npeSafe(riskFeatures::getIncomeTrends).map(this::parseIncomeTrends).ifPresent(attributes::putAll);
        npeSafe(riskFeatures::getCashFlow).map(this::parseRiskCashFlow).ifPresent(attributes::putAll);
        npeSafe(riskFeatures::getCollections).map(this::parseCollections).ifPresent(attributes::putAll);
        npeSafe(riskFeatures::getLowBalances).map(this::parseLowBalance).ifPresent(attributes::putAll);
        npeSafe(riskFeatures::getLoans).map(this::parseLoans).ifPresent(attributes::putAll);
        npeSafe(riskFeatures::getSpendingDistribution).map(this::parseSpendingDistribution).ifPresent(attributes::putAll);
        npeSafe(riskFeatures::getGamblingVsIncome).map(this::parseGamblingVsIncome).ifPresent(attributes::putAll);
        npeSafe(riskFeatures::getAtmWithdrawals).map(this::parseAtmWithdrawals).ifPresent(attributes::putAll);
        npeSafe(riskFeatures::getMonthlyPaymentVariance).map(this::parseMonthlyPaymentVariance).ifPresent(attributes::putAll);
        npeSafe(riskFeatures::getAccountActivity).map(this::parseAccountActivity).ifPresent(attributes::putAll);
        npeSafe(riskFeatures::getOverdrafts).map(this::parseOverdrafts).ifPresent(attributes::putAll);
        npeSafe(riskFeatures::getAccountOverview).map(this::parseAccountOverview).ifPresent(attributes::putAll);
        npeSafe(riskFeatures::getBalances).map(this::parseBalances).ifPresent(attributes::putAll);
        npeSafe(riskFeatures::getTransactionStats).map(this::parseTransactionsStats).ifPresent(attributes::putAll);
        return attributes;
    }

    private Map<String, String> parseRiskSavings(Savings savings) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(savings::getSumSavings1W).ifPresent(v -> attributes.put(SUM_SAVINGS_1W.name(), v.toString()));
        npeSafe(savings::getNumSavings6M).ifPresent(v -> attributes.put(NUM_SAVINGS_6M.name(), v.toString()));
        npeSafe(savings::getNumSavingsTotal).ifPresent(v -> attributes.put(NUM_SAVINGS_TOTAL.name(), v.toString()));
        npeSafe(savings::getSumSavingsTotal).ifPresent(v -> attributes.put(SUM_SAVINGS_TOTAL.name(), v.toString()));
        npeSafe(savings::getSumSavings1M).ifPresent(v -> attributes.put(SUM_SAVINGS_1M.name(), v.toString()));
        npeSafe(savings::getSumSavings3M).ifPresent(v -> attributes.put(SUM_SAVINGS_3M.name(), v.toString()));
        npeSafe(savings::getSumSavings12M).ifPresent(v -> attributes.put(SUM_SAVINGS_12M.name(), v.toString()));
        npeSafe(savings::getNumSavings12M).ifPresent(v -> attributes.put(NUM_SAVINGS_12M.name(), v.toString()));
        npeSafe(savings::getNumSavings3M).ifPresent(v -> attributes.put(NUM_SAVINGS_3M.name(), v.toString()));
        npeSafe(savings::getNumSavings1W).ifPresent(v -> attributes.put(NUM_SAVINGS_1W.name(), v.toString()));
        npeSafe(savings::getSumSavings6M).ifPresent(v -> attributes.put(SUM_SAVINGS_6M.name(), v.toString()));
        npeSafe(savings::getNumSavings1M).ifPresent(v -> attributes.put(NUM_SAVINGS_1M.name(), v.toString()));
        return attributes;
    }

    private Map<String, String> parseIncomeTrends(IncomeTrends incomeTrends) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(incomeTrends::getAvg30dRollingRecent).ifPresent(v -> attributes.put(AVG_30D_ROLLING_RECENT.name(), v.toString()));
        npeSafe(incomeTrends::getAvg30dRollingPast).ifPresent(v -> attributes.put(AVG_30DROLLING_PAST.name(), v.toString()));
        npeSafe(incomeTrends::getAvgRollingTrend).ifPresent(v -> attributes.put(AVG_ROLLING_TREND.name(), v.toString()));
        npeSafe(incomeTrends::getAvgRollingDiff).ifPresent(v -> attributes.put(AVG_ROLLING_DIFF.name(), v.toString()));
        return attributes;
    }

    private Map<String, String> parseRiskCashFlow(RiskCashFlow riskCashFlow) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(riskCashFlow::getPositiveCashflow1M).ifPresent(v -> attributes.put(POSITIVE_CASHFLOW_1M.name(), v.toString()));
        npeSafe(riskCashFlow::getPositiveCashflow1W).ifPresent(v -> attributes.put(POSITIVE_CASHFLOW_1W.name(), v.toString()));
        npeSafe(riskCashFlow::getNegativeCashflow3M).ifPresent(v -> attributes.put(NEGATIVE_CASHFLOW_3M.name(), v.toString()));
        npeSafe(riskCashFlow::getPositiveCashflowTotal).ifPresent(v -> attributes.put(POSITIVE_CASHFLOW_TOTAL.name(), v.toString()));
        npeSafe(riskCashFlow::getPositiveCashflow3M).ifPresent(v -> attributes.put(POSITIVE_CASHFLOW_3M.name(), v.toString()));
        npeSafe(riskCashFlow::getNegativeCashflowTotal).ifPresent(v -> attributes.put(NEGATIVE_CASHFLOW_TOTAL.name(), v.toString()));
        npeSafe(riskCashFlow::getPositiveNegativeRatio6M).ifPresent(v -> attributes.put(POSITIVE_NEGATIVE_RATIO_6M.name(), v.toString()));
        npeSafe(riskCashFlow::getPositiveNegativeRatioTotal).ifPresent(v -> attributes.put(POSITIVE_NEGATIVE_RATIO_TOTAL.name(), v.toString()));
        npeSafe(riskCashFlow::getNegativeCashflow1W).ifPresent(v -> attributes.put(NEGATIVE_CASHFLOW_1W.name(), v.toString()));
        npeSafe(riskCashFlow::getPositiveNegativeRatio3M).ifPresent(v -> attributes.put(POSITIVE_NEGATIVE_RATIO_3M.name(), v.toString()));
        npeSafe(riskCashFlow::getNegativeCashflow12M).ifPresent(v -> attributes.put(NEGATIVE_CASHFLOW_12M.name(), v.toString()));
        npeSafe(riskCashFlow::getNegativeCashflow1M).ifPresent(v -> attributes.put(NEGATIVE_CASHFLOW_1M.name(), v.toString()));
        npeSafe(riskCashFlow::getPositiveNegativeRatio1W).ifPresent(v -> attributes.put(POSITIVE_NEGATIVE_RATIO_1W.name(), v.toString()));
        npeSafe(riskCashFlow::getPositiveNegativeRatio1M).ifPresent(v -> attributes.put(POSITIVE_NEGATIVE_RATIO_1M.name(), v.toString()));
        npeSafe(riskCashFlow::getPositiveCashflow6M).ifPresent(v -> attributes.put(POSITIVE_CASHFLOW_6M.name(), v.toString()));
        npeSafe(riskCashFlow::getPositiveCashflow12M).ifPresent(v -> attributes.put(POSITIVE_CASHFLOW_12M.name(), v.toString()));
        npeSafe(riskCashFlow::getPositiveNegativeRatio12M).ifPresent(v -> attributes.put(POSITIVE_NEGATIVE_RATIO_12M.name(), v.toString()));
        npeSafe(riskCashFlow::getNegativeCashflow6M).ifPresent(v -> attributes.put(NEGATIVE_CASHFLOW_6M.name(), v.toString()));
        return attributes;
    }

    private Map<String, String> parseCollections(Collections collections) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(collections::getSumCollections3M).ifPresent(v -> attributes.put(SUM_COLLECTIONS_3M.name(), v.toString()));
        npeSafe(collections::getNumCollectionsTotal).ifPresent(v -> attributes.put(NUM_COLLECTIONS_TOTAL.name(), v.toString()));
        npeSafe(collections::getSumCollections6M).ifPresent(v -> attributes.put(SUM_COLLECTIONS_6M.name(), v.toString()));
        npeSafe(collections::getNumCollections1W).ifPresent(v -> attributes.put(NUM_COLLECTIONS_1W.name(), v.toString()));
        npeSafe(collections::getSumCollections12M).ifPresent(v -> attributes.put(SUM_COLLECTIONS_12M.name(), v.toString()));
        npeSafe(collections::getSumCollectionsTotal).ifPresent(v -> attributes.put(SUM_COLLECTIONS_TOTAL.name(), v.toString()));
        npeSafe(collections::getNumCollections1M).ifPresent(v -> attributes.put(NUM_COLLECTIONS_1M.name(), v.toString()));
        npeSafe(collections::getNumCollections6M).ifPresent(v -> attributes.put(NUM_COLLECTIONS_6M.name(), v.toString()));
        npeSafe(collections::getSumCollections1M).ifPresent(v -> attributes.put(SUM_COLLECTIONS_1M.name(), v.toString()));
        npeSafe(collections::getSumCollections1W).ifPresent(v -> attributes.put(SUM_COLLECTIONS_1W.name(), v.toString()));
        npeSafe(collections::getNumCollections12M).ifPresent(v -> attributes.put(NUM_COLLECTIONS_12M.name(), v.toString()));
        npeSafe(collections::getNumCollections3M).ifPresent(v -> attributes.put(NUM_COLLECTIONS_3M.name(), v.toString()));
        return attributes;
    }

    private Map<String, String> parseLowBalance(LowBalances lowBalances) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(lowBalances::getDaysBalanceBelow7_3M).ifPresent(v -> attributes.put(DAYS_BALANCE_BELOW_7_3M.name(), v.toString()));
        npeSafe(lowBalances::getDaysBalanceBelow0_1W).ifPresent(v -> attributes.put(DAYS_BALANCE_BELOW_0_1W.name(), v.toString()));
        npeSafe(lowBalances::getDaysBalanceBelow0_3M).ifPresent(v -> attributes.put(DAYS_BALANCE_BELOW_0_3M.name(), v.toString()));
        npeSafe(lowBalances::getDaysBalanceBelow7_1W).ifPresent(v -> attributes.put(DAYS_BALANCE_BELOW_7_1W.name(), v.toString()));
        npeSafe(lowBalances::getDaysBalanceBelow0_12M).ifPresent(v -> attributes.put(DAYS_BALANCE_BELOW_0_12M.name(), v.toString()));
        npeSafe(lowBalances::getDaysBalanceBelow0_6M).ifPresent(v -> attributes.put(DAYS_BALANCE_BELOW_0_6M.name(), v.toString()));
        npeSafe(lowBalances::getDaysBalanceBelow7_1M).ifPresent(v -> attributes.put(DAYS_BALANCE_BELOW_7_1M.name(), v.toString()));
        npeSafe(lowBalances::getDaysBalanceBelow7_Total).ifPresent(v -> attributes.put(DAYS_BALANCE_BELOW_7_TOTAL.name(), v.toString()));
        npeSafe(lowBalances::getDaysBalanceBelow0_Total).ifPresent(v -> attributes.put(DAYS_BALANCE_BELOW_0_TOTAL.name(), v.toString()));
        npeSafe(lowBalances::getDaysBalanceBelow0_1M).ifPresent(v -> attributes.put(DAYS_BALANCE_BELOW_0_1M.name(), v.toString()));
        npeSafe(lowBalances::getDaysBalanceBelow7_12M).ifPresent(v -> attributes.put(DAYS_BALANCE_BELOW_7_12M.name(), v.toString()));
        npeSafe(lowBalances::getDaysBalanceBelow7_6M).ifPresent(v -> attributes.put(DAYS_BALANCE_BELOW_7_6M.name(), v.toString()));
        return attributes;
    }

    private Map<String, String> parseLoans(Loans loans) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(loans::getRepaymentLoanRatio3M).ifPresent(v -> attributes.put(REPAYMENT_LOAN_RATIO_3M.name(), v.toString()));
        npeSafe(loans::getSumRepayments12M).ifPresent(v -> attributes.put(SUM_REPAYMENTS_12M.name(), v.toString()));
        npeSafe(loans::getRepaymentLoanRatioTotal).ifPresent(v -> attributes.put(REPAYMENT_LOAN_RATIO_TOTAL.name(), v.toString()));
        npeSafe(loans::getSumRepaymentsTotal).ifPresent(v -> attributes.put(SUM_REPAYMENTS_TOTAL.name(), v.toString()));
        npeSafe(loans::getSumRepayments1W).ifPresent(v -> attributes.put(SUM_REPAYMENTS_1W.name(), v.toString()));
        npeSafe(loans::getRepaymentLoanRatio1W).ifPresent(v -> attributes.put(REPAYMENT_LOAN_RATIO_1W.name(), v.toString()));
        npeSafe(loans::getSumLoansTotal).ifPresent(v -> attributes.put(SUM_LOANS_TOTAL.name(), v.toString()));
        npeSafe(loans::getSumLoans3M).ifPresent(v -> attributes.put(SUM_LOANS_3M.name(), v.toString()));
        npeSafe(loans::getSumLoans1W).ifPresent(v -> attributes.put(SUM_LOANS_1W.name(), v.toString()));
        npeSafe(loans::getSumRepayments3M).ifPresent(v -> attributes.put(SUM_REPAYMENTS_3M.name(), v.toString()));
        npeSafe(loans::getRepaymentLoanRatio6M).ifPresent(v -> attributes.put(REPAYMENT_LOAN_RATIO_6M.name(), v.toString()));
        npeSafe(loans::getSumLoans1M).ifPresent(v -> attributes.put(SUM_LOANS_1M.name(), v.toString()));
        npeSafe(loans::getRepaymentLoanRatio12M).ifPresent(v -> attributes.put(REPAYMENT_LOAN_RATIO_12M.name(), v.toString()));
        npeSafe(loans::getRepaymentLoanRatio1M).ifPresent(v -> attributes.put(REPAYMENT_LOAN_RATIO_1M.name(), v.toString()));
        npeSafe(loans::getSumLoans12M).ifPresent(v -> attributes.put(SUM_LOANS_12M.name(), v.toString()));
        npeSafe(loans::getSumRepayments1M).ifPresent(v -> attributes.put(SUM_REPAYMENTS_1M.name(), v.toString()));
        npeSafe(loans::getSumRepayments6M).ifPresent(v -> attributes.put(SUM_REPAYMENTS_6M.name(), v.toString()));
        npeSafe(loans::getSumLoans6M).ifPresent(v -> attributes.put(SUM_LOANS_6M.name(), v.toString()));
        return attributes;
    }

    private Map<String, String> parseSpendingDistribution(SpendingDistribution spendingDistribution) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(spendingDistribution::getLastMonthSum_1000_to_2000).ifPresent(v -> attributes.put(LAST_MONTH_SUM_1000_TO_2000.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_0_to_5).ifPresent(v -> attributes.put(TREND_0_TO_5.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_50_to_100).ifPresent(v -> attributes.put(LAST_MONTH_SUM_50_TO_100.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_minus_5_to_0).ifPresent(v -> attributes.put(LAST_MONTH_SUM_MINUS_5_TO_0.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_20_to_50).ifPresent(v -> attributes.put(TREND_20_TO_50.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_minus_200_to_minus_100).ifPresent(v -> attributes.put(TREND_MINUS_200_TO_MINUS_100.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_2000_to_inf).ifPresent(v -> attributes.put(LAST_MONTH_SUM_2000_TO_INF.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_500_to_1000).ifPresent(v -> attributes.put(TREND_500_TO_1000.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_500_to_1000).ifPresent(v -> attributes.put(LAST_MONTH_SUM_500_TO_1000.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_0_to_5).ifPresent(v -> attributes.put(LAST_MONTH_SUM_0_TO_5.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_20_to_50).ifPresent(v -> attributes.put(LAST_MONTH_SUM_20_TO_50.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_minus_50_to_minus_20).ifPresent(v -> attributes.put(LAST_MONTH_SUM_MINUS_50_TO_MINUS_20.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_minus_inf_to_minus_2000).ifPresent(v -> attributes.put(TREND_MINUS_INF_TO_MINUS_2000.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_200_to_500).ifPresent(v -> attributes.put(TREND_200_TO_500.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_minus_1000_to_minus_500).ifPresent(v -> attributes.put(TREND_MINUS_1000_TO_MINUS_500.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_minus_100_to_minus_50).ifPresent(v -> attributes.put(TREND_MINUS_100_TO_MINUS_50.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_minus_10_to_minus_5).ifPresent(v -> attributes.put(TREND_MINUS_10_TO_MINUS_5.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_10_to_20).ifPresent(v -> attributes.put(TREND_10_TO_20.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_minus_2000_to_minus_1000).ifPresent(v -> attributes.put(TREND_MINUS_2000_TO_MINUS_1000.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_10_to_20).ifPresent(v -> attributes.put(LAST_MONTH_SUM_10_TO_20.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_minus_200_to_minus_100).ifPresent(v -> attributes.put(LAST_MONTH_SUM_MINUS_200_TO_MINUS_100.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_minus_2000_to_minus_1000).ifPresent(v -> attributes.put(LAST_MONTH_SUM_MINUS_2000_TO_MINUS_1000.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_200_to_500).ifPresent(v -> attributes.put(LAST_MONTH_SUM_200_TO_500.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_minus_inf_to_minus_2000).ifPresent(v -> attributes.put(LAST_MONTH_SUM_MINUS_INF_TO_MINUS_2000.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_5_to_10).ifPresent(v -> attributes.put(TREND_5_TO_10.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_minus_50_to_minus_20).ifPresent(v -> attributes.put(TREND_MINUS_50_TO_MINUS_20.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_100_to_200).ifPresent(v -> attributes.put(TREND_100_TO_200.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_5_to_10).ifPresent(v -> attributes.put(LAST_MONTH_SUM_5_TO_10.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_100_to_200).ifPresent(v -> attributes.put(LAST_MONTH_SUM_100_TO_200.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_1000_to_2000).ifPresent(v -> attributes.put(TREND_1000_TO_2000.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_50_to_100).ifPresent(v -> attributes.put(TREND_50_TO_100.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_minus_500_to_minus_200).ifPresent(v -> attributes.put(TREND_MINUS_500_TO_MINUS_200.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_minus_1000_to_minus_500).ifPresent(v -> attributes.put(LAST_MONTH_SUM_MINUS_1000_TO_MINUS_500.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_minus_20_to_minus_10).ifPresent(v -> attributes.put(TREND_MINUS_20_TO_MINUS_10.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_minus_20_to_minus_10).ifPresent(v -> attributes.put(LAST_MONTH_SUM_MINUS_20_TO_MINUS_10.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_minus_500_to_minus_200).ifPresent(v -> attributes.put(LAST_MONTH_SUM_MINUS_500_TO_MINUS_200.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_minus_5_to_0).ifPresent(v -> attributes.put(TREND_MINUS_5_TO_0.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_minus_10_to_minus_5).ifPresent(v -> attributes.put(LAST_MONTH_SUM_MINUS_10_TO_MINUS_5.name(), v.toString()));
        npeSafe(spendingDistribution::getTrend_2000_to_inf).ifPresent(v -> attributes.put(TREND_2000_TO_INF.name(), v.toString()));
        npeSafe(spendingDistribution::getLastMonthSum_minus_100_to_minus_50).ifPresent(v -> attributes.put(LAST_MONTH_SUM_MINUS_100_TO_MINUS_50.name(), v.toString()));
        return attributes;
    }

    private Map<String, String> parseGamblingVsIncome(GamblingVsIncome gamblingVsIncome) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(gamblingVsIncome::getGamblingIncomeRatio3M).ifPresent(v -> attributes.put(GAMBLING_INCOME_RATIO_3M.name(), v.toString()));
        npeSafe(gamblingVsIncome::getGamblingIncomeRatio1W).ifPresent(v -> attributes.put(GAMBLING_INCOME_RATIO_1W.name(), v.toString()));
        npeSafe(gamblingVsIncome::getSumGambling1W).ifPresent(v -> attributes.put(SUM_GAMBLING_1W.name(), v.toString()));
        npeSafe(gamblingVsIncome::getGamblingIncomeRatioTotal).ifPresent(v -> attributes.put(GAMBLING_INCOME_RATIO_TOTAL.name(), v.toString()));
        npeSafe(gamblingVsIncome::getGamblingIncomeRatio12M).ifPresent(v -> attributes.put(GAMBLING_INCOME_RATIO_12M.name(), v.toString()));
        npeSafe(gamblingVsIncome::getSumGamblingTotal).ifPresent(v -> attributes.put(SUM_GAMBLING_TOTAL.name(), v.toString()));
        npeSafe(gamblingVsIncome::getGamblingIncomeRatio1M).ifPresent(v -> attributes.put(GAMBLING_INCOME_RATIO1M.name(), v.toString()));
        npeSafe(gamblingVsIncome::getSumGambling6M).ifPresent(v -> attributes.put(SUM_GAMBLING_6M.name(), v.toString()));
        npeSafe(gamblingVsIncome::getSumGambling12M).ifPresent(v -> attributes.put(SUM_GAMBLING_12M.name(), v.toString()));
        npeSafe(gamblingVsIncome::getGamblingIncomeRatio6M).ifPresent(v -> attributes.put(GAMBLING_INCOME_RATIO_6M.name(), v.toString()));
        npeSafe(gamblingVsIncome::getSumGambling3M).ifPresent(v -> attributes.put(SUM_GAMBLING_3M.name(), v.toString()));
        npeSafe(gamblingVsIncome::getSumGambling1M).ifPresent(v -> attributes.put(SUM_GAMBLING_1M.name(), v.toString()));
        return attributes;
    }

    private Map<String, String> parseAtmWithdrawals(AtmWithdrawals atmWithdrawals) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(atmWithdrawals::getAtmExpensesRatioTotal).ifPresent(v -> attributes.put(ATM_EXPENSES_RATIO_TOTAL.name(), v.toString()));
        npeSafe(atmWithdrawals::getSumAtmWithdrawals6M).ifPresent(v -> attributes.put(SUM_ATM_WITHDRAWALS_6M.name(), v.toString()));
        npeSafe(atmWithdrawals::getAtmExpensesRatio1W).ifPresent(v -> attributes.put(ATM_EXPENSES_RATIO_1W.name(), v.toString()));
        npeSafe(atmWithdrawals::getAtmExpensesRatio1M).ifPresent(v -> attributes.put(ATM_EXPENSES_RATIO_1M.name(), v.toString()));
        npeSafe(atmWithdrawals::getAtmExpensesRatio6M).ifPresent(v -> attributes.put(ATM_EXPENSES_RATIO_6M.name(), v.toString()));
        npeSafe(atmWithdrawals::getSumAtmWithdrawals3M).ifPresent(v -> attributes.put(SUM_ATM_WITHDRAWALS_3M.name(), v.toString()));
        npeSafe(atmWithdrawals::getSumAtmWithdrawalsTotal).ifPresent(v -> attributes.put(SUM_ATM_WITHDRAWALS_TOTAL.name(), v.toString()));
        npeSafe(atmWithdrawals::getAtmExpensesRatio3M).ifPresent(v -> attributes.put(ATM_EXPENSES_RATIO_3M.name(), v.toString()));
        npeSafe(atmWithdrawals::getSumAtmWithdrawals1W).ifPresent(v -> attributes.put(SUM_ATM_WITHDRAWALS_1W.name(), v.toString()));
        npeSafe(atmWithdrawals::getSumAtmWithdrawals1M).ifPresent(v -> attributes.put(SUM_ATM_WITHDRAWALS_1M.name(), v.toString()));
        npeSafe(atmWithdrawals::getSumAtmWithdrawals12M).ifPresent(v -> attributes.put(SUM_ATM_WITHDRAWALS_12M.name(), v.toString()));
        npeSafe(atmWithdrawals::getAtmExpensesRatio12M).ifPresent(v -> attributes.put(ATM_EXPENSES_RATIO_12M.name(), v.toString()));
        return attributes;
    }

    private Map<String, String> parseMonthlyPaymentVariance(MonthlyPaymentVariance monthlyPaymentVariance) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(monthlyPaymentVariance::getNumMonthlyExpenseStreams).ifPresent(v -> attributes.put(NUM_MONTHLY_EXPENSE_STREAMS.name(), v.toString()));
        npeSafe(monthlyPaymentVariance::getMaxNormStdMonthlyExpenseStreams).ifPresent(v -> attributes.put(MAX_NORM_STD_MONTHLY_EXPENSE_STREAMS.name(), v.toString()));
        npeSafe(monthlyPaymentVariance::getMaxStdMonthlyExpenseStreams).ifPresent(v -> attributes.put(MAX_STD_MONTHLY_EXPENSE_STREAMS.name(), v.toString()));
        npeSafe(monthlyPaymentVariance::getAvgNormStdMonthlyExpenseStreams).ifPresent(v -> attributes.put(AVG_NORM_STD_MONTHLY_EXPENSE_STREAMS.name(), v.toString()));
        npeSafe(monthlyPaymentVariance::getAvgStdMonthlyExpenseStreams).ifPresent(v -> attributes.put(AVG_STD_MONTHLY_EXPENSE_STREAMS.name(), v.toString()));
        npeSafe(monthlyPaymentVariance::getAvgStdTiming).ifPresent(v -> attributes.put(AVG_STD_TIMING.name(), v.toString()));
        npeSafe(monthlyPaymentVariance::getMaxStdTiming).ifPresent(v -> attributes.put(MAX_STD_TIMING.name(), v.toString()));
        return attributes;
    }

    private Map<String, String> parseAccountActivity(AccountActivity accountActivity) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(accountActivity::getActivityIncTotal).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_INC_TOTAL.name(), v.toString()));
        npeSafe(accountActivity::getActivity12M).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_12M.name(), v.toString()));
        npeSafe(accountActivity::getActivityInc1W).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_INC_1W.name(), v.toString()));
        npeSafe(accountActivity::getActivity1W).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_1W.name(), v.toString()));
        npeSafe(accountActivity::getActivity3M).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_3M.name(), v.toString()));
        npeSafe(accountActivity::getActivity1M).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_1M.name(), v.toString()));
        npeSafe(accountActivity::getTrnsPerDay).ifPresent(v -> attributes.put(ACCOUNT_TRNS_PER_DAY.name(), v.toString()));
        npeSafe(accountActivity::getActivityInc12M).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_INC_12M.name(), v.toString()));
        npeSafe(accountActivity::getDaysSinceLastTrn).ifPresent(v -> attributes.put(ACCOUNT_DAYS_SINCE_LAST_TRN.name(), v.toString()));
        npeSafe(accountActivity::getActivityOut12M).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_OUT_12M.name(), v.toString()));
        npeSafe(accountActivity::getActivityInc1M).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_INC_1M.name(), v.toString()));
        npeSafe(accountActivity::getActivityOut3M).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_OUT_3M.name(), v.toString()));
        npeSafe(accountActivity::getDaysOfOutTrns).ifPresent(v -> attributes.put(ACCOUNT_DAYS_OF_OUT_TRNS.name(), v.toString()));
        npeSafe(accountActivity::getActivityInc3M).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_INC_3M.name(), v.toString()));
        npeSafe(accountActivity::getActivityInc6M).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_INC_6M.name(), v.toString()));
        npeSafe(accountActivity::getDaysOfTrns).ifPresent(v -> attributes.put(ACCOUNT_DAYS_OF_TRNS.name(), v.toString()));
        npeSafe(accountActivity::getActivityOut6M).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_OUT_6M.name(), v.toString()));
        npeSafe(accountActivity::getActivityOut1W).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_OUT_1W.name(), v.toString()));
        npeSafe(accountActivity::getActivityOut1M).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_OUT_1M.name(), v.toString()));
        npeSafe(accountActivity::getDaysSinceFirstTrn).ifPresent(v -> attributes.put(ACCOUNT_DAYS_SINCE_FIRST_TRN.name(), v.toString()));
        npeSafe(accountActivity::getActivityOutTotal).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_OUT_TOTAL.name(), v.toString()));
        npeSafe(accountActivity::getActivityTotal).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_TOTAL.name(), v.toString()));
        npeSafe(accountActivity::getActivity6M).ifPresent(v -> attributes.put(ACCOUNT_ACTIVITY_6M.name(), v.toString()));
        npeSafe(accountActivity::getDaysOfIncTrns).ifPresent(v -> attributes.put(ACCOUNT_DAYS_OF_INC_TRNS.name(), v.toString()));
        return attributes;
    }

    private Map<String, String> parseOverdrafts(Overdrafts overdrafts) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(overdrafts::getNumDaysInOverdraft1W).ifPresent(v -> attributes.put(NUM_DAYS_IN_OVERDRAFT_1W.name(), v.toString()));
        npeSafe(overdrafts::getNumDaysInOverdraft1M).ifPresent(v -> attributes.put(NUM_DAYS_IN_OVERDRAFT_1M.name(), v.toString()));
        npeSafe(overdrafts::getNumDaysInOverdraft3M).ifPresent(v -> attributes.put(NUM_DAYS_IN_OVERDRAFT_3M.name(), v.toString()));
        npeSafe(overdrafts::getNumDaysInOverdraft6M).ifPresent(v -> attributes.put(NUM_DAYS_IN_OVERDRAFT_6M.name(), v.toString()));
        npeSafe(overdrafts::getNumDaysInOverdraft12M).ifPresent(v -> attributes.put(NUM_DAYS_IN_OVERDRAFT_12M.name(), v.toString()));
        npeSafe(overdrafts::getNumDaysInOverdraftTotal).ifPresent(v -> attributes.put(NUM_DAYS_IN_OVERDRAFT_TOTAL.name(), v.toString()));
        npeSafe(overdrafts::getNumOverdraft1W).ifPresent(v -> attributes.put(NUM_OVERDRAFT_1W.name(), v.toString()));
        npeSafe(overdrafts::getNumOverdraft1M).ifPresent(v -> attributes.put(NUM_OVERDRAFT_1M.name(), v.toString()));
        npeSafe(overdrafts::getNumOverdraft3M).ifPresent(v -> attributes.put(NUM_OVERDRAFT_3M.name(), v.toString()));
        npeSafe(overdrafts::getNumOverdraft6M).ifPresent(v -> attributes.put(NUM_OVERDRAFT_6M.name(), v.toString()));
        npeSafe(overdrafts::getNumOverdraft12M).ifPresent(v -> attributes.put(NUM_OVERDRAFT_12M.name(), v.toString()));
        npeSafe(overdrafts::getNumOverdraftTotal).ifPresent(v -> attributes.put(NUM_OVERDRAFT_TOTAL.name(), v.toString()));
        return attributes;
    }

    private Map<String, String> parseAccountOverview(AccountOverview accountOverview) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(accountOverview::getNumAccounts).ifPresent(v -> attributes.put(ACCOUNT_OVERVIEW_NUM_ACCOUNTS.name(), v.toString()));
        npeSafe(accountOverview::getNumAccountTypes).ifPresent(v -> attributes.put(ACCOUNT_OVERVIEW_NUM_ACCOUNT_TYPES.name(), v.toString()));
        npeSafe(accountOverview::getNumAccountHolders).ifPresent(v -> attributes.put(ACCOUNT_OVERVIEW_NUM_ACCOUNT_HOLDERS.name(), v.toString()));
        npeSafe(accountOverview::getNumCurrencies).ifPresent(v -> attributes.put(ACCOUNT_OVERVIEW_NUM_CURRENCIES.name(), v.toString()));
        return attributes;
    }

    private Map<String, String> parseBalances(Balances balances) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(balances::getMeanBalance1W).ifPresent(v -> attributes.put(BALANCES_MEAN_BALANCE_1W.name(), v.toString()));
        npeSafe(balances::getMeanBalance1M).ifPresent(v -> attributes.put(BALANCES_MEAN_BALANCE_1M.name(), v.toString()));
        npeSafe(balances::getMeanBalance3M).ifPresent(v -> attributes.put(BALANCES_MEAN_BALANCE_3M.name(), v.toString()));
        npeSafe(balances::getMeanBalance6M).ifPresent(v -> attributes.put(BALANCES_MEAN_BALANCE_6M.name(), v.toString()));
        npeSafe(balances::getMeanBalance12M).ifPresent(v -> attributes.put(BALANCES_MEAN_BALANCE_12M.name(), v.toString()));
        npeSafe(balances::getMeanBalanceTotal).ifPresent(v -> attributes.put(BALANCES_MEAN_BALANCE_TOTAL.name(), v.toString()));

        npeSafe(balances::getMinBalance1W).ifPresent(v -> attributes.put(BALANCES_MIN_BALANCE_1W.name(), v.toString()));
        npeSafe(balances::getMinBalance1M).ifPresent(v -> attributes.put(BALANCES_MIN_BALANCE_1M.name(), v.toString()));
        npeSafe(balances::getMinBalance3M).ifPresent(v -> attributes.put(BALANCES_MIN_BALANCE_3M.name(), v.toString()));
        npeSafe(balances::getMinBalance6M).ifPresent(v -> attributes.put(BALANCES_MIN_BALANCE_6M.name(), v.toString()));
        npeSafe(balances::getMinBalance12M).ifPresent(v -> attributes.put(BALANCES_MIN_BALANCE_12M.name(), v.toString()));
        npeSafe(balances::getMinBalanceTotal).ifPresent(v -> attributes.put(BALANCES_MIN_BALANCE_TOTAL.name(), v.toString()));

        npeSafe(balances::getFirstBalance).ifPresent(v -> attributes.put(BALANCES_FIRST_BALANCE.name(), v.toString()));
        npeSafe(balances::getLastBalance).ifPresent(v -> attributes.put(BALANCES_LAST_BALANCE.name(), v.toString()));
        npeSafe(balances::getChangeInFirstLastBalance).ifPresent(v -> attributes.put(BALANCES_CHANGE_IN_FIRST_LAST_BALANCE.name(), v.toString()));

        npeSafe(balances::getMaxBalance1W).ifPresent(v -> attributes.put(BALANCES_MAX_BALANCE_1W.name(), v.toString()));
        npeSafe(balances::getMaxBalance1M).ifPresent(v -> attributes.put(BALANCES_MAX_BALANCE_1M.name(), v.toString()));
        npeSafe(balances::getMaxBalance3M).ifPresent(v -> attributes.put(BALANCES_MAX_BALANCE_3M.name(), v.toString()));
        npeSafe(balances::getMaxBalance6M).ifPresent(v -> attributes.put(BALANCES_MAX_BALANCE_6M.name(), v.toString()));
        npeSafe(balances::getMaxBalance12M).ifPresent(v -> attributes.put(BALANCES_MAX_BALANCE_12M.name(), v.toString()));
        npeSafe(balances::getMaxBalanceTotal).ifPresent(v -> attributes.put(BALANCES_MAX_BALANCE_TOTAL.name(), v.toString()));

        npeSafe(balances::getStdBalance1W).ifPresent(v -> attributes.put(BALANCES_STD_BALANCE_1W.name(), v.toString()));
        npeSafe(balances::getStdBalance1M).ifPresent(v -> attributes.put(BALANCES_STD_BALANCE_1M.name(), v.toString()));
        npeSafe(balances::getStdBalance3M).ifPresent(v -> attributes.put(BALANCES_STD_BALANCE_3M.name(), v.toString()));
        npeSafe(balances::getStdBalance6M).ifPresent(v -> attributes.put(BALANCES_STD_BALANCE_6M.name(), v.toString()));
        npeSafe(balances::getStdBalance12M).ifPresent(v -> attributes.put(BALANCES_STD_BALANCE_12M.name(), v.toString()));
        npeSafe(balances::getStdBalanceTotal).ifPresent(v -> attributes.put(BALANCES_STD_BALANCE_TOTAL.name(), v.toString()));
        return attributes;
    }

    private Map<String, String> parseTransactionsStats(TransactionStats transactionStats) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(transactionStats::getNumUniqueIncTrns1W).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_INC_TRNS_1W.name(), v.toString()));
        npeSafe(transactionStats::getNumUniqueIncTrns1M).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_INC_TRNS_1M.name(), v.toString()));
        npeSafe(transactionStats::getNumUniqueIncTrns3M).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_INC_TRNS_3M.name(), v.toString()));
        npeSafe(transactionStats::getNumUniqueIncTrns6M).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_INC_TRNS_6M.name(), v.toString()));
        npeSafe(transactionStats::getNumUniqueIncTrns12M).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_INC_TRNS_12M.name(), v.toString()));
        npeSafe(transactionStats::getNumUniqueIncTrnsTotal).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_INC_TRNS_TOTAL.name(), v.toString()));

        npeSafe(transactionStats::getNumUniqueTrns1W).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_TRNS_1W.name(), v.toString()));
        npeSafe(transactionStats::getNumUniqueTrns1M).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_TRNS_1M.name(), v.toString()));
        npeSafe(transactionStats::getNumUniqueTrns3M).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_TRNS_3M.name(), v.toString()));
        npeSafe(transactionStats::getNumUniqueTrns6M).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_TRNS_6M.name(), v.toString()));
        npeSafe(transactionStats::getNumUniqueTrns12M).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_TRNS_12M.name(), v.toString()));
        npeSafe(transactionStats::getNumUniqueTrnsTotal).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_TRNS_TOTAL.name(), v.toString()));

        npeSafe(transactionStats::getNumUniqueOutTrns1W).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_OUT_TRNS_1W.name(), v.toString()));
        npeSafe(transactionStats::getNumUniqueOutTrns1M).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_OUT_TRNS_1M.name(), v.toString()));
        npeSafe(transactionStats::getNumUniqueOutTrns3M).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_OUT_TRNS_3M.name(), v.toString()));
        npeSafe(transactionStats::getNumUniqueOutTrns6M).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_OUT_TRNS_6M.name(), v.toString()));
        npeSafe(transactionStats::getNumUniqueOutTrns12M).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_OUT_TRNS_12M.name(), v.toString()));
        npeSafe(transactionStats::getNumUniqueOutTrnsTotal).ifPresent(v -> attributes.put(TX_STATS_NUM_UNIQUE_OUT_TRNS_TOTAL.name(), v.toString()));

        npeSafe(transactionStats::getMeanAmount1W).ifPresent(v -> attributes.put(TX_STATS_MEAN_AMOUNT_1W.name(), v.toString()));
        npeSafe(transactionStats::getMeanAmount1M).ifPresent(v -> attributes.put(TX_STATS_MEAN_AMOUNT_1M.name(), v.toString()));
        npeSafe(transactionStats::getMeanAmount3M).ifPresent(v -> attributes.put(TX_STATS_MEAN_AMOUNT_3M.name(), v.toString()));
        npeSafe(transactionStats::getMeanAmount6M).ifPresent(v -> attributes.put(TX_STATS_MEAN_AMOUNT_6M.name(), v.toString()));
        npeSafe(transactionStats::getMeanAmount12M).ifPresent(v -> attributes.put(TX_STATS_MEAN_AMOUNT_12M.name(), v.toString()));
        npeSafe(transactionStats::getMeanAmountTotal).ifPresent(v -> attributes.put(TX_STATS_MEAN_AMOUNT_TOTAL.name(), v.toString()));

        npeSafe(transactionStats::getNumIncTrns1W).ifPresent(v -> attributes.put(TX_STATS_NUM_INC_TRNS_1W.name(), v.toString()));
        npeSafe(transactionStats::getNumIncTrns1M).ifPresent(v -> attributes.put(TX_STATS_NUM_INC_TRNS_1M.name(), v.toString()));
        npeSafe(transactionStats::getNumIncTrns3M).ifPresent(v -> attributes.put(TX_STATS_NUM_INC_TRNS_3M.name(), v.toString()));
        npeSafe(transactionStats::getNumIncTrns6M).ifPresent(v -> attributes.put(TX_STATS_NUM_INC_TRNS_6M.name(), v.toString()));
        npeSafe(transactionStats::getNumIncTrns12M).ifPresent(v -> attributes.put(TX_STATS_NUM_INC_TRNS_12M.name(), v.toString()));
        npeSafe(transactionStats::getNumIncTrnsTotal).ifPresent(v -> attributes.put(TX_STATS_NUM_INC_TRNS_TOTAL.name(), v.toString()));

        npeSafe(transactionStats::getNumTrns1W).ifPresent(v -> attributes.put(TX_STATS_NUM_TRNS_1W.name(), v.toString()));
        npeSafe(transactionStats::getNumTrns1M).ifPresent(v -> attributes.put(TX_STATS_NUM_TRNS_1M.name(), v.toString()));
        npeSafe(transactionStats::getNumTrns3M).ifPresent(v -> attributes.put(TX_STATS_NUM_TRNS_3M.name(), v.toString()));
        npeSafe(transactionStats::getNumTrns6M).ifPresent(v -> attributes.put(TX_STATS_NUM_TRNS_6M.name(), v.toString()));
        npeSafe(transactionStats::getNumTrns12M).ifPresent(v -> attributes.put(TX_STATS_NUM_TRNS_12M.name(), v.toString()));
        npeSafe(transactionStats::getNumTrnsTotal).ifPresent(v -> attributes.put(TX_STATS_NUM_TRNS_TOTAL.name(), v.toString()));

        npeSafe(transactionStats::getMeanIncAmount1W).ifPresent(v -> attributes.put(TX_STATS_MEAN_INC_AMOUNT_1W.name(), v.toString()));
        npeSafe(transactionStats::getMeanIncAmount1M).ifPresent(v -> attributes.put(TX_STATS_MEAN_INC_AMOUNT_1M.name(), v.toString()));
        npeSafe(transactionStats::getMeanIncAmount3M).ifPresent(v -> attributes.put(TX_STATS_MEAN_INC_AMOUNT_3M.name(), v.toString()));
        npeSafe(transactionStats::getMeanIncAmount6M).ifPresent(v -> attributes.put(TX_STATS_MEAN_INC_AMOUNT_6M.name(), v.toString()));
        npeSafe(transactionStats::getMeanIncAmount12M).ifPresent(v -> attributes.put(TX_STATS_MEAN_INC_AMOUNT_12M.name(), v.toString()));
        npeSafe(transactionStats::getMeanIncAmountTotal).ifPresent(v -> attributes.put(TX_STATS_MEAN_INC_AMOUNT_TOTAL.name(), v.toString()));

        npeSafe(transactionStats::getSumAmounts1W).ifPresent(v -> attributes.put(TX_STATS_SUM_AMOUNTS_1W.name(), v.toString()));
        npeSafe(transactionStats::getSumAmounts1M).ifPresent(v -> attributes.put(TX_STATS_SUM_AMOUNTS_1M.name(), v.toString()));
        npeSafe(transactionStats::getSumAmounts3M).ifPresent(v -> attributes.put(TX_STATS_SUM_AMOUNTS_3M.name(), v.toString()));
        npeSafe(transactionStats::getSumAmounts6M).ifPresent(v -> attributes.put(TX_STATS_SUM_AMOUNTS_6M.name(), v.toString()));
        npeSafe(transactionStats::getSumAmounts12M).ifPresent(v -> attributes.put(TX_STATS_SUM_AMOUNTS_12M.name(), v.toString()));
        npeSafe(transactionStats::getSumAmountsTotal).ifPresent(v -> attributes.put(TX_STATS_SUM_AMOUNTS_TOTAL.name(), v.toString()));

        npeSafe(transactionStats::getMaxIncAmount1W).ifPresent(v -> attributes.put(TX_STATS_MAX_INC_AMOUNT_1W.name(), v.toString()));
        npeSafe(transactionStats::getMaxIncAmount1M).ifPresent(v -> attributes.put(TX_STATS_MAX_INC_AMOUNT_1M.name(), v.toString()));
        npeSafe(transactionStats::getMaxIncAmount3M).ifPresent(v -> attributes.put(TX_STATS_MAX_INC_AMOUNT_3M.name(), v.toString()));
        npeSafe(transactionStats::getMaxIncAmount6M).ifPresent(v -> attributes.put(TX_STATS_MAX_INC_AMOUNT_6M.name(), v.toString()));
        npeSafe(transactionStats::getMaxIncAmount12M).ifPresent(v -> attributes.put(TX_STATS_MAX_INC_AMOUNT_12M.name(), v.toString()));
        npeSafe(transactionStats::getMaxIncAmountTotal).ifPresent(v -> attributes.put(TX_STATS_MAX_INC_AMOUNT_TOTAL.name(), v.toString()));

        npeSafe(transactionStats::getMeanOutAmount1W).ifPresent(v -> attributes.put(TX_STATS_MEAN_OUT_AMOUNT_1W.name(), v.toString()));
        npeSafe(transactionStats::getMeanOutAmount1M).ifPresent(v -> attributes.put(TX_STATS_MEAN_OUT_AMOUNT_1M.name(), v.toString()));
        npeSafe(transactionStats::getMeanOutAmount3M).ifPresent(v -> attributes.put(TX_STATS_MEAN_OUT_AMOUNT_3M.name(), v.toString()));
        npeSafe(transactionStats::getMeanOutAmount6M).ifPresent(v -> attributes.put(TX_STATS_MEAN_OUT_AMOUNT_6M.name(), v.toString()));
        npeSafe(transactionStats::getMeanOutAmount12M).ifPresent(v -> attributes.put(TX_STATS_MEAN_OUT_AMOUNT_12M.name(), v.toString()));
        npeSafe(transactionStats::getMeanOutAmountTotal).ifPresent(v -> attributes.put(TX_STATS_MEAN_OUT_AMOUNT_TOTAL.name(), v.toString()));

        npeSafe(transactionStats::getNumOutTrns1W).ifPresent(v -> attributes.put(TX_STATS_NUM_OUT_TRNS_1W.name(), v.toString()));
        npeSafe(transactionStats::getNumOutTrns1M).ifPresent(v -> attributes.put(TX_STATS_NUM_OUT_TRNS_1M.name(), v.toString()));
        npeSafe(transactionStats::getNumOutTrns3M).ifPresent(v -> attributes.put(TX_STATS_NUM_OUT_TRNS_3M.name(), v.toString()));
        npeSafe(transactionStats::getNumOutTrns6M).ifPresent(v -> attributes.put(TX_STATS_NUM_OUT_TRNS_6M.name(), v.toString()));
        npeSafe(transactionStats::getNumOutTrns12M).ifPresent(v -> attributes.put(TX_STATS_NUM_OUT_TRNS_12M.name(), v.toString()));
        npeSafe(transactionStats::getNumOutTrnsTotal).ifPresent(v -> attributes.put(TX_STATS_NUM_OUT_TRNS_TOTAL.name(), v.toString()));

        npeSafe(transactionStats::getSumIncAmounts1W).ifPresent(v -> attributes.put(TX_STATS_SUM_INC_AMOUNTS_1W.name(), v.toString()));
        npeSafe(transactionStats::getSumIncAmounts1M).ifPresent(v -> attributes.put(TX_STATS_SUM_INC_AMOUNTS_1M.name(), v.toString()));
        npeSafe(transactionStats::getSumIncAmounts3M).ifPresent(v -> attributes.put(TX_STATS_SUM_INC_AMOUNTS_3M.name(), v.toString()));
        npeSafe(transactionStats::getSumIncAmounts6M).ifPresent(v -> attributes.put(TX_STATS_SUM_INC_AMOUNTS_6M.name(), v.toString()));
        npeSafe(transactionStats::getSumIncAmounts12M).ifPresent(v -> attributes.put(TX_STATS_SUM_INC_AMOUNTS_12M.name(), v.toString()));
        npeSafe(transactionStats::getSumIncAmountsTotal).ifPresent(v -> attributes.put(TX_STATS_SUM_INC_AMOUNTS_TOTAL.name(), v.toString()));

        npeSafe(transactionStats::getSumOutAmounts1W).ifPresent(v -> attributes.put(TX_STATS_SUM_OUT_AMOUNTS_1W.name(), v.toString()));
        npeSafe(transactionStats::getSumOutAmounts1M).ifPresent(v -> attributes.put(TX_STATS_SUM_OUT_AMOUNTS_1M.name(), v.toString()));
        npeSafe(transactionStats::getSumOutAmounts3M).ifPresent(v -> attributes.put(TX_STATS_SUM_OUT_AMOUNTS_3M.name(), v.toString()));
        npeSafe(transactionStats::getSumOutAmounts6M).ifPresent(v -> attributes.put(TX_STATS_SUM_OUT_AMOUNTS_6M.name(), v.toString()));
        npeSafe(transactionStats::getSumOutAmounts12M).ifPresent(v -> attributes.put(TX_STATS_SUM_OUT_AMOUNTS_12M.name(), v.toString()));
        npeSafe(transactionStats::getSumOutAmountsTotal).ifPresent(v -> attributes.put(TX_STATS_SUM_OUT_AMOUNTS_TOTAL.name(), v.toString()));

        npeSafe(transactionStats::getMaxOutAmount1W).ifPresent(v -> attributes.put(TX_STATS_MAX_OUT_AMOUNT_1W.name(), v.toString()));
        npeSafe(transactionStats::getMaxOutAmount1M).ifPresent(v -> attributes.put(TX_STATS_MAX_OUT_AMOUNT_1M.name(), v.toString()));
        npeSafe(transactionStats::getMaxOutAmount3M).ifPresent(v -> attributes.put(TX_STATS_MAX_OUT_AMOUNT_3M.name(), v.toString()));
        npeSafe(transactionStats::getMaxOutAmount6M).ifPresent(v -> attributes.put(TX_STATS_MAX_OUT_AMOUNT_6M.name(), v.toString()));
        npeSafe(transactionStats::getMaxOutAmount12M).ifPresent(v -> attributes.put(TX_STATS_MAX_OUT_AMOUNT_12M.name(), v.toString()));
        npeSafe(transactionStats::getMaxOutAmountTotal).ifPresent(v -> attributes.put(TX_STATS_MAX_OUT_AMOUNT_TOTAL.name(), v.toString()));

        return attributes;
    }

    private Optional<AccountReport> getAccountReport(InstantorInsightResponse response, String primaryBankAccount) {
        Optional<Account> account = getAccount(response, primaryBankAccount);
        if (account.isPresent()) {
            return response.getAccountReportList().stream().filter(a -> IbanUtils.equals(a.getNumber(), account.get().getNumber())).findAny();
        } else {
            return Optional.empty();
        }
    }

    private Optional<Account> getAccount(InstantorInsightResponse response, String primaryBankAccount) {
        return response.getAccountList().stream().filter(a -> IbanUtils.equals(a.getIban(), primaryBankAccount)).findAny();
    }

    private List<InstantorAccountEntity> parseInstantorAccounts(InstantorResponseEntity entity, InstantorInsightResponse response) {
        List<Account> accounts = npeSafe(response::getAccountList).orElse(ImmutableList.of());
        return accounts.stream()
            .filter(a -> !StringUtils.isBlank(a.getIban()))
            .map(a -> parseInstantorAccount(entity, a))
            .collect(Collectors.toList());
    }

    private InstantorAccountEntity parseInstantorAccount(InstantorResponseEntity responseEntity, Account account) {
        InstantorAccountEntity accountEntity = new InstantorAccountEntity();
        accountEntity.setResponse(responseEntity);
        accountEntity.setClientId(responseEntity.getClientId());
        accountEntity.setAccountHolderName(account.getHolderName());
        accountEntity.setAccountNumber(getIbanFromAccount(account));
        accountEntity.setBalance(amount(account.getBalance()));
        accountEntity.setCurrency(account.getCurrency());

        List<InstantorTransactionEntity> transactions = Optional.ofNullable(account.getTransactionList())
            .orElse(java.util.Collections.emptyList())
            .stream()
            .map(t -> parseInstantorTransaction(accountEntity, t))
            .collect(Collectors.toList());

        accountEntity.setTransactions(transactions);
        return accountEntity;
    }

    private String getIbanFromAccount(Account account) {
        if (isIbanValid(account.getIban())) {
            return account.getIban();
        }
        if (isIbanValid(account.getNumber())) {
            return account.getNumber();
        }
        return account.getIban();
    }

    private InstantorTransactionEntity parseInstantorTransaction(InstantorAccountEntity accountEntity, Transaction transaction) {
        InstantorTransactionEntity transactionEntity = new InstantorTransactionEntity();
        transactionEntity.setResponse(accountEntity.getResponse());
        transactionEntity.setAccount(accountEntity);
        transactionEntity.setClientId(accountEntity.getResponse().getClientId());
        transactionEntity.setAccountNumber(normalizeIban(accountEntity.getAccountNumber()));
        transactionEntity.setAccountHolderName(MoreObjects.firstNonNull(accountEntity.getAccountHolderName(), StringUtils.EMPTY));
        transactionEntity.setCurrency(MoreObjects.firstNonNull(accountEntity.getCurrency(), StringUtils.EMPTY));
        transactionEntity.setDate(LocalDate.parse(transaction.getOnDate(), DateTimeFormatter.ISO_DATE));
        transactionEntity.setAmount(amount(transaction.getAmount()));
        transactionEntity.setBalance(amount(transaction.getBalance()));
        transactionEntity.setDescription(transaction.getDescription());
        return transactionEntity;
    }

    @Override
    public Map<String, String> parseAccountAttributes(InstantorResponseEntity response, String iban) {
        InstantorInsightResponse json = JsonUtils.readValue(response.getPayloadJson(), InstantorInsightResponse.class);
        return getAccountReport(json, iban).map(this::parseAccountReportsAttributes).orElse(java.util.Collections.emptyMap());
    }

    @Override
    public String getNameForVerification(InstantorResponseEntity response, String iban) {
        InstantorInsightResponse json = JsonUtils.readValue(response.getPayloadJson(), InstantorInsightResponse.class);
        return dataResolver.resolveName(json, iban);
    }
}
