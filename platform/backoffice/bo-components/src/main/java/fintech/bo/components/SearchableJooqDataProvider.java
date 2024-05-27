package fintech.bo.components;

import com.google.common.collect.ImmutableList;
import fintech.Validate;
import fintech.bo.components.common.SearchFieldValue;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.*;
import java.util.function.Function;

import static fintech.bo.components.common.SearchFieldOptions.ALL;

public abstract class SearchableJooqDataProvider<T extends Record> extends JooqDataProvider<T> {

    protected SearchFieldValue searchFieldValue;
    private List<Condition> filterConditions;

    private Map<String, Function<String, Condition>> searchFields;

    public SearchableJooqDataProvider(DSLContext db,
                                      Map<String, Function<String, Condition>> searchFields) {
        super(db);
        this.searchFields = searchFields;
        this.filterConditions = new ArrayList<>();
    }

    public Collection<String> getSearchFieldsNames() {
        return searchFields.keySet();
    }

    protected void applySearch(SelectWhereStep<Record> select) {
        Optional.ofNullable(searchFieldValue).filter(SearchFieldValue::isNotBlank)
            .ifPresent(sf -> applySearch(select, sf));
    }

    protected void applySearch(SelectWhereStep<Record> select, SearchFieldValue filter) {
        Validate.notBlank(filter.getField(), "Search field can't be empty.");

        List<Condition> conditions = new ArrayList<>();
        String filterValue = filter.getValue();
        Collection<Function<String, Condition>> conditionsFunctions;
        if (ALL.equals(filter.getField()))
            conditionsFunctions = searchFields.values();
        else
            conditionsFunctions = ImmutableList.of(searchFields.get(filter.getField()));

        for (String fragment : StringUtils.split(filterValue, " ")) {
            conditions.add(conditionsFunctions.stream()
                .map(condition -> condition.apply(fragment))
                .reduce(DSL.falseCondition(), Condition::or));
        }
        select.where(conditions);
    }

    protected void applyFilter(SelectWhereStep<Record> select) {
        select.where(filterConditions);
    }

    protected <V> void addFilterCondition(V value, Function<V, Condition> condition) {
        Optional.ofNullable(value)
            .ifPresent(val -> filterConditions.add(condition.apply(val)));
    }

    protected void resetFilterConditions() {
        filterConditions.clear();
    }

    public void setTextFilter(SearchFieldValue searchFieldValue) {
        this.searchFieldValue = searchFieldValue;
    }
}
