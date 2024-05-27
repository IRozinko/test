package fintech.marketing.predicate;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import fintech.lending.core.application.db.QLoanApplicationEntity;
import fintech.marketing.MarketingConditionContext;
import org.springframework.stereotype.Component;

import static fintech.lending.core.db.Entities.loanApplication;


@Component
public class LastLoanApplicationStatusDetailPredicate implements PredicateResolver {

    @Override
    public Predicate resolve(MarketingConditionContext context) {
        String value = context.getRequiredParam("value", String.class);
        QLoanApplicationEntity la = new QLoanApplicationEntity("la");

        return JPAExpressions.selectFrom(loanApplication).where(
            loanApplication.id.eq(
                JPAExpressions.select(la.id.max()).from(la).where(la.clientId.eq(context.targetClient().id))
            ).and(loanApplication.statusDetail.eq(value))
        ).exists();

    }
}
