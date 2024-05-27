package fintech.dc.commands;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChangeManagingCompanyCommand {

    private Long debtId;
    private String actionName;
    private String newPortfolio;
    private String status;
    private String newManagingCompany;
    private String nextAction;
    private LocalDateTime nextActionAt;
    private boolean autoAssignmentRequired;

}
