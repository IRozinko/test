package fintech.bo.components.jooq;

import com.vaadin.data.provider.SortOrder;
import com.vaadin.data.provider.SortOrderBuilder;
import com.vaadin.shared.data.sort.SortDirection;
import org.jooq.Field;

public class JooqGridSortOrderBuilder extends SortOrderBuilder<SortOrder<Field<?>>, Field<?>> {

    @Override
    protected SortOrder<Field<?>> createSortOrder(Field<?> by, SortDirection direction) {
        return new SortOrder<>(by, direction);
    }
}
