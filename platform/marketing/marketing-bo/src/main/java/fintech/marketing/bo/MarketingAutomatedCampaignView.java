package fintech.marketing.bo;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ConfirmDialog;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.SecuredView;
import fintech.bo.db.jooq.marketing.Tables;
import fintech.bo.db.jooq.marketing.tables.records.MarketingCampaignRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;


@Slf4j
@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.MARKETING_MANAGER})
@SpringView(name = MarketingAutomatedCampaignView.NAME)
public class MarketingAutomatedCampaignView extends VerticalLayout implements View {

    public static final String NAME = "marketing-automated-campaigns";

    @Autowired
    private DSLContext db;
    private Grid<MarketingCampaignRecord> grid;
    private TextField search;
    private MarketingCampaignsDataProvider dataProvider;

    @Autowired
    private MarketingApiClient marketingTemplatesApiClient;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Marketing automation");
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildGrid(GridViewLayout layout) {
        dataProvider = new MarketingCampaignsDataProvider(db, true);
        JooqGridBuilder<MarketingCampaignRecord> builder = new JooqGridBuilder<>();
        builder.addActionColumn("Edit", this::editCampaign);
        builder.addColumn(Tables.MARKETING_CAMPAIGN.NAME);
        builder.addAuditColumns(Tables.MARKETING_CAMPAIGN);
        builder.addColumn(Tables.MARKETING_CAMPAIGN.SCHEDULE_TYPE);
        builder.addColumn(Tables.MARKETING_CAMPAIGN.STATUS);
        builder.addComponentColumn(this::toggleStatusButton).setWidth(100).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW);
        grid = builder.build(dataProvider);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        layout.setContent(grid);
    }


    private Button toggleStatusButton(Record record) {
        boolean paused = "PAUSED".equals(record.get(Tables.MARKETING_CAMPAIGN.STATUS));
        Long id = record.get(Tables.MARKETING_CAMPAIGN.ID);
        Button button = new Button(paused ? "Resume" : "Pause");
        button.addClickListener((Button.ClickListener) event -> {
            ConfirmDialog dialog = new ConfirmDialog(button.getCaption() + " campaign?", (e) -> {
                BackgroundOperations.callApi("Executing request", marketingTemplatesApiClient.toggleCampaignStatus(new IdRequest(id)),
                    t -> {
                        Notifications.trayNotification("Done");
                        refresh();
                    },
                    Notifications::errorNotification);
            });
            UI.getCurrent().addWindow(dialog);
        });

        return button;
    }

    private void addItem() {
        EditMarketingCampaignDialog dialog = new EditMarketingCampaignDialog(new MarketingCampaignRecord());
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }

    private void editCampaign(MarketingCampaignRecord item) {
        grid.select(item);
        EditMarketingCampaignDialog dialog = new EditMarketingCampaignDialog(item);
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }

    private void buildTop(GridViewLayout layout) {
        search = layout.searchField();
        search.addValueChangeListener(event -> refresh());
        layout.addTopComponent(search);
        layout.setRefreshAction(e -> refresh());
        layout.addActionMenuItem("Add campaign", e -> addItem());
    }

    private void refresh() {
        dataProvider.setKey(search.getValue());
        dataProvider.refreshAll();
    }

}
