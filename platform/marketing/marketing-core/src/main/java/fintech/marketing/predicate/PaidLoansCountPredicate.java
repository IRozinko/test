package fintech.marketing.predicate;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.marketing.MarketingConditionContext;
import org.springframework.stereotype.Component;

import static fintech.lending.core.db.Entities.loan;


@Component
public class PaidLoansCountPredicate implements PredicateResolver {

    @Override
    public Predicate resolve(MarketingConditionContext context) {
        Integer countFrom = context.getRequiredParam("from", Integer.class);
        Integer countTo = context.getParam("to", Integer.class).orElse(Integer.MAX_VALUE);
        Expression<Boolean> cases = new CaseBuilder().when(loan.count().between(countFrom, countTo)).then(true).otherwise(false);

        return JPAExpressions.select(cases)
            .from(loan)
            .where(loan.statusDetail.eq(LoanStatusDetail.PAID).and(loan.clientId.eq(context.targetClient().id))
            ).eq(true);
    }
}
