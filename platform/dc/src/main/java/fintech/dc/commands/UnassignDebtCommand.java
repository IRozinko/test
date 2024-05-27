package fintech.dc.commands;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UnassignDebtCommand {

    private Long debtId;
    private String comment;
}
