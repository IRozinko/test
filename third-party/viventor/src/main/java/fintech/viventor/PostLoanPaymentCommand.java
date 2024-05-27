package fintech.viventor;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PostLoanPaymentCommand {

    private Long loanId;

    private String viventorLoanId;

    private Integer number;

    private LocalDate actualDate;

}
