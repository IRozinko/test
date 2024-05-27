package fintech.spain.alfa.bo.model;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Value
public class RenounceLoanRequest {

    @NotNull
    Long loanId;

    @NotNull
    LocalDate date;
}
