package fintech.bo.components;


import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.utils.DataProviderExporter;
import org.apache.commons.lang3.text.WordUtils;
import org.jooq.Record;
import org.jooq.TableField;

import java.time.LocalDateTime;
import java.util.function.Function;

import static com.vaadin.ui.themes.ValoTheme.BUTTON_SMALL;

public class GridHelper {

    public static final String ALIGN_RIGHT_STYLE = "v-align-right";

    public static <T> StyleGenerator<T> alignRightStyle() {
        return item -> "v-align-right";
    }

    public static <T> StyleGenerator<T> alignCenterStyle() {
        return item -> "v-align-center";
    }

    public static <T> FooterCell addTotalCountFooter(Grid<T> grid, JooqDataProvider dataProvider) {
        final FooterCell footer = grid.appendFooterRow().join(grid.getColumns().toArray(new Grid.Column[0]));
        dataProvider.addSizeListener((size, moreRecords) -> footer.setText("Total: " + size + (((Boolean) moreRecords) ? "+" : "")));
        return footer;
    }

    public static <T> void addTotalCountAsCaption(Grid<T> grid, JooqDataProvider dataProvider) {
        dataProvider.addSizeListener((size, moreRecords) -> grid.setCaption("Total: " + size + (((Boolean) moreRecords) ? "+" : "")));
    }

    public static <T> FooterCell addExportFooter(Grid<T> grid, String fileName) {
        final FooterCell footer = grid.appendFooterRow().join(grid.getColumns().toArray(new Grid.Column[0]));
        Button exportButton = new Button("Export");
        exportButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        if (grid.getDataProvider() instanceof ExportableDataProvider) {
            DataProviderExporter.xlsDownloader(grid.getDataProvider(), fileName, "Sheet 1").extend(exportButton);
        }
        footer.setComponent(exportButton);
        return footer;
    }

    public static <T extends Record, G extends Record, R> Grid.Column<G, R> addColumn(Grid<G> grid, TableField<T, R> field) {
        Grid.Column<G, R> column = grid.addColumn(record -> record.get(field));
        return column.setWidth(150)
            .setCaption(toReadable(field.getName()))
            .setId(String.format("%s.%s", field.getTable().getName(), field.getName()));
    }

    public static <T extends Record, G extends Record, R extends LocalDateTime> Grid.Column<G, R> addDateTimeColumn(Grid<G> grid, TableField<T, R> field) {
        return addColumn(grid, field).setRenderer(new LocalDateTimeRenderer(Formats.DATE_TIME_FORMAT)).setWidth(200);
    }

    public static <T> Grid.Column<T, Button> addNavigationColumn(Grid<T> grid, String title, Function<T, String> stateProvider) {
        return grid.addColumn(record -> {
            String state = stateProvider.apply(record);
            Button btn = new Button(title);
            btn.addStyleNames(BUTTON_SMALL);
            btn.addClickListener(event -> UI.getCurrent().getNavigator().navigateTo(state));
            btn.setEnabled(state != null);
            return btn;
        }, new ComponentRenderer()).setWidth(80).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW).setSortable(false);
    }

    private static String toReadable(String name) {
        return WordUtils.capitalize(name.replaceAll("_", " "));
    }

}
