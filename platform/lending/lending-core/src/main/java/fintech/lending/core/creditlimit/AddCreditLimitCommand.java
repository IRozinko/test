package fintech.lending.core.creditlimit;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AddCreditLimitCommand {

    private Long clientId;
    private BigDecimal limit;
    private LocalDate activeFrom;
    private String reason;
}
