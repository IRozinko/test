package fintech.spain.alfa.product.lending;


import java.time.LocalDate;

public interface LoanServicingFacade {

    LoanPrepayment calculatePrepayment(Long loanId, LocalDate date);

    void renounceLoan(Long loanId, LocalDate date);
}
