package fintech.marketing.bo;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.loan.promocodes.PromoCodeQueries;
import fintech.bo.components.security.SecuredView;
import fintech.bo.db.jooq.marketing.Tables;
import fintech.bo.db.jooq.marketing.tables.records.MarketingCampaignRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;


@Slf4j
@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.MARKETING_MANAGER})
@SpringView(name = MarketingOneTimeCampaignView.NAME)
public class MarketingOneTimeCampaignView extends VerticalLayout implements View {

    public static final String NAME = "marketing-one-time-campaigns";

    @Autowired
    private DSLContext db;
    private Grid<MarketingCampaignRecord> grid;
    private TextField search;
    private MarketingCampaignsDataProvider dataProvider;

    @Autowired
    private MarketingApiClient marketingTemplatesApiClient;

    @Autowired
    private FileApiClient fileApiClient;

    @Autowired
    private PromoCodeQueries promoCodeQueries;

    @Autowired
    private MarketingTemplateQueries templateQueries;


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Marketing one time campaigns");
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildGrid(GridViewLayout layout) {
        dataProvider = new MarketingCampaignsDataProvider(db, false);
        JooqGridBuilder<MarketingCampaignRecord> builder = new JooqGridBuilder<>();
        builder.addColumn(Tables.MARKETING_CAMPAIGN.NAME);
        builder.addAuditColumns(Tables.MARKETING_CAMPAIGN);
        builder.addComponentColumn(this::resendButton).setWidth(100).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW);
        grid = builder.build(dataProvider);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        layout.setContent(grid);
    }


    private Button resendButton(MarketingCampaignRecord record) {
        Button button = new Button(  "Resend");
        button.addClickListener((Button.ClickListener) event -> {
            ResendMarketingCampaignDialog dialog = new ResendMarketingCampaignDialog(record);
            dialog.addCloseListener(e -> refresh());
            getUI().addWindow(dialog);
        });

        return button;
    }

    private void buildTop(GridViewLayout layout) {
        search = layout.searchField();
        search.addValueChangeListener(event -> refresh());
        layout.addTopComponent(search);
        layout.setRefreshAction(e -> refresh());
    }

    private void refresh() {
        dataProvider.setKey(search.getValue());
        dataProvider.refreshAll();
    }

}
