package fintech.dc.commands;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AssignDebtCommand {

    private Long debtId;
    private String agent;
    private String comment;
}
