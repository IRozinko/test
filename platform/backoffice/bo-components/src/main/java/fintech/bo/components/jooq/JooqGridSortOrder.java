package fintech.bo.components.jooq;

import com.vaadin.data.provider.SortOrder;
import com.vaadin.data.provider.SortOrderBuilder;
import com.vaadin.shared.data.sort.SortDirection;
import org.jooq.Field;
import org.jooq.Record;

public class JooqGridSortOrder<R extends Record> extends SortOrder<R> {

    public JooqGridSortOrder(R sorted, SortDirection direction) {
        super(sorted, direction);
    }

    public static SortOrderBuilder<SortOrder<Field<?>>, Field<?>> asc(Field<?> by) {
        return new JooqGridSortOrderBuilder()
            .thenAsc(by);
    }

    public static SortOrderBuilder<SortOrder<Field<?>>, Field<?>> desc(Field<?> by) {
        return new JooqGridSortOrderBuilder()
            .thenDesc(by);
    }


}
