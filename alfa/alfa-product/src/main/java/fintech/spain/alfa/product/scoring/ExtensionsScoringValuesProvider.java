package fintech.spain.alfa.product.scoring;

import fintech.ScoringProperties;
import fintech.scoring.values.spi.ScoringValuesProvider;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LongSummaryStatistics;
import java.util.Properties;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.amount;
import static fintech.transactions.TransactionQuery.byClient;
import static fintech.transactions.TransactionType.LOAN_EXTENSION;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExtensionsScoringValuesProvider implements ScoringValuesProvider {

    private static final String HISTORIC_EXTENSIONS_BY_DAYS_PREFIX = "historic_extensions_by_days";
    private static final String MAX = "max";
    private static final String MIN = "min";
    private static final String SUM = "sum";
    private static final String AVG = "avg";

    private final TransactionService transactionService;

    @Override
    public Properties provide(long clientId) {
        LongSummaryStatistics stats = transactionService.findTransactions(byClient(clientId, LOAN_EXTENSION))
            .stream()
            .collect(Collectors.summarizingLong(Transaction::getExtensionDays));

        ScoringProperties properties = new ScoringProperties(HISTORIC_EXTENSIONS_BY_DAYS_PREFIX);
        properties.put(MAX, stats.getCount() == 0 ? null : stats.getMax());
        properties.put(MIN, stats.getCount() == 0 ? null : stats.getMin());
        properties.put(SUM, stats.getSum());
        properties.put(AVG, amount(stats.getAverage()));
        return properties;
    }

}
