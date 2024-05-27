package fintech.lending.core.creditlimit;

import fintech.TimeMachine;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static fintech.BigDecimalUtils.amount;

@Data
public class CreditLimit {

    private Long clientId;
    private BigDecimal limit;
    private LocalDate activeFrom;
    private LocalDateTime grantedAt;
    private String reason;

    public static CreditLimit zeroLimit(Long clientId) {
        CreditLimit limit = new CreditLimit();
        limit.setLimit(amount(0));
        limit.setClientId(clientId);
        limit.setReason("default");
        limit.setActiveFrom(TimeMachine.today());
        limit.setGrantedAt(TimeMachine.now());
        return limit;
    }
}
