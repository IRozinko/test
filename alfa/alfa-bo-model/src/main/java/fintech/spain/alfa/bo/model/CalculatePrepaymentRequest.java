package fintech.spain.alfa.bo.model;

import lombok.Value;

import java.time.LocalDate;

@Value
public class CalculatePrepaymentRequest {

    Long loanId;

    LocalDate onDate;
}
