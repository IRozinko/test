package fintech.spain.alfa.product.dc.spi;

import fintech.dc.db.DebtEntity;
import fintech.dc.db.DebtRepository;
import fintech.dc.spi.ActionContext;
import fintech.dc.spi.ActionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AutoAssignToAgentAction implements ActionHandler {

    protected static final String AUTO_ASSIGN_PROPERTY_NAME = "agent";

    @Autowired
    private DebtRepository debtRepository;

    @Override
    public void handle(ActionContext context) {
        String agent = getAgent(context);
        DebtEntity entity = debtRepository.getRequired(context.getDebt().getId());
        log.info("Assign debt [{}] to agent [{}]", entity.getId(), agent);
        entity.setAgent(agent);
        entity.setAutoAssignmentRequired(false);
        entity.setBatchAssignmentRequired(false);
    }

    protected String getAgent(ActionContext context) {
        return context.getRequiredParam(AUTO_ASSIGN_PROPERTY_NAME, String.class);
    }
}
