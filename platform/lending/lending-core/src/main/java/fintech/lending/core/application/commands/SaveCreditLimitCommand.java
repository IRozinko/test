package fintech.lending.core.application.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SaveCreditLimitCommand {

    @NonNull
    private Long id;

    @NonNull
    private BigDecimal limit;
}
