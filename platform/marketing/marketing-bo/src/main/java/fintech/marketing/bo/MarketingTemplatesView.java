package fintech.marketing.bo;

import com.google.common.base.MoreObjects;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.StringResponse;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.common.HtmlPreview;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.SecuredView;
import fintech.bo.db.jooq.marketing.tables.MarketingTemplate;
import fintech.bo.db.jooq.marketing.tables.records.MarketingTemplateRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;



@Slf4j
@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.CMS_EDIT})
@SpringView(name = MarketingTemplatesView.NAME)
public class MarketingTemplatesView extends VerticalLayout implements View {

    public static final String NAME = "marketing-templates";

    @Autowired
    private DSLContext db;

    @Autowired
    private MarketingApiClient apiClient;

    private Grid<MarketingTemplateRecord> grid;
    private TextField search;
    private MarketingTemplatesDataProvider dataProvider;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Marketing Templates");
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildGrid(GridViewLayout layout) {
        dataProvider = new MarketingTemplatesDataProvider(db);
        JooqGridBuilder<MarketingTemplateRecord> builder = new JooqGridBuilder<>();

        builder.addActionColumn("Edit", this::edit);
        builder.addActionColumn("Preview", this::preview);
        builder.addColumn(MarketingTemplate.MARKETING_TEMPLATE.NAME);
        builder.addColumn(MarketingTemplate.MARKETING_TEMPLATE.UPDATED_BY);
        builder.addColumn(MarketingTemplate.MARKETING_TEMPLATE.UPDATED_AT);

        grid = builder.build(dataProvider);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        layout.setContent(grid);
    }

    private void preview(MarketingTemplateRecord record) {
        Call<StringResponse> call = apiClient.templatePreview(new IdRequest(record.getId()));
        BackgroundOperations.callApi("Rendering template", call, renderResponse -> {
            VerticalLayout layout = new VerticalLayout();
            layout.addComponent(previewPanel(renderResponse.getString()));
            Window previewWindow = new Window(record.getName() + " Preview");
            previewWindow.setContent(layout);
            previewWindow.center();
            previewWindow.setModal(true);
            getUI().addWindow(previewWindow);
        }, Notifications::errorNotification);
    }

    private Panel previewPanel(String body) {
        Panel panel = new Panel();
        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        panel.setWidth(100, Sizeable.Unit.PERCENTAGE);
        panel.setHeightUndefined();
        panel.setContent(new HtmlPreview(MoreObjects.firstNonNull(body, "null")));
        return panel;
    }

    private void edit(MarketingTemplateRecord item) {
        grid.select(item);
        EditMarketingTemplateDialog dialog = new EditMarketingTemplateDialog(apiClient, item);
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }

    private void buildTop(GridViewLayout layout) {
        search = layout.searchField();
        search.addValueChangeListener(event -> refresh());
        layout.addTopComponent(search);

        layout.setRefreshAction(e -> refresh());
        layout.addActionMenuItem("Add template", e -> addItem());
    }

    private void addItem() {
        EditMarketingTemplateDialog dialog = new EditMarketingTemplateDialog(apiClient, new MarketingTemplateRecord());
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }

    private void refresh() {
        dataProvider.setKey(search.getValue());
        dataProvider.refreshAll();
    }
}
