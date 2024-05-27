package fintech.dc.spi.handlers;

import fintech.dc.spi.ConditionContext;
import fintech.dc.spi.ConditionHandler;
import org.springframework.stereotype.Component;

@Component
public class DpdCondition implements ConditionHandler {

    @Override
    public boolean apply(ConditionContext context) {
        Integer dpdFrom = context.getParam("dpdFrom", Integer.class).orElse(Integer.MIN_VALUE);
        Integer dpdTo = context.getParam("dpdTo", Integer.class).orElse(Integer.MAX_VALUE);
        int dpd = context.getDebt().getDpd();
        return dpd >= dpdFrom && dpd <= dpdTo;
    }
}
