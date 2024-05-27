package fintech.lending.core.penalty;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PenaltyStrategy {

    @Data
    @AllArgsConstructor
    class CalculatedPenalty {
        private BigDecimal penalty;
        private String comments;

        public static CalculatedPenalty noPenalty(String comments) {
            return new CalculatedPenalty(BigDecimal.ZERO, comments);
        }
    }

    CalculatedPenalty calculate(Long loanId, LocalDate when);

}
