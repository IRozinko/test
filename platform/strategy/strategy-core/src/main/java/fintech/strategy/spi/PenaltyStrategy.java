package fintech.strategy.spi;

import fintech.lending.core.loan.Installment;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PenaltyStrategy {

    BigDecimal calculate(Installment installment, LocalDate calculationDate);
}
