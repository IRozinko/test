package fintech.marketing.predicate;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import fintech.TimeMachine;
import fintech.lending.core.loan.LoanStatus;
import fintech.lending.core.loan.db.QLoanEntity;
import fintech.marketing.MarketingConditionContext;
import org.springframework.stereotype.Component;

import static fintech.lending.core.db.Entities.loan;


@Component
public class DaysSinceLastLoanClosedPredicate implements PredicateResolver {

    @Override
    public Predicate resolve(MarketingConditionContext context) {
        int fromDays = context.getRequiredParam("from", Integer.class);
        int toDays = context.getRequiredParam("to", Integer.class);

        QLoanEntity l = new QLoanEntity("l");
        return JPAExpressions.selectFrom(loan).where(
            loan.id.eq(
                JPAExpressions.select(l.id.max()).from(l).where(l.clientId.eq(context.targetClient().id).and(l.status.eq(LoanStatus.CLOSED)))
            )
            .and(loan.closeDate.between(TimeMachine.today().minusDays(toDays), TimeMachine.today().minusDays(fromDays)))
        ).exists();
    }
}
