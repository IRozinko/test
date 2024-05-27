package fintech.instantor.model;

import fintech.instantor.json.insight.CashFlow;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Iterables.concat;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CashflowAttributes {

    private final List<String> calendarMonth;
    private final List<BigDecimal> incoming;
    private final List<BigDecimal> outgoing;
    private final List<BigDecimal> minBalance;
    private final List<BigDecimal> maxBalance;
    private final List<BigDecimal> avgBalance;
    private final List<Boolean> isWholeMonth;

    public CashflowAttributes() {
        calendarMonth = Collections.emptyList();
        incoming = Collections.emptyList();
        outgoing = Collections.emptyList();
        minBalance = Collections.emptyList();
        maxBalance = Collections.emptyList();
        avgBalance = Collections.emptyList();
        isWholeMonth = Collections.emptyList();
    }

    public CashflowAttributes(CashFlow cashFlow) {
        calendarMonth = of(cashFlow.getCalendarMonth());
        incoming = of(cashFlow.getIncoming());
        outgoing = of(cashFlow.getOutgoing());
        minBalance = of(cashFlow.getMinBalance());
        maxBalance = of(cashFlow.getMaxBalance());
        avgBalance = of(cashFlow.getAvgBalance());
        isWholeMonth = of(cashFlow.getIsWholeMonth());
    }

    public CashflowAttributes add(CashflowAttributes attributes) {
        return new CashflowAttributes(
            copyOf(concat(calendarMonth, attributes.calendarMonth)),
            copyOf(concat(incoming, attributes.incoming)),
            copyOf(concat(outgoing, attributes.outgoing)),
            copyOf(concat(minBalance, attributes.minBalance)),
            copyOf(concat(maxBalance, attributes.maxBalance)),
            copyOf(concat(avgBalance, attributes.avgBalance)),
            copyOf(concat(isWholeMonth, attributes.isWholeMonth))
        );
    }


}
