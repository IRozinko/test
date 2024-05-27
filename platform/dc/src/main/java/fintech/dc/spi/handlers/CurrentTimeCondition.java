package fintech.dc.spi.handlers;

import fintech.TimeMachine;
import fintech.dc.spi.ConditionContext;
import fintech.dc.spi.ConditionHandler;
import org.springframework.stereotype.Component;

@Component
public class CurrentTimeCondition implements ConditionHandler {

    @Override
    public boolean apply(ConditionContext context) {
        Integer hourFrom = context.getParam("hourFrom", Integer.class).orElse(0);
        Integer hourTo = context.getParam("hourTo", Integer.class).orElse(24);
        int currentHour = TimeMachine.now().getHour();
        return currentHour >= hourFrom && currentHour <= hourTo;
    }
}
