package fintech.dc.spi.handlers;

import fintech.dc.spi.ConditionContext;
import fintech.dc.spi.ConditionHandler;
import org.springframework.stereotype.Component;

@Component
public class TotalOutstandingCondition implements ConditionHandler {

    @Override
    public boolean apply(ConditionContext context) {
        Double amountFrom = context.getParam("amountFrom", Double.class).orElse(-1_000_000_000.0d);
        Double amountTo = context.getParam("amountTo", Double.class).orElse(1_000_000_000.0d);
        double totalOutstanding = context.getDebt().getTotalOutstanding().doubleValue();
        return totalOutstanding >= amountFrom && totalOutstanding <= amountTo;
    }
}
