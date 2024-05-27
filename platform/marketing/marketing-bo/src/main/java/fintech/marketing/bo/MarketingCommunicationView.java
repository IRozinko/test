package fintech.marketing.bo;


import com.google.common.collect.Sets;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.security.SecuredView;
import fintech.bo.db.jooq.marketing.Tables;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import static fintech.bo.db.jooq.lending.Tables.PROMO_CODE;
import static fintech.marketing.bo.MarketingCommunicationsDataProvider.CANCELLED;
import static fintech.marketing.bo.MarketingCommunicationsDataProvider.ERROR;
import static fintech.marketing.bo.MarketingCommunicationsDataProvider.QUEUED;
import static fintech.marketing.bo.MarketingCommunicationsDataProvider.SENT;


@Slf4j
@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.CMS_EDIT})
@SpringView(name = MarketingCommunicationView.NAME)
public class MarketingCommunicationView extends VerticalLayout implements View {

    public static final String NAME = "marketing-communications";

    @Autowired
    private DSLContext db;

    private MarketingCommunicationsDataProvider queueDataProvider;
    private MarketingCommunicationsDataProvider sentDataProvider;

    @Autowired
    private MarketingApiClient apiClient;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Marketing overview");
        queueDataProvider = new MarketingCommunicationsDataProvider(db, Sets.newHashSet(CANCELLED, QUEUED, ERROR));
        sentDataProvider = new MarketingCommunicationsDataProvider(db, Sets.newHashSet(SENT));

        GridViewLayout queueLayout = new GridViewLayout();
        buildTop(queueLayout, queueDataProvider);
        buildGrid(queueLayout, queueDataProvider);

        GridViewLayout sentLayout = new GridViewLayout();
        buildTop(sentLayout, sentDataProvider);
        buildGrid(sentLayout, sentDataProvider);

        VerticalLayout layout = new VerticalLayout();

        TabSheet tabsheet = new TabSheet();
        tabsheet.addTab(queueLayout, "Queue");
        tabsheet.addTab(sentLayout, "Sent");
        tabsheet.setSizeFull();
        layout.addComponentsAndExpand(tabsheet);
        addComponentsAndExpand(layout);
    }

    private void buildGrid(GridViewLayout layout, MarketingCommunicationsDataProvider provider) {

        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addColumn(Tables.MARKETING_CAMPAIGN.NAME);
        builder.addColumn(Tables.MARKETING_CAMPAIGN.SCHEDULE_TYPE);
        builder.addColumn(Tables.MARKETING_COMMUNICATION.NEXT_ACTION_AT, "Date schedule");
        builder.addColumn(Tables.MARKETING_COMMUNICATION.VIEW_RATE, "Open rate");
        builder.addColumn(Tables.MARKETING_COMMUNICATION.CLICK_RATE, "Click rate");
        builder.addColumn(PROMO_CODE.CODE, "Promo code");
        builder.addColumn(Tables.MARKETING_COMMUNICATION.TARGETED_USERS, "Targeted users");
        builder.addColumn(Tables.MARKETING_COMMUNICATION.STATUS);
        builder.addColumn(Tables.MARKETING_COMMUNICATION.REMINDER).setWidth(80);
        Grid<Record> grid = builder.build(provider);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        layout.setContent(grid);
    }

    private void buildTop(GridViewLayout layout, MarketingCommunicationsDataProvider provider) {
        TextField search = layout.searchField();
        search.addValueChangeListener(event -> refresh(provider, search.getValue()));
        layout.addTopComponent(search);
        layout.setRefreshAction(e -> refresh(provider, search.getValue()));
    }

    private void refresh(MarketingCommunicationsDataProvider provider, String value) {
        provider.setKey(value);
        provider.refreshAll();
    }

}
