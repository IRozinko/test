package fintech.viventor.model;

import lombok.Data;

@Data
public class PostLoanRequest {

    private ViventorNewLoan loan;

    private ViventorBorrower borrower;

    private ViventorSchedule schedule;

}
