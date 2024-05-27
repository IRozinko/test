package fintech.spain.alfa.product.scoring;

import fintech.ScoringProperties;
import fintech.scoring.values.spi.ScoringValuesProvider;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static fintech.transactions.TransactionQuery.notVoidedByClient;
import static fintech.transactions.TransactionType.REPAYMENT;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionsScoringValuesProvider implements ScoringValuesProvider {

    private static final String ALL_REPAID_HISTORIC_PRINCIPAL_BY_TRANSACTIONS = "all_repaid_historic_principal_by_transactions";
    private static final String ALL_REPAID_HISTORIC_INTEREST_BY_TRANSACTIONS = "all_repaid_historic_interest_by_transactions";
    private static final String ALL_REPAID_HISTORIC_PENALTIES_BY_TRANSACTIONS = "all_repaid_historic_penalties_by_transactions";
    private static final String ALL_REPAID_HISTORIC_FEES_BY_TRANSACTIONS = "all_repaid_historic_fees_by_transactions";

    private final TransactionService transactionService;

    @Override
    public Properties provide(long clientId) {
        List<Transaction> transactions = transactionService.findTransactions(notVoidedByClient(clientId, REPAYMENT));

        List<BigDecimal> principalPaid = new ArrayList<>(transactions.size());
        List<BigDecimal> interestsPaid = new ArrayList<>(transactions.size());
        List<BigDecimal> penaltiesPaid = new ArrayList<>(transactions.size());
        List<BigDecimal> feesPaid = new ArrayList<>(transactions.size());

        transactions.forEach(tx -> {
            principalPaid.add(tx.getPrincipalPaid());
            interestsPaid.add(tx.getInterestPaid());
            penaltiesPaid.add(tx.getPenaltyPaid());
            feesPaid.add(tx.getFeePaid());
        });

        ScoringProperties properties = new ScoringProperties();
        properties.put(ALL_REPAID_HISTORIC_PRINCIPAL_BY_TRANSACTIONS, principalPaid);
        properties.put(ALL_REPAID_HISTORIC_INTEREST_BY_TRANSACTIONS, interestsPaid);
        properties.put(ALL_REPAID_HISTORIC_PENALTIES_BY_TRANSACTIONS, penaltiesPaid);
        properties.put(ALL_REPAID_HISTORIC_FEES_BY_TRANSACTIONS, feesPaid);
        return properties;
    }

}
