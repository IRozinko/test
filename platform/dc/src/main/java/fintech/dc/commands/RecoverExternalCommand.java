package fintech.dc.commands;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class RecoverExternalCommand {

    private Long debtId;
    private String status;

    private String nextAction;
    private LocalDateTime nextActionAt;
    private String agent;
    private boolean autoAssign;

}
