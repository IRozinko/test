package fintech.dc.spi.handlers;

import fintech.dc.db.DebtEntity;
import fintech.dc.db.DebtRepository;
import fintech.dc.model.Debt;
import fintech.dc.spi.ActionContext;
import fintech.dc.spi.ActionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class ChangePortfolioAction implements ActionHandler {

    protected static final String PORTFOLIO_PARAMETER_NAME = "portfolio";
    protected static final String ASSIGNMENT_MODE_PARAMETER_NAME = "assignmentMode";
    protected static final String IGNORE_BATCH_MODE_ON_LOAN_STATUSES_PARAMETER_NAME = "ignoreBatchOnLoanStatuses";

    private static final String AUTO_ASSIGN_MODE = "auto";
    private static final String BATCH_ASSIGN_MODE = "batch";

    @Autowired
    private DebtRepository debtRepository;

    @Override
    public void handle(ActionContext context) {
        String portfolio = context.getRequiredParam(PORTFOLIO_PARAMETER_NAME, String.class);

        String assignmentMode = context.getParam(ASSIGNMENT_MODE_PARAMETER_NAME, String.class).orElse(AUTO_ASSIGN_MODE);

        List<String> loanStatusesToIgnoreOnBatch = context.getParam(IGNORE_BATCH_MODE_ON_LOAN_STATUSES_PARAMETER_NAME, List.class).orElse(Collections.emptyList());

        DebtEntity entity = debtRepository.getRequired(context.getDebt().getId());
        log.info("Changing debt [{}] portfolio to [{}]", entity.getId(), portfolio);
        entity.setPortfolio(portfolio);

        if (AUTO_ASSIGN_MODE.equalsIgnoreCase(assignmentMode)) {
            entity.setAutoAssignmentRequired(true);
            entity.setBatchAssignmentRequired(false);
        } else if (BATCH_ASSIGN_MODE.equalsIgnoreCase(assignmentMode) && !loanStatusesToIgnoreOnBatch.contains(entity.getLoanStatusDetail())) {
            entity.setAgent(null);
            entity.setAutoAssignmentRequired(false);
            entity.setBatchAssignmentRequired(true);
        } else {
            entity.setAutoAssignmentRequired(false);
            entity.setBatchAssignmentRequired(false);
        }
    }
}
