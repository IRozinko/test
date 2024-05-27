package fintech.bo.components.client;

import com.google.common.base.MoreObjects;
import com.vaadin.addon.daterangefield.DateRangeField;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.common.Fields;
import fintech.bo.components.common.SearchField;
import fintech.bo.components.layouts.GridViewLayout;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.crm.tables.EmailContact.EMAIL_CONTACT;

@Slf4j
public abstract class AbstractClientsView extends VerticalLayout implements View {

    public static final String NAME = "clients";

    private Grid<Record> grid;
    private SearchField search;

    @Autowired
    private DSLContext db;

    @Autowired
    private JooqClientDataService jooqClientDataService;

    private ClientGridDataProvider dataProvider;
    private DateRangeField createdDate;

    @PostConstruct
    private void init() {
        dataProvider = new ClientGridDataProvider(db, jooqClientDataService);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Clients");
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    protected void buildTop(GridViewLayout layout) {
        search = layout.searchFieldWithOptions();
        search.addValueChangeListener(event -> refresh());
        search.addFieldOptions(dataProvider.getSearchFieldsNames());

        createdDate = Fields.dateRangeField("Created date");
        createdDate.addValueChangeListener(event -> refresh());

        layout.addTopComponent(search);
        layout.addTopComponent(createdDate);
        layout.setRefreshAction(e -> refresh());
    }

    private void buildGrid(GridViewLayout layout) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        addColumns(builder);
        builder.sortDesc(CLIENT.ID);
        grid = builder.build(dataProvider);
        layout.setContent(grid);
    }

    protected void addColumns(JooqGridBuilder<Record> builder) {
        builder.addNavigationColumn("Open", r -> "client/" + r.get(CLIENT.ID));
        builder.addColumn(CLIENT.CLIENT_NUMBER).setWidth(120);
        builder.addColumn(CLIENT.FIRST_NAME);
        builder.addColumn(CLIENT.LAST_NAME);
        builder.addColumn(CLIENT.SEGMENTS_TEXT).setStyleGenerator((StyleGenerator<Record>) item -> {
            String segment = MoreObjects.firstNonNull(item.get(CLIENT.SEGMENTS_TEXT), "");
            if (StringUtils.containsIgnoreCase(segment, "Rejected")) {
                return BackofficeTheme.TEXT_DANGER;
            } else if (StringUtils.containsIgnoreCase(segment, "OpenLoan")) {
                return BackofficeTheme.TEXT_SUCCESS;
            } else if (StringUtils.containsIgnoreCase(segment, "Unidentified")) {
                return BackofficeTheme.TEXT_GRAY;
            } else {
                return null;
            }
        }).setWidth(200);
        builder.addColumn(ClientGridDataProvider.FIELD_NEXT_ACTIVITY).setWidth(200);
        builder.addColumn(ClientGridDataProvider.FIELD_LAST_CLOSE_REASON).setWidth(200);
        builder.addColumn(EMAIL_CONTACT.EMAIL).setWidth(250);
        builder.addColumn(CLIENT.PHONE);
        builder.addColumn(CLIENT.DOCUMENT_NUMBER);
        builder.addColumn(CLIENT.ACCOUNT_NUMBER);
        builder.addColumn(CLIENT.DATE_OF_BIRTH);
        builder.addAuditColumns(CLIENT);
        builder.addColumn(CLIENT.ID);
    }

    protected void refresh() {
        refreshSearchValues();
        refreshDataProvider();
    }

    protected void refreshSearchValues() {
        dataProvider.setTextFilter(search.getValue());
        dataProvider.setCreatedFrom(createdDate.getBeginDate());
        dataProvider.setCreatedTo(createdDate.getEndDate());
    }

    protected void refreshDataProvider() {
        grid.getDataProvider().refreshAll();
    }

    public ClientGridDataProvider getDataProvider() {
        return dataProvider;
    }
}
