package fintech.bo.components.jooq;

import com.vaadin.data.ValueProvider;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.renderers.Renderer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.jooq.Record;


@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ColumnBuilder<R extends Record, V> {

    private ValueProvider<R, V> provider;
    private Renderer<? super V> renderer;
    private String id;
    private String caption = "";
    private int width = 150;
    private boolean sortable = true;
    private StyleGenerator<R> styleGenerator;
    private boolean component;

    public ColumnBuilder(JooqGrid<R> grid, Field<?> field) {
        id = getColumnId(field, grid);
        caption = formatCaption(field.getName());
        if ("id".equals(field.getName())) {
            width = 80;
        }
    }

    private String formatCaption(String caption) {
        return StringUtils.capitalize(StringUtils.replace(caption, "_", " "));
    }

    private String getColumnId(Field field, JooqGrid grid) {
        String fieldName = field.getName();

        if (grid.getColumn(fieldName) != null)
            return fieldName + grid.getColumns().size();
        else
            return fieldName;

    }
}
