package fintech.payments.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankMovementsFileParseResult {
    private BigDecimal amount;
    private String description;
}
