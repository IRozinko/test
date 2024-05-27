package fintech.bo.api.model.loan;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BreakLoanRequest {

    @NotNull
    private Long loanId;

    private String reasonForBreak;

}
