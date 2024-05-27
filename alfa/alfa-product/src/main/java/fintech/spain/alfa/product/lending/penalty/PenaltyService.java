package fintech.spain.alfa.product.lending.penalty;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PenaltyService {

    BigDecimal calculatePenalty(Long loanId, LocalDate date);

    void applyPenalty(Long loanId, LocalDate from, LocalDate to);
    void applyPenalty(Long loanId, BigDecimal amount, LocalDate from, LocalDate to);

    void applyPenalty(Long loanId, LocalDate date);

    void applyPenalty(Long loanId, BigDecimal amount, LocalDate date);
}
