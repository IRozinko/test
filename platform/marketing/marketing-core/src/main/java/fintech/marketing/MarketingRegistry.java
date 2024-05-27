package fintech.marketing;

import fintech.marketing.predicate.PredicateResolver;

public interface MarketingRegistry {

    void registerPredicateResolver(String type, Class<? extends PredicateResolver> handlerClass);

    PredicateResolver getPredicateResolver(String type);

    void setUp();
}
