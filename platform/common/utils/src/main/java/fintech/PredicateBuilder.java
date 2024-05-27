package fintech;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


public class PredicateBuilder {

    private final List<Predicate> predicates;

    public PredicateBuilder() {
        predicates = new ArrayList<>();
    }

    public <T> PredicateBuilder addIfPresent(T val, Function<T, Predicate> toPredicate) {
        Optional.ofNullable(val)
            .map(toPredicate)
            .ifPresent(predicates::add);
        return this;
    }

    public <T extends Collection<?>> PredicateBuilder addIfPresent(T val, Function<T, Predicate> toPredicate) {
        Optional.ofNullable(val)
            .filter(CollectionUtils::isNotEmpty)
            .map(toPredicate)
            .ifPresent(predicates::add);
        return this;
    }

    public Predicate allOf() {
        return ExpressionUtils.allOf(predicates);
    }

}
