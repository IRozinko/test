package fintech.strategy.bo;

import com.vaadin.data.provider.Query;
import fintech.bo.components.SearchableJooqDataProvider;
import fintech.bo.components.common.SearchFieldValue;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.util.HashMap;

import static fintech.bo.components.common.SearchFieldOptions.ALL;
import static fintech.bo.db.jooq.strategy.Tables.CALCULATION_STRATEGY;

@Slf4j
@Setter
public class StrategyDataProvider extends SearchableJooqDataProvider<Record> {

    private String strategyType;
    private String calculationType;

    public StrategyDataProvider(DSLContext db) {
        super(db, new HashMap<>());
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(CALCULATION_STRATEGY.fields())
            .from(CALCULATION_STRATEGY);

        query.getFilter().ifPresent(filter -> applySearch(select, new SearchFieldValue(ALL, filter)));
        applySearch(select);
        setFilters();
        applyFilter(select);
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(CALCULATION_STRATEGY.ID);
    }

    protected void setFilters() {
        resetFilterConditions();
        addFilterCondition(strategyType, CALCULATION_STRATEGY.STRATEGY_TYPE::eq);
        addFilterCondition(calculationType, CALCULATION_STRATEGY.CALCULATION_TYPE::eq);
    }
}
