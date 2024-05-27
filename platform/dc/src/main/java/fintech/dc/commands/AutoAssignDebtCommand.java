package fintech.dc.commands;

import lombok.Data;

@Data
public class AutoAssignDebtCommand {

    private Long debtId;

    private String excludeAgent;

    private boolean tryToStickWithCurrentAgent;
}
