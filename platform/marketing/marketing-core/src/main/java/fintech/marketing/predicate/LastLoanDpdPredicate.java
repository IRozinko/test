package fintech.marketing.predicate;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import fintech.lending.core.loan.db.QLoanEntity;
import fintech.marketing.MarketingConditionContext;
import org.springframework.stereotype.Component;

import static fintech.lending.core.db.Entities.loan;


@Component
public class LastLoanDpdPredicate implements PredicateResolver {

    @Override
    public Predicate resolve(MarketingConditionContext context) {
        Integer countFrom = context.getParam("from", Integer.class).orElse(Integer.MIN_VALUE);
        Integer countTo = context.getRequiredParam("to", Integer.class);
        QLoanEntity l = new QLoanEntity("l");
        return JPAExpressions.selectFrom(loan).where(
            loan.id.eq(
                JPAExpressions.select(l.id.max()).from(l).where(l.clientId.eq(context.targetClient().id))
            )
            .and(loan.overdueDays.between(countFrom, countTo))
        ).exists();

    }
}
