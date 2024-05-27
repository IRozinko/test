package fintech.spain.alfa.product.strategy.fee;

import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.spain.alfa.product.db.AlfaFeeStrategyEntity;
import fintech.spain.alfa.product.db.AlfaFeeStrategyRepository;
import fintech.spain.alfa.product.db.Entities;
import fintech.spain.alfa.strategy.CalculationType;
import fintech.strategy.spi.FeeStrategy;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

import static fintech.BigDecimalUtils.amount;

@RequiredArgsConstructor
public class AlfaFeeStrategy implements FeeStrategy {

    public static final CalculationType CALCULATION_TYPE = CalculationType.FT;

    private final AlfaFeeStrategyRepository alfaFeeStrategyRepository;
    private final LoanService loanService;
    @Override
    public BigDecimal calculate(Long loanId, String company) {
        Loan loan = loanService.getLoan(loanId);
        Optional<AlfaFeeStrategyEntity> maybeFeeStrategy = alfaFeeStrategyRepository.findFirst(Entities.feeStrategy.calculationStrategyId.eq(loan.getFeeStrategyId()).and(Entities.feeStrategy.company.eq(company)));
        return maybeFeeStrategy.map(AlfaFeeStrategyEntity::getFeeRate).orElse(amount(0));
    }

}
