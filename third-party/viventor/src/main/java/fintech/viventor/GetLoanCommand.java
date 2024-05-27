package fintech.viventor;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetLoanCommand {

    private Long loanId;

    private String viventorLoanId;

}
