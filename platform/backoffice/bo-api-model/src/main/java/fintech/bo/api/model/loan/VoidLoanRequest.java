package fintech.bo.api.model.loan;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VoidLoanRequest {

    @NotNull
    private Long loanId;
}
