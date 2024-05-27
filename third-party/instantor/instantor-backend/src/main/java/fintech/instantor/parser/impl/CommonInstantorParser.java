package fintech.instantor.parser.impl;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import fintech.BigDecimalUtils;
import fintech.IbanUtils;
import fintech.JsonUtils;
import fintech.PojoUtils;
import fintech.Validate;
import fintech.instantor.db.InstantorAccountEntity;
import fintech.instantor.db.InstantorResponseEntity;
import fintech.instantor.db.InstantorTransactionEntity;
import fintech.instantor.json.common.AccountList;
import fintech.instantor.json.common.InstantorCommonResponse;
import fintech.instantor.json.common.TransactionList;
import fintech.instantor.model.InstantorResponseStatus;
import fintech.instantor.model.SaveInstantorResponseCommand;
import fintech.instantor.parser.InstantorDataResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.amount;
import static fintech.PojoUtils.npeSafe;
import static fintech.PojoUtils.safe;
import static fintech.instantor.model.InstantorResponseAttributes.AVERAGE_AMOUNT_OF_INCOMING_TRANSACTIONS_PER_MONTH;
import static fintech.instantor.model.InstantorResponseAttributes.AVERAGE_AMOUNT_OF_OUTGOING_TRANSACTIONS_PER_MONTH;
import static fintech.instantor.model.InstantorResponseAttributes.AVERAGE_AMOUNT_OF_TRANSACTIONS_PER_MONTH;
import static fintech.instantor.model.InstantorResponseAttributes.AVERAGE_MINIMUM_BALANCE_PER_MONTH;
import static fintech.instantor.model.InstantorResponseAttributes.BANK_NAME;
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_AMOUNT_OF_LOANS;
import static fintech.instantor.model.InstantorResponseAttributes.MONTHS_AVAILABLE;
import static fintech.instantor.model.InstantorResponseAttributes.THIS_MONTH_AMOUNT_OF_LOANS;
import static fintech.instantor.model.InstantorResponseAttributes.TOTAL_NUMBER_OF_TRANSACTIONS;

@Slf4j
@Deprecated
public class CommonInstantorParser extends AbstractInstantorParser {

    private static final int THIS_MONTH_CASH_FLOW_INDEX = 0;
    private static final int LAST_MONTH_CASH_FLOW_INDEX = 1;

    private final InstantorDataResolver<InstantorCommonResponse> dataResolver;

    public CommonInstantorParser(InstantorDataResolver<InstantorCommonResponse> dataResolver) {
        this.dataResolver = dataResolver;
    }

    protected void parseJsonInstantorData(InstantorResponseEntity entity, SaveInstantorResponseCommand command) {
        Validate.notBlank(command.getPayloadJson(), "Payload json is empty: [%s]", command);
        InstantorCommonResponse response = JsonUtils.readValue(command.getPayloadJson(), InstantorCommonResponse.class);
        String scrapeStatus = PojoUtils.npeSafe(() -> response.getBasicInfo().getScrapeReport().getStatus()).orElse(StringUtils.EMPTY);
        entity.setClientId(dataResolver.resolveClientId(response));

        if (!StringUtils.equalsIgnoreCase("ok", scrapeStatus)) {
            entity.setStatus(InstantorResponseStatus.FAILED);
            entity.setError("Process status: " + scrapeStatus);
        } else {
            if (entity.getClientId() == null) {
                entity.setStatus(InstantorResponseStatus.FAILED);
                entity.setError("Client id not resolved");
            } else {
                String accountNumbers = response.getScrape().getAccountList().stream().map(a -> StringUtils.upperCase(StringUtils.replace(a.getIban(), " ", ""))).collect(Collectors.joining(","));
                entity.setAccountNumbers(accountNumbers);
                entity.setNameForVerification(dataResolver.resolveName(response));
                entity.setPersonalNumberForVerification(dataResolver.resolvePersonalNumber(response));
                entity.setLatest(true);
                entity.setAttributes(parseInstantorAttributes(response));
                entity.setAccounts(parseInstantorAccounts(entity, response));
            }
        }
    }

    private Map<String, String> parseInstantorAttributes(InstantorCommonResponse response) {
        Map<String, String> attributes = new HashMap<>();
        npeSafe(() -> response.getBankReport().getAverageAmountOfOutgoingTransactionsMonth()).map(BigDecimalUtils::amount).map(BigDecimalUtils::abs).ifPresent(v -> attributes.put(AVERAGE_AMOUNT_OF_OUTGOING_TRANSACTIONS_PER_MONTH.name(), v.toString()));
        npeSafe(() -> response.getBankReport().getAverageAmountOfIncomingTransactionsMonth()).map(BigDecimalUtils::amount).map(BigDecimalUtils::abs).ifPresent(v -> attributes.put(AVERAGE_AMOUNT_OF_INCOMING_TRANSACTIONS_PER_MONTH.name(), v.toString()));
        npeSafe(() -> response.getBankReport().getAverageMinimumBalanceMonth()).map(BigDecimalUtils::amount).ifPresent(v -> attributes.put(AVERAGE_MINIMUM_BALANCE_PER_MONTH.name(), v.toString()));
        safe(() -> response.getBankReport().getCashFlow().get(THIS_MONTH_CASH_FLOW_INDEX).getAmountOfLoans()).ifPresent(v -> attributes.put(THIS_MONTH_AMOUNT_OF_LOANS.name(), v.toString()));
        safe(() -> response.getBankReport().getCashFlow().get(LAST_MONTH_CASH_FLOW_INDEX).getAmountOfLoans()).ifPresent(v -> attributes.put(LAST_MONTH_AMOUNT_OF_LOANS.name(), v.toString()));
        npeSafe(() -> response.getBankReport().getAverageNumberOfTransactionsMonth()).ifPresent(v -> attributes.put(AVERAGE_AMOUNT_OF_TRANSACTIONS_PER_MONTH.name(), v.toString()));
        npeSafe(() -> response.getBankReport().getMonthsAvailable()).ifPresent(v -> attributes.put(MONTHS_AVAILABLE.name(), v.toString()));
        npeSafe(() -> response.getBankReport().getTotalNumberOfTransactions()).ifPresent(v -> attributes.put(TOTAL_NUMBER_OF_TRANSACTIONS.name(), v.toString()));
        npeSafe(() -> response.getBankReport().getBank().getName()).ifPresent(v -> attributes.put(BANK_NAME.name(), v));
        return attributes;
    }

    private List<InstantorAccountEntity> parseInstantorAccounts(InstantorResponseEntity entity, InstantorCommonResponse response) {
        List<AccountList> accounts = PojoUtils.npeSafe(() -> response.getScrape().getAccountList()).orElse(ImmutableList.of());
        return accounts.stream().filter(a -> !StringUtils.isBlank(a.getIban())).map(a -> parseInstantorAccount(entity, a)).collect(Collectors.toList());
    }

    private InstantorAccountEntity parseInstantorAccount(InstantorResponseEntity responseEntity, AccountList accountList) {
        InstantorAccountEntity accountEntity = new InstantorAccountEntity();
        accountEntity.setResponse(responseEntity);
        accountEntity.setClientId(responseEntity.getClientId());
        accountEntity.setAccountHolderName(accountList.getHolderName());
        accountEntity.setAccountNumber(accountList.getIban());
        accountEntity.setBalance(amount(accountList.getBalance()));
        accountEntity.setCurrency(accountList.getCurrency());

        List<InstantorTransactionEntity> transactions = Optional.ofNullable(accountList.getTransactionList())
            .orElse(Collections.emptyList())
            .stream()
            .map(t -> parseInstantorTransaction(accountEntity, t))
            .collect(Collectors.toList());

        accountEntity.setTransactions(transactions);
        return accountEntity;
    }

    private InstantorTransactionEntity parseInstantorTransaction(InstantorAccountEntity accountEntity, TransactionList transactionList) {
        InstantorTransactionEntity transactionEntity = new InstantorTransactionEntity();
        transactionEntity.setResponse(accountEntity.getResponse());
        transactionEntity.setAccount(accountEntity);
        transactionEntity.setClientId(accountEntity.getResponse().getClientId());
        transactionEntity.setAccountNumber(IbanUtils.normalizeIban(accountEntity.getAccountNumber()));
        transactionEntity.setAccountHolderName(MoreObjects.firstNonNull(accountEntity.getAccountHolderName(), StringUtils.EMPTY));
        transactionEntity.setCurrency(MoreObjects.firstNonNull(accountEntity.getCurrency(), StringUtils.EMPTY));
        transactionEntity.setDate(LocalDate.parse(transactionList.getOnDate(), DateTimeFormatter.ISO_DATE_TIME));
        transactionEntity.setAmount(amount(transactionList.getAmount()));
        transactionEntity.setBalance(amount(transactionList.getBalance()));
        transactionEntity.setDescription(transactionList.getDescription());
        return transactionEntity;
    }

    @Override
    public Map<String, String> parseAccountAttributes(InstantorResponseEntity response, String iban) {
        return Collections.emptyMap();
    }

    @Override
    public String getNameForVerification(InstantorResponseEntity response, String iban) {
        InstantorCommonResponse json = JsonUtils.readValue(response.getPayloadJson(), InstantorCommonResponse.class);
        return dataResolver.resolveName(json, iban);
    }
}
