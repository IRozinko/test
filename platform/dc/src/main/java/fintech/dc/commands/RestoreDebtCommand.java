package fintech.dc.commands;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class RestoreDebtCommand {

    private Long debtId;
    private String portfolio;
    private String status;
    private String subStatus;
    private String company;
    private String nextAction;
    private LocalDateTime nextActionAt;

    private String operationType;
}
