package fintech.lending.core.loan.events;

import fintech.lending.core.loan.Loan;
import lombok.Getter;

public class LoanDerivedValuesUpdated {

    @Getter
    private final Loan loan;
    @Getter
    private final String state;
    @Getter
    private final String status;

    public LoanDerivedValuesUpdated(Loan loan) {
        this.loan = loan;
        this.state = null;
        this.status = null;
    }
    public LoanDerivedValuesUpdated(Loan loan, String status, String state) {
        this.loan = loan;
        this.state = state;
        this.status = status;
    }
}
