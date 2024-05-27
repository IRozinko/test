package fintech.dc.commands;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChangeDebtStateCommand {

    private Long debtId;
    private String status;
    private String subStatus;
    private String state;
}
