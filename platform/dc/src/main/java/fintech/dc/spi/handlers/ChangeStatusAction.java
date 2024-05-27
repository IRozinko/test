package fintech.dc.spi.handlers;

import fintech.dc.db.DebtEntity;
import fintech.dc.db.DebtRepository;
import fintech.dc.model.DcSettings;
import fintech.dc.spi.ActionContext;
import fintech.dc.spi.ActionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChangeStatusAction implements ActionHandler {

    @Autowired
    private DebtRepository debtRepository;

    @Override
    public void handle(ActionContext context) {
        String status = context.getRequiredParam("status", String.class);
        DebtEntity entity = debtRepository.getRequired(context.getDebt().getId());

        DcSettings.Portfolio portfolio = context.getSettings().findPortfolio(entity.getPortfolio());
        DcSettings.Status newStatus = portfolio.statusByName(status);

        log.info("Changing debt [{}] status to [{}] with priority [{}]", entity.getId(), newStatus.getName(), newStatus.getPriority());

        entity.setStatus(newStatus.getName());
        entity.setPriority(newStatus.getPriority());
    }
}
