package fintech.instantor.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class InstantorTransaction {

    private Long responseId;
    private Long clientId;
    private String accountNumber;
    private String accountHolderName;
    private String currency;
    private LocalDate date;
    private BigDecimal amount;
    private BigDecimal balance;
    private String description;
    private String category;
    private String nordigenCategory;
}
