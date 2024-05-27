package fintech.lending.core.periods.spi;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

public interface PeriodClosingStrategy {

    @AllArgsConstructor
    @Data
    class PeriodClosingResult {
        private String resultLog;
    }

    PeriodClosingResult closePeriod(LocalDate periodDate);

}
