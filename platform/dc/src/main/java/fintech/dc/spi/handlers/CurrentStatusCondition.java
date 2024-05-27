package fintech.dc.spi.handlers;

import fintech.dc.spi.ConditionContext;
import fintech.dc.spi.ConditionHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CurrentStatusCondition implements ConditionHandler {

    @Override
    public boolean apply(ConditionContext context) {
        String status = context.getRequiredParam("status", String.class);
        return StringUtils.equalsIgnoreCase(status, context.getDebt().getStatus());
    }
}
