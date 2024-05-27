package fintech.dc.commands;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
public class EditDebtCommand {

    private Long debtId;

    private String status;
    private String portfolio;
    private String owningCompany;
    private String managingCompany;

    private String subStatus;
    private String nextAction;
    private LocalDateTime nextActionAt;

    private String operationType;

}
