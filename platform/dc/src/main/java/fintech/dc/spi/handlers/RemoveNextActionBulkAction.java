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
public class RemoveNextActionBulkAction implements BulkActionHandler {

    @Autowired
    private DebtRepository debtRepository;

    @Override
    public void handle(BulkActionContext context) {
        DebtEntity entity = debtRepository.getRequired(context.getDebt().getId());
        log.info("Removing next action from debt [{}]", entity.getId());

        entity.setNextAction(null);
        entity.setNextActionAt(null);
    }
}
