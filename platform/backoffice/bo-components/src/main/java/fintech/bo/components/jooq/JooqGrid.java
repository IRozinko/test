package fintech.bo.components.jooq;

import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.provider.SortOrder;
import com.vaadin.data.provider.SortOrderBuilder;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.grid.ItemClickListener;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.LocalDateRenderer;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.ui.themes.ValoTheme;
import fintech.Validate;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.Formats;
import fintech.bo.components.InternalResource;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.dialogs.InfoDialog;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.TableImpl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static fintech.bo.components.Formats.decimalRenderer;
import static fintech.bo.components.GridHelper.alignCenterStyle;
import static fintech.bo.components.GridHelper.alignRightStyle;
import static java.util.Optional.ofNullable;

public class JooqGrid<R extends Record> extends Grid<R> {

    private static <T, R> BiConsumer<T, R> BI_NOOP() {
        return (t, r) -> {
        };
    }

    protected ColumnBuilder<R, Object> text(Field field) {
        return new ColumnBuilder<>(this, field)
            .setProvider(field::getValue)
            .setRenderer(new TextRenderer());
    }

    protected ColumnBuilder<R, LocalDateTime> dateTime(Field<LocalDateTime> field) {
        return new ColumnBuilder<R, LocalDateTime>(this, field)
            .setProvider(field::getValue)
            .setRenderer(new LocalDateTimeRenderer(Formats.DATE_TIME_FORMAT))
            .setWidth(200);
    }

    protected ColumnBuilder<R, LocalDate> date(Field<LocalDate> field) {
        return new ColumnBuilder<R, LocalDate>(this, field)
            .setProvider(field::getValue)
            .setRenderer(new LocalDateRenderer(Formats.DATE_FORMAT));
    }

    protected ColumnBuilder<R, Number> decimal(Field<Number> field) {
        return new ColumnBuilder<R, Number>(this, field)
            .setProvider(field::getValue)
            .setRenderer(decimalRenderer())
            .setStyleGenerator(alignRightStyle());
    }

    protected ColumnBuilder<R, CheckBox> checkBox(Field<Boolean> field) {
        return new ColumnBuilder<R, CheckBox>(this, field)
            .setRenderer(new ComponentRenderer())
            .setProvider(checkBoxProvider(field, BI_NOOP(), alwaysDisabled()));
    }

    protected ColumnBuilder<R, CheckBox> checkBox(Field<Boolean> field, BiConsumer<R, Boolean> action, Predicate<R> disabled) {
        return new ColumnBuilder<R, CheckBox>(this, field)
            .setRenderer(new ComponentRenderer())
            .setStyleGenerator(alignCenterStyle())
            .setProvider(checkBoxProvider(field, action, disabled));
    }

    protected ColumnBuilder<R, Button> button(String actionTitle, Consumer<R> action, Predicate<R> disabled) {
        return new ColumnBuilder<R, Button>()
            .setRenderer(new ComponentRenderer())
            .setProvider(buttonProvider(actionTitle, action, disabled))
            .setWidth(80)
            .setStyleGenerator(item -> BackofficeTheme.ACTION_ROW)
            .setSortable(false);
    }

    protected ColumnBuilder<R, Link> link(Field field, Function<R, String> linkProvider) {
        return new ColumnBuilder<R, Link>(this, field)
            .setRenderer(new ComponentRenderer())
            .setProvider(linkProvider(field, linkProvider))
            .setStyleGenerator(item -> BackofficeTheme.ACTION_ROW)
            .setSortable(false);
    }

    protected ColumnBuilder<R, Button> navigation(String title, Function<R, String> stateProvider) {
        return button(title, navigateTo(stateProvider), alwaysEnabled());
    }

    protected <V> Column addColumn(ColumnBuilder<R, V> builder) {
        Column<R, V> column = this.addColumn(builder.getProvider())
            .setRenderer(builder.getRenderer())
            .setCaption(builder.getCaption())
            .setWidth(builder.getWidth())
            .setSortable(builder.isSortable());

        ofNullable(builder.getId())
            .ifPresent(column::setId);

        ofNullable(builder.getStyleGenerator())
            .ifPresent(column::setStyleGenerator);

        return column;
    }

    protected void setSortOrder(SortOrderBuilder<SortOrder<Field<?>>, Field<?>> fields) {
        List<GridSortOrder<R>> orders = fields.build().stream().map(f -> {
            Column<R, ?> column = this.getColumn(f.getSorted().getName());
            Validate.notNull(column, "Sorting column not found for field %s", f.getSorted().getName());
            return new GridSortOrder<>(column, f.getDirection());
        }).collect(Collectors.toList());
        this.setSortOrder(orders);
    }

    protected void addAuditColumns(TableImpl<?> table) {
        addCreatedCols(table);
        addUpdatedCols(table);
        addRowPreview();
    }

    protected void addCreatedCols(TableImpl<?> table) {
        if (this.getColumn("created_at") == null) {
            addColumn(dateTime(table.field("created_at", LocalDateTime.class)));
        }
        if (this.getColumn("created_by") == null) {
            addColumn(text(table.field("created_by")));
        }
    }

    protected void addUpdatedCols(TableImpl<?> table) {
        if (this.getColumn("updated_at") == null) {
            addColumn(dateTime(table.field("updated_at", LocalDateTime.class)));
        }
        if (this.getColumn("updated_by") == null) {
            addColumn(text(table.field("updated_by")));
        }
    }

    private ValueProvider<R, CheckBox> checkBoxProvider(Field<Boolean> field, BiConsumer<R, Boolean> action, Predicate<R> disabled) {
        return r -> {
            CheckBox checkBox = new CheckBox();
            checkBox.setValue(field.getValue(r));
            checkBox.addValueChangeListener(event -> action.accept(r, event.getValue()));
            checkBox.setEnabled(!disabled.test(r));
            return checkBox;
        };
    }

    private ValueProvider<R, Button> buttonProvider(String actionTitle, Consumer<R> action, Predicate<R> disabled) {
        return record -> {
            Button btn = new Button(actionTitle);
            btn.addStyleNames(ValoTheme.BUTTON_SMALL);
            btn.addClickListener(event -> action.accept(record));
            btn.setEnabled(!disabled.test(record));
            return btn;
        };
    }

    private ValueProvider<R, Link> linkProvider(Field<?> field, Function<R, String> linkProvider) {
        return record -> ofNullable(record.get(field))
            .map(val -> new Link(Objects.toString(val), new InternalResource(linkProvider.apply(record))))
            .orElse(new Link());
    }

    private Consumer<R> navigateTo(Function<R, String> stateProvider) {
        return r -> UI.getCurrent().getNavigator().navigateTo(stateProvider.apply(r));
    }

    protected Predicate<R> alwaysDisabled() {
        return r -> true;
    }

    protected Predicate<R> alwaysEnabled() {
        return r -> false;
    }

    protected void tuneGrid() {
        this.setSelectionMode(Grid.SelectionMode.NONE);
        this.addColumn(i -> "").setCaption("").setWidthUndefined().setSortable(false);
        this.setSizeFull();
    }

    protected void totalCountAsCaption(int size, boolean moreRecords) {
        this.setCaption("Total: " + size + (moreRecords ? "+" : ""));
    }

    private void addRowPreview() {
        this.addItemClickListener((ItemClickListener<R>) event -> {
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

}
