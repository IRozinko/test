package fintech.instantor;

import fintech.instantor.model.InstantorResponse;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
public class InstantorScoringValues {

    private String personalNumberForVerification;
    private String nameForVerification;
    private String accountNumbers;
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

    public InstantorScoringValues(InstantorResponse response) {
        personalNumberForVerification = response.getPersonalNumberForVerification();
        nameForVerification = response.getNameForVerification();
        accountNumbers = response.getAccountNumbers();
        averageAmountOfOutgoingTransactionsPerMonth = response.getAverageAmountOfOutgoingTransactionsPerMonth();
        averageAmountOfIncomingTransactionsPerMonth = response.getAverageAmountOfIncomingTransactionsPerMonth();
        averageNumberOfTransactionsPerMonth = response.getAverageNumberOfTransactionsPerMonth();
        thisMonthAmountOfLoans = response.getThisMonthAmountOfLoans();
        lastMonthAmountOfLoans = response.getLastMonthAmountOfLoans();
        monthsAvailable = response.getMonthsAvailable();
        totalNumberOfTransactions = response.getTotalNumberOfTransactions();
        bankName = response.getBankName();
        averageMinimumBalancePerMonth = response.getAverageMinimumBalancePerMonth();
        attributes = response.getAttributes();
    }
}
