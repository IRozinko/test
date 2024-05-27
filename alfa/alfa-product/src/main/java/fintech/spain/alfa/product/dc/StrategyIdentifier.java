package fintech.spain.alfa.product.dc;


import fintech.lending.core.loan.Loan;

public interface StrategyIdentifier {

    void applyFee(Loan loan);

    void applyPenalty(Loan loan);

}
