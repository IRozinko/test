package fintech.lending.core.periods.impl;

import fintech.lending.core.periods.spi.PeriodClosingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Lazy
@Component
@Transactional(propagation = Propagation.NEVER)
@Slf4j
public class DefaultPeriodClosingStrategy implements PeriodClosingStrategy {

    @Override
    public PeriodClosingResult closePeriod(LocalDate periodDate) {
        log.info("Taking no actions on period {} closing", periodDate);

        return new PeriodClosingResult("No actions taken");
    }

}
