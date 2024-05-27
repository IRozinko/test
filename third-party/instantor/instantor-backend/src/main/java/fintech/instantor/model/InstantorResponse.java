package fintech.instantor.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class InstantorResponse {

    private Long id;
    private Long clientId;
    private InstantorResponseStatus status;
    private LocalDateTime createdAt;
    private String personalNumberForVerification;
    private String nameForVerification;
    private String accountNumbers;
    private List<InstantorAccount> accounts;
    private BigDecimal averageAmountOfOutgoingTransactionsPerMonth;
    private BigDecimal averageAmountOfIncomingTransactionsPerMonth;
    private BigDecimal averageNumberOfTransactionsPerMonth;
    private BigDecimal thisMonthAmountOfLoans;
    private BigDecimal lastMonthAmountOfLoans;
    private Integer monthsAvailable;
    private Integer totalNumberOfTransactions;
    private String bankName;
    private BigDecimal averageMinimumBalancePerMonth;
    private Map<String, String> attributes = new HashMap<>();

    public List<InstantorAccount> getAccountsWithTransactions() {
        return getAccounts().stream().filter(a -> a.getTransactionCount() > 0).collect(Collectors.toList());
    }

    //F** inherited logic , have no idea why we need this logic
    public List<InstantorAccount> getValidAccounts() {
        List<InstantorAccount> accountsWithTransactions = getAccountsWithTransactions();
        if (accountsWithTransactions.isEmpty()) {
            return getAccounts();
        } else {
            return accountsWithTransactions;
        }
    }

    public Optional<String> getAttribute(InstantorResponseAttributes attribute) {
        return Optional.ofNullable(attributes.get(attribute.name()));
    }
}
