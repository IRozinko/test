package fintech.spain.alfa.bo.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CalculatePrepaymentResponse {

    private boolean prepaymentAvailable;
    private BigDecimal principalToPay;
    private BigDecimal interestToPay;
    private BigDecimal interestToWriteOff;
    private BigDecimal prepaymentFeeToPay;
    private BigDecimal totalToPay;
}
