package fintech.marketing;

import fintech.Validate;
import fintech.marketing.predicate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MarketingRegistryBean implements MarketingRegistry {

    private final Map<String, Class<? extends PredicateResolver>> resolvers = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void setUp() {
        registerPredicateResolver("RepaidLoansCount", PaidLoansCountPredicate.class);
        registerPredicateResolver("LastLoanDpd", LastLoanDpdPredicate.class);
        registerPredicateResolver("DaysSinceLastLoanClosed", DaysSinceLastLoanClosedPredicate.class);
        registerPredicateResolver("Max30DCancellations", Max30DCancellationsPredicate.class);
        registerPredicateResolver("Max30DRejections", Max30DRejectionsPredicate.class);
        registerPredicateResolver("LastLoanStatusDetail", LastLoanStatusDetailPredicate.class);
        registerPredicateResolver("LastLoanApplicationStatusDetail", LastLoanApplicationStatusDetailPredicate.class);
        registerPredicateResolver("LastLoanApplicationCloseReason", LastLoanApplicationCloseReasonPredicate.class);
    }

    @Override
    public void registerPredicateResolver(String type, Class<? extends PredicateResolver> handlerClass) {
        resolvers.put(type, handlerClass);
    }

    @Override
    public PredicateResolver getPredicateResolver(String type) {
        Class<? extends PredicateResolver> resolverClass = resolvers.get(type);
        Validate.notNull(resolverClass, "Resolver class not found by type: [%s]", type);
        return applicationContext.getBean(resolverClass);
    }
}
