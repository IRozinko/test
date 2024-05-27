package fintech.marketing.predicate;

import com.querydsl.core.types.Predicate;
import fintech.marketing.MarketingConditionContext;

public interface PredicateResolver {

    Predicate resolve(MarketingConditionContext ctx);
}
