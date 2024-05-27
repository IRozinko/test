package fintech.viventor;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PostLoanExtensionCommand {

    private Long loanId;

    private String viventorLoanId;

    private LocalDate maturityDate;

}
