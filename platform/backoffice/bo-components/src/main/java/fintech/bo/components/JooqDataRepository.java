package fintech.bo.components;

import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.shared.data.sort.SortDirection;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.SortField;
import org.jooq.SortOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.jooq.impl.DSL.field;

public abstract class JooqDataRepository<T> extends AbstractBackEndDataProvider<T, String> {

    protected final DSLContext db;
    private final Class<T> type;
    private List<Consumer<Integer>> sizeListeners = new ArrayList<>();

    public JooqDataRepository(DSLContext db, Class<T> type) {
        this.db = db;
        this.type = type;
    }

    public abstract T getRequired(Long id);

    protected abstract SelectOnConditionStep<Record> buildSelect(Query<T, String> query);

    @Override
    protected Stream<T> fetchFromBackEnd(Query<T, String> query) {
        SelectForUpdateStep<Record> select = applySortingAndLimits(buildSelect(query), query.getSortOrders(), query.getOffset(), query.getLimit());
        return runQuery(select).stream();
    }

    protected List<T> runQuery(SelectForUpdateStep<Record> select) {
        return select.fetchInto(type);
    }

    @Override
    protected int sizeInBackEnd(Query<T, String> query) {
        int size = db.fetchCount(buildSelect(query));
        sizeListeners.forEach(consumer -> consumer.accept(size));
        return size;
    }

    public void addSizeListener(Consumer<Integer> listener) {
        this.sizeListeners.add(listener);
    }

    private SelectForUpdateStep<Record> applySortingAndLimits(SelectOnConditionStep<Record> select, List<QuerySortOrder> sortOrder, int offset, int limit) {
        sortOrder.addAll(getDefaultSortOrders());
        sortOrder.forEach(order -> {
            SortField<?> field = field(order.getSorted()).sort(order.getDirection() == SortDirection.ASCENDING ? SortOrder.ASC : SortOrder.DESC);
            select.orderBy(field);
        });

        return select.offset(offset).limit(limit);
    }

    protected List<QuerySortOrder> getDefaultSortOrders() {
        return Collections.emptyList();
    }

    @Override
    public Object getId(T item) {
        return id(item);
    }

    protected abstract Object id(T item);

    public static List<Field<?>> fields(Field<?>[] fields1, Field<?>... fields2) {
        List<Field<?>> allFields = new ArrayList<>();
        allFields.addAll(Arrays.asList(fields1));
        allFields.addAll(Arrays.asList(fields2));
        return allFields;
    }
}
