package fintech.spain.alfa.strategy.penalty;

import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Data
@Accessors(chain = true)
public class DpdPenaltyStrategyProperties {

    List<PenaltyStrategy> strategies;

    public DpdPenaltyStrategyProperties() {
        this.strategies = new ArrayList<>();
        this.strategies.add(new PenaltyStrategy());
    }

    public BigDecimal getRateFor(int daysOverdue) {
        if (strategies == null || strategies.isEmpty()) {
            throw new RuntimeException("Penalty strategy is`nt configured properly");
        }
        return strategies.stream()
            .filter(ps -> ps.getFrom() <= daysOverdue)
            .max(Comparator.comparing(PenaltyStrategy::getFrom))
            .map(PenaltyStrategy::getRate)
            .orElse(BigDecimal.ZERO);
    }

    @Getter
    @Setter
    @EqualsAndHashCode(of = "id")
    public static class PenaltyStrategy {

        @Getter(AccessLevel.NONE)
        private final Long id = new Random().nextLong();

        private Integer from;
        private BigDecimal rate;

    }

}
