package fintech.dc.spi.handlers;

import fintech.dc.db.DebtEntity;
import fintech.dc.db.DebtRepository;
import fintech.dc.spi.BulkActionContext;
import fintech.dc.spi.BulkActionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChangePortfolioBulkAction implements BulkActionHandler {

    protected static final String PORTFOLIO_PROPERTY_NAME = "portfolio";

    @Autowired
    private DebtRepository debtRepository;

    @Override
    public void handle(BulkActionContext context) {
        String portfolio = getNewPortfolio(context);
        DebtEntity entity = debtRepository.getRequired(context.getDebt().getId());
        log.info("Changing debt [{}] portfolio to [{}]", entity.getId(), portfolio);
        entity.setPortfolio(portfolio);
        entity.setAutoAssignmentRequired(true);
    }

    protected String getNewPortfolio(BulkActionContext context) {
        return context.getRequiredParam(PORTFOLIO_PROPERTY_NAME, String.class);
    }
}
