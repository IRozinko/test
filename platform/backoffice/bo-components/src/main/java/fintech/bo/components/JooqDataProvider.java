package fintech.bo.components;

import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.shared.data.sort.SortDirection;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectWhereStep;
import org.jooq.SortField;
import org.jooq.SortOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static org.jooq.impl.DSL.field;

public abstract class JooqDataProvider<T extends Record> extends AbstractBackEndDataProvider<T, String> {

    // https://github.com/vaadin/framework/issues/6290
    private static final int MAX_SIZE = 100000;

    protected final DSLContext db;

    private List<BiConsumer<Integer, Boolean>> sizeListeners = new ArrayList<>();

    public JooqDataProvider(DSLContext db) {
        this.db = db;
    }

    protected abstract SelectWhereStep<T> buildSelect(Query<T, String> query);

    @Override
    protected Stream<T> fetchFromBackEnd(Query<T, String> query) {
        SelectForUpdateStep<T> select = applySortingAndLimits(buildSelect(query), query.getSortOrders(), query.getOffset(), query.getLimit());
        return runQuery(select).stream();
    }

    protected Result<T> runQuery(SelectForUpdateStep<T> select) {
        return select.fetch();
    }

    @Override
    protected int sizeInBackEnd(Query<T, String> query) {
        int size = db.fetchCount(buildSelect(query));
        int manageableSize = Math.min(size, MAX_SIZE);
        sizeListeners.forEach(consumer -> consumer.accept(manageableSize, manageableSize < size));
        return manageableSize;
    }

    public void addSizeListener(BiConsumer<Integer, Boolean> listener) {
        this.sizeListeners.add(listener);
    }

    private <R extends Record> SelectForUpdateStep<R> applySortingAndLimits(SelectWhereStep<R> select, List<QuerySortOrder> sortOrder, int offset, int limit) {
        sortOrder.forEach(order -> select.orderBy(getSortField(order)));
        getDefaultSortOrders().forEach(order -> select.orderBy(getSortField(order)));

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

    private SortField<Object> getSortField(QuerySortOrder order) {
        return field(order.getSorted()).sort(order.getDirection() == SortDirection.ASCENDING ? SortOrder.ASC : SortOrder.DESC);
    }
}
