package fintech.viventor;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PostLoanPaidCommand {

    private Long loanId;

    private String viventorLoanId;

    private LocalDate date;

}
