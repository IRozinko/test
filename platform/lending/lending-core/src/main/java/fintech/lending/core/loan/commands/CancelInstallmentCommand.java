package fintech.lending.core.loan.commands;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class CancelInstallmentCommand {

    private Long installmentId;

    private boolean broken;
    private LocalDate cancelDate;

    private boolean reverseAppliedAmounts = true;


}
