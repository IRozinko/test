package fintech.spain.dc.command;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class RepurchaseDebtCommand {

    private Long debtId;
    private String status;
    private String portfolio;
    private String nextAction;
    private LocalDateTime nextActionAt;
    private String agent;
    private boolean autoAssign;

}
