package fintech.bo.api.model.dc;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class EditDebtRequest {

    private Long debtId;

    private boolean autoAssign;

    private String agent;
    private String status;
    private String portfolio;
    private String nextAction;
    private LocalDateTime nextActionAt;
}
