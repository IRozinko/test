package fintech.spain.alfa.product.dc.spi;

import com.querydsl.jpa.JPQLQueryFactory;
import fintech.dc.model.Debt;
import fintech.dc.spi.ConditionContext;
import fintech.dc.spi.ConditionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.dc.db.Entities.action;
import static java.util.Objects.nonNull;

@Component
public class ExternalPortfolioBeforeCondition implements ConditionHandler {

    @Autowired
    private JPQLQueryFactory queryFactory;

    @Override
    public boolean apply(ConditionContext context) {
        Debt debt = context.getDebt();
        String portfolioBefore = queryFactory
            .select(action.portfolioBefore).from(action)
            .where(action.debt.id.eq(debt.getId()))
            .orderBy(action.id.desc()).fetchFirst();
        return nonNull(portfolioBefore) && (portfolioBefore.equals("External Collections") || portfolioBefore.equals("Sold"));
    }
}
