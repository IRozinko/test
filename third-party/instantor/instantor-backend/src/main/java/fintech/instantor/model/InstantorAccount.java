package fintech.instantor.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class InstantorAccount {

    private Long clientId;
    private Long responseId;
    private BigDecimal balance;
    private String iban;
    private String currency;
    private Integer transactionCount = 0;
    private String holderName;
    private List<InstantorTransaction> transactionList;
}
