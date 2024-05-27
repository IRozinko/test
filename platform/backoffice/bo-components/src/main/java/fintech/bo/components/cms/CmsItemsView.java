package fintech.bo.components.cms;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.CmsApiClient;
import fintech.bo.api.model.cms.DeleteCmsItemRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.SecuredView;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record6;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import java.time.LocalDateTime;
import java.util.Arrays;

import static fintech.bo.db.jooq.cms.Tables.ITEM;
import static org.jooq.impl.DSL.field;


@Slf4j
@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.CMS_EDIT})
@SpringView(name = CmsItemsView.NAME)
public class CmsItemsView extends VerticalLayout implements View {

    public static final String NAME = "cms-items";

    @Autowired
    private DSLContext db;

    @Autowired
    private CmsApiClient cmsApiClient;

    private Grid<Record6<String, String, String, String[], String, LocalDateTime>> grid;
    private TextField search;
    private ComboBox<String> type;
    private CmsItemDataProvider dataProvider;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("CMS Items");
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildGrid(GridViewLayout layout) {
        dataProvider = new CmsItemDataProvider(db);
        JooqGridBuilder<Record6<String, String, String, String[], String, LocalDateTime>> builder = new JooqGridBuilder<>();

        builder.addActionColumn("Edit", this::edit);
        builder.addActionColumn("Delete", this::deleteItem);
        builder.addColumn(field(CmsItemDataProvider.ITEM_TYPE));
        builder.addColumn(field(CmsItemDataProvider.ITEM_KEY)).setWidth(300);
        builder.addColumn(field(CmsItemDataProvider.DESCRIPTION)).setWidth(400);
        builder.addColumn(row -> {
            String[] locales = row.get(field(CmsItemDataProvider.LOCALES, String[].class));
            Arrays.sort(locales);
            return String.join(", ", locales);
        }).setWidth(100).setCaption("Locales");
        builder.addColumn(field(CmsItemDataProvider.UPDATED_BY));
        builder.addColumn(field(CmsItemDataProvider.UPDATED_AT, LocalDateTime.class));

        builder.sortAsc(field(CmsItemDataProvider.ITEM_KEY));

        grid = builder.build(dataProvider);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        layout.setContent(grid);
    }

    private void deleteItem(Record6<String, String, String, String[], String, LocalDateTime> item) {
        Dialogs.confirm("Delete item '" + item.get(field(CmsItemDataProvider.ITEM_KEY)) + "' ?", e -> {
            DeleteCmsItemRequest request = new DeleteCmsItemRequest();
            request.setKey(item.get(field(CmsItemDataProvider.ITEM_KEY, String.class)));
            Call<Void> call = cmsApiClient.deleteItem(request);
            BackgroundOperations.callApi("Deleting item", call, v -> {
                Notifications.trayNotification("Item deleted");
                refresh();
            }, Notifications::errorNotification);
        });
    }

    private void edit(Record6<String, String, String, String[], String, LocalDateTime> item) {
        grid.select(item);
        EditCmsItemDialog dialog = new EditCmsItemDialog(cmsApiClient, db, item.get(field(CmsItemDataProvider.ITEM_KEY), String.class));
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }

    private void buildTop(GridViewLayout layout) {
        type = typesComboBox();
        type.addValueChangeListener(event -> refresh());
        layout.addTopComponent(type);

        search = layout.searchField();
        search.addValueChangeListener(event -> refresh());
        layout.addTopComponent(search);

        layout.setRefreshAction(e -> refresh());
        layout.addActionMenuItem("Add item", e -> addItem());
        layout.addActionMenuItem("Manage locales", e -> manageLocales());
    }

    private void addItem() {
        AddCmsItemDialog dialog = new AddCmsItemDialog(cmsApiClient, db);
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }

    private void manageLocales() {
        ManageLocalesDialog dialog = new ManageLocalesDialog();
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }

    private void refresh() {
        dataProvider.setKey(search.getValue());
        dataProvider.setType(type.getValue());
        dataProvider.refreshAll();
    }

    private ComboBox<String> typesComboBox() {
        ComboBox<String> comboBox = new ComboBox<>("Type");
        comboBox.setTextInputAllowed(false);
        comboBox.setItems(db.selectDistinct(ITEM.ITEM_TYPE)
            .from(ITEM)
            .orderBy(ITEM.ITEM_TYPE)
            .fetchInto(String.class));
        return comboBox;
    }

}
