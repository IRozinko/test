package fintech.spain.alfa.bo.model;

import lombok.Value;

import java.time.LocalDate;

@Value
public class CalculatePenaltyRequest {

    Long loanId;

    LocalDate onDate;
}
