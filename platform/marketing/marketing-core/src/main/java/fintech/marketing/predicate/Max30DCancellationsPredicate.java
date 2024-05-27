package fintech.marketing.predicate;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import fintech.TimeMachine;
import fintech.marketing.MarketingConditionContext;
import org.springframework.stereotype.Component;

import static fintech.lending.core.application.LoanApplicationStatusDetail.CANCELLED;
import static fintech.lending.core.db.Entities.loanApplication;


@Component
public class Max30DCancellationsPredicate implements PredicateResolver {

    @Override
    public Predicate resolve(MarketingConditionContext context) {
        Integer max = context.getRequiredParam("value", Integer.class);
        Expression<Boolean> cases = new CaseBuilder().when(loanApplication.count().loe(max)).then(true).otherwise(false);

        return JPAExpressions.select(cases)
            .from(loanApplication)
            .where(
                loanApplication.statusDetail.eq(CANCELLED)
                    .and(loanApplication.clientId.eq(context.targetClient().id))
                    .and(loanApplication.closeDate.goe(TimeMachine.today().minusDays(30)))
            )
            .eq(true);
    }
}

