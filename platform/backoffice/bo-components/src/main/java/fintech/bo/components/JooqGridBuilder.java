package fintech.bo.components;

import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Link;
import com.vaadin.ui.components.grid.ItemClickListener;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.LocalDateRenderer;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import com.vaadin.ui.renderers.Renderer;
import com.vaadin.ui.themes.ValoTheme;
import fintech.Validate;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.dialogs.InfoDialog;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.TableImpl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static fintech.bo.components.Formats.decimalRenderer;
import static fintech.bo.components.GridHelper.alignRightStyle;

public class JooqGridBuilder<R extends Record> {

    private final Grid<R> grid;

    private boolean showTotalCount = true;

    public JooqGridBuilder() {
        this.grid = new Grid<>();
    }

    public <F> Grid.Column<R, F> addColumn(ValueProvider<R, F> valueProvider) {
        return grid.addColumn(valueProvider);
    }

    public <F> Grid.Column<R, F> addColumn(Field<F> field) {
        String caption = StringUtils.capitalize(StringUtils.replace(field.getName(), "_", " "));
        return addColumn(field, caption);
    }

    public <F> Grid.Column<R, F> addColumn(Field<F> field, String caption) {
        String columnId = field.getName();
        if (grid.getColumn(field.getName()) != null) {
            columnId = field.getName() + grid.getColumns().size();
        }
        Grid.Column<R, F> column = addColumnInternal(field).setId(columnId).setCaption(caption);

        if (field.getDataType().getType() == LocalDateTime.class) {
            column.setRenderer((Renderer<? super F>) new LocalDateTimeRenderer(Formats.DATE_TIME_FORMAT));
            column.setWidth(200);
        } else if (field.getDataType().getType() == LocalDate.class) {
            column.setRenderer((Renderer<? super F>) new LocalDateRenderer(Formats.DATE_FORMAT));
            column.setWidth(150);
        } else if (field.getDataType().getType() == BigDecimal.class) {
            column.setRenderer((Renderer<? super F>) decimalRenderer()).setStyleGenerator(alignRightStyle());
            column.setWidth(150);
        } else if (field.getDataType().getType() == Boolean.class) {
            column.setWidth(50);
        } else {
            column.setWidth(150);
        }
        if ("id".equals(field.getName())) {
            column.setWidth(80);
        }
        return column;
    }

    private <F> Grid.Column<R, F> addColumnInternal(Field<F> field) {
        if (field.getDataType().getType() == Boolean.class) {
            return (Grid.Column<R, F>) grid.addComponentColumn(r -> {
                CheckBox checkBox = new CheckBox();
                checkBox.setValue(Boolean.TRUE.equals(field.getValue(r)));
                checkBox.setEnabled(false);
                return checkBox;
            });
        }
        return grid.addColumn(field::getValue);
    }

    public <F> Grid.Column<R, Button> addActionColumn(Field<F> field, Consumer<R> action) {
        return grid.addColumn(record -> {
            Button btn = new Button(Optional.ofNullable(record.get(field)).map(Objects::toString).orElse(""));
            btn.addStyleNames(ValoTheme.BUTTON_BORDERLESS_COLORED);
            btn.addClickListener(event -> action.accept(record));
            return btn;
        }, new ComponentRenderer()).setWidth(80).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW).setSortable(false);
    }

    public Grid.Column<R, Button> addActionColumn(String actionTitle, Consumer<R> action) {
        return grid.addColumn(record -> {
            Button btn = new Button(actionTitle);
            btn.addStyleNames(ValoTheme.BUTTON_SMALL);
            btn.addClickListener(event -> action.accept(record));
            return btn;
        }, new ComponentRenderer()).setWidth(80).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW).setSortable(false);
    }

    public Grid.Column<R, Button> addActionColumn(String actionTitle, Consumer<R> action, Predicate<R> disabled) {
        return grid.addColumn(record -> {
            Button btn = new Button(actionTitle);
            btn.addStyleNames(ValoTheme.BUTTON_SMALL);
            btn.addClickListener(event -> action.accept(record));
            btn.setEnabled(!disabled.test(record));
            return btn;
        }, new ComponentRenderer()).setWidth(80).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW).setSortable(false);
    }

    public Grid.Column<R, Component> addActionColumn(Function<R, Component> provider) {
        return grid.addColumn(provider::apply, new ComponentRenderer()).setWidth(80).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW).setSortable(false);
    }

    public Grid.Column<R, Button> addNavigationColumn(String title, Function<R, String> stateProvider) {
        return GridHelper.addNavigationColumn(grid, title, stateProvider);
    }

    public <F> Grid.Column<R, Link> addLinkColumn(Field<F> field, Function<R, String> linkProvider) {
        return grid.addColumn(record -> Optional.ofNullable(record.get(field))
            .map(val -> new Link(Objects.toString(val), new InternalResource(linkProvider.apply(record))))
            .orElse(new Link()), new ComponentRenderer())
            .setCaption(field.getName())
            .setWidth(150);
    }

    public <F> Grid.Column<R, F> addComponentColumn(Function<R, Component> provider) {
        return addComponentColumn(provider, "");
    }

    public <F> Grid.Column<R, F> addComponentColumn(Function<R, Component> provider, String caption) {
        Grid.Column<R, F> column = (Grid.Column<R, F>) grid.addColumn(r -> provider.apply(r), new ComponentRenderer())
            .setWidth(150).setSortable(false).setCaption(caption);
        return column;
    }

    public void sortAsc(Field<?>... fields) {
        List<GridSortOrder<R>> order = new ArrayList<>();
        for (Field<?> field : fields) {
            Grid.Column<R, ?> column = grid.getColumn(field.getName());
            Validate.notNull(column, "Sorting column not found for field %s", field.getName());
            order.add(new GridSortOrder(column, SortDirection.ASCENDING));
        }
        grid.setSortOrder(order);
    }

    public void sortDesc(Field<?>... fields) {
        List<GridSortOrder<R>> order = new ArrayList<>();
        for (Field<?> field : fields) {
            Grid.Column<R, ?> column = grid.getColumn(field.getName());
            Validate.notNull(column, "Sorting column not found for field %s", field.getName());
            order.add(new GridSortOrder(column, SortDirection.DESCENDING));
        }
        grid.setSortOrder(order);
    }


    public Grid<R> build(JooqDataProvider<R> dataProvider) {
        this.grid.setDataProvider(dataProvider);
        if (showTotalCount) {
            GridHelper.addTotalCountAsCaption(this.grid, dataProvider);
        }
        tuneGrid();
        return grid;
    }

    public Grid<R> build(AbstractBackEndDataProvider<R, ?> dataProvider) {
        this.grid.setDataProvider(dataProvider);
        tuneGrid();
        return grid;
    }

    public Grid<R> build(List<R> items) {
        this.grid.setItems(items);
        tuneGrid();
        return grid;
    }

    private void tuneGrid() {
        this.grid.setSelectionMode(Grid.SelectionMode.NONE);
        this.grid.addColumn(i -> "").setCaption("").setWidthUndefined().setSortable(false);
        this.grid.setSizeFull();
    }

    public void addAuditColumns(TableImpl<?> table) {
        if (grid.getColumn("created_at") == null) {
            addColumn(table.field("created_at"));
        }
        if (grid.getColumn("created_by") == null) {
            addColumn(table.field("created_by"));
        }
        if (grid.getColumn("updated_at") == null) {
            addColumn(table.field("updated_at"));
        }
        if (grid.getColumn("updated_by") == null) {
            addColumn(table.field("updated_by"));
        }

        this.grid.addItemClickListener((ItemClickListener<R>) event -> {
            if (event.getMouseEventDetails().isDoubleClick() && event.getMouseEventDetails().isCtrlKey()) {
                R r = event.getItem();
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                for (Field field : r.fields()) {
                    pw.println(field.getName() + ":");
                    pw.println(field.getValue(r));
                    pw.println("----------------------------------------------------------");
                }
                InfoDialog dialog = Dialogs.showText("Row", sw.toString());
                dialog.setWidth(90, Sizeable.Unit.PERCENTAGE);
                dialog.setHeight(90, Sizeable.Unit.PERCENTAGE);
                dialog.setModal(true);
            }
        });
    }

    public void setShowTotalCount(boolean showTotalCount) {
        this.showTotalCount = showTotalCount;
    }

    public Grid<R> getGrid() {
        return grid;
    }

    private Grid.Column<R, Component> applyActionStyle(Grid.Column<R, Component> column) {
        return column.setWidth(80)
            .setStyleGenerator(item -> BackofficeTheme.ACTION_ROW)
            .setSortable(false);
    }
}
