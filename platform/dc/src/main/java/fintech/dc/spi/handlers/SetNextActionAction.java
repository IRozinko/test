package fintech.dc.spi.handlers;

import fintech.TimeMachine;
import fintech.dc.db.DebtEntity;
import fintech.dc.db.DebtRepository;
import fintech.dc.spi.ActionContext;
import fintech.dc.spi.ActionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class SetNextActionAction implements ActionHandler {

    @Autowired
    private DebtRepository debtRepository;

    @Override
    public void handle(ActionContext context) {
        String action = context.getRequiredParam("action", String.class);
        Integer afterHours = context.getParam("afterHours", Integer.class).orElse(0);
        boolean force = context.getParam("force", Boolean.class).orElse(false);
        LocalDateTime actionAt = TimeMachine.now().plusHours(afterHours).plusSeconds(1);

        DebtEntity entity = debtRepository.getRequired(context.getDebt().getId());
        if (entity.getNextAction() == null || entity.getNextActionAt() == null || force) {
            log.info("Setting debt [{}] next action to [{}] at [{}]", entity.getId(), action, actionAt);
            entity.setNextAction(action);
            entity.setNextActionAt(actionAt);
        }
    }
}
