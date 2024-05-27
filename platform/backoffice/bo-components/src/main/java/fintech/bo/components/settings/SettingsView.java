package fintech.bo.components.settings;


import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.GridSortOrderBuilder;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.Formats;
import fintech.bo.components.GridHelper;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.security.SecuredView;
import fintech.bo.db.jooq.settings.tables.records.PropertyRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import static fintech.bo.db.jooq.settings.Settings.SETTINGS;

@Slf4j
@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.SETTINGS_EDIT})
@SpringView(name = SettingsView.NAME)
public class SettingsView extends VerticalLayout implements View {
    public static final String NAME = "settings";

    @Autowired
    private SettingsComponents settingsComponents;

    private PropertyDataProvider dataProvider;
    private TextField search;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Settings");
        removeAllComponents();
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildTop(GridViewLayout layout) {
        search = layout.searchField();
        search.addValueChangeListener(e -> refresh());
        search.setPlaceholder("Search by name...");

        layout.addTopComponent(search);
        layout.setRefreshAction(e -> refresh());
    }

    private void refresh() {
        dataProvider.setFilterText(search.getValue());
        dataProvider.refreshAll();
    }

    private void buildGrid(GridViewLayout layout) {
        Grid<PropertyRecord> grid = new Grid<>();

        grid.addColumn(record -> {
            Button button = new Button("Edit");
            button.addStyleName(ValoTheme.BUTTON_SMALL);
            button.addClickListener(e -> openEditDialog(record));
            return button;
        }, new ComponentRenderer()).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW).setWidth(80).setSortable(false);


        Grid.Column<PropertyRecord, String> name = grid.addColumn(PropertyRecord::getName)
                .setCaption("Name")
                .setId(SETTINGS.PROPERTY.NAME.getName())
                .setWidth(400);

        grid.addColumn((ValueProvider<PropertyRecord, Object>) propertyRecord -> {
            switch (propertyRecord.getType()) {
                case "BOOLEAN":
                    return propertyRecord.getBooleanValue();
                case "DATE":
                    return Formats.dateFormatter().format(propertyRecord.getDateValue());
                case "DATETIME":
                    return Formats.dateTimeFormatter().format(propertyRecord.getDateTimeValue());
                case "DECIMAL":
                    return propertyRecord.getDecimalValue();
                case "NUMBER":
                    return propertyRecord.getNumberValue();
                case "TEXT":
                    return propertyRecord.getTextValue();
            }
            return "";
        }, new TextRenderer())
                .setCaption("Value")
                .setWidth(200)
                .setSortable(false);

        grid.addColumn(PropertyRecord::getDescription)
                .setCaption("Description")
                .setId(SETTINGS.PROPERTY.DESCRIPTION.getName());
        grid.addColumn(PropertyRecord::getUpdatedBy)
            .setCaption("Updated by")
            .setId(SETTINGS.PROPERTY.UPDATED_BY.getName());
        grid.addColumn(PropertyRecord::getUpdatedAt)
            .setCaption("Updated at")
            .setRenderer(new LocalDateTimeRenderer(Formats.DATE_TIME_FORMAT))
            .setId(SETTINGS.PROPERTY.UPDATED_AT.getName());

        dataProvider = settingsComponents.dataProvider();
        grid.setDataProvider(dataProvider);
        grid.setSortOrder(new GridSortOrderBuilder<PropertyRecord>().thenAsc(name));
        GridHelper.addTotalCountAsCaption(grid, dataProvider);
        layout.setContent(grid);
    }

    private void openEditDialog(PropertyRecord item) {
        EditPropertyDialog dialog = settingsComponents.editPropertyDialog(item);
        dialog.addCloseListener((e) -> refresh());
        UI.getCurrent().addWindow(dialog);
    }
}
