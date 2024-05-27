package fintech.dc.spi.handlers;

import fintech.dc.spi.ConditionContext;
import fintech.dc.spi.ConditionHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoanStatusDetailsCondition implements ConditionHandler {

    @Override
    public boolean apply(ConditionContext context) {
        String loanStatusDetail = context.getDebt().getLoanStatusDetail();
        List<String> statuses = context.getRequiredParam("statusDetails", List.class);
        return statuses.stream().anyMatch(s -> StringUtils.equalsIgnoreCase(s, loanStatusDetail));
    }
}
