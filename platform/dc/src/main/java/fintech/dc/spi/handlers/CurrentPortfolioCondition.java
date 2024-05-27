package fintech.dc.spi.handlers;

import fintech.dc.spi.ConditionContext;
import fintech.dc.spi.ConditionHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CurrentPortfolioCondition implements ConditionHandler {

    @Override
    public boolean apply(ConditionContext context) {
        String portfolio = context.getRequiredParam("portfolio", String.class);
        return StringUtils.equalsIgnoreCase(portfolio, context.getDebt().getPortfolio());
    }
}
