package fintech.dc.commands;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePortfolioCommand {

    private Long debtId;
    private String newPortfolio;
    private boolean autoAssignmentRequired;
}
