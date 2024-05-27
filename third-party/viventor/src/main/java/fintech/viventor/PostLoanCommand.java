package fintech.viventor;

import fintech.viventor.model.ViventorBorrower;
import fintech.viventor.model.ViventorNewLoan;
import fintech.viventor.model.ViventorSchedule;
import lombok.Data;

@Data
public class PostLoanCommand {

    private Long loanId;

    private ViventorNewLoan loan;

    private ViventorBorrower borrower;

    private ViventorSchedule schedule;

}
