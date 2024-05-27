package fintech.bo.components.payments.statement;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Link;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.CloudFile;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.utils.CloudFileDownloader;
import fintech.bo.db.jooq.payment.tables.Institution;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import static fintech.bo.db.jooq.payment.tables.Statement.STATEMENT;

@Slf4j
@SpringView(name = StatementsView.NAME)
public class StatementsView extends VerticalLayout implements View {

    public static final String NAME = "statements";

    @Autowired
    private DSLContext db;

    private StatementDataProvider dataProvider;

    @Autowired
    private FileApiClient fileApiClient;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Statements");

        dataProvider = new StatementDataProvider(db);

        removeAllComponents();
        GridViewLayout layout = buildTop();
        Grid<Record> grid = buildGrid(dataProvider);
        layout.setContent(grid);
        addComponentsAndExpand(layout);
    }

    private Grid<Record> buildGrid(StatementDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addColumn(STATEMENT.ID);
        builder.addColumn(STATEMENT.STATUS).setStyleGenerator(statusStyle());
        builder.addColumn(Institution.INSTITUTION.NAME);
        builder.addColumn(STATEMENT.ACCOUNT_NUMBER).setWidth(250);
        builder.addComponentColumn(this::generateDownloadLink).setCaption("File").setWidth(200);
        builder.addColumn(STATEMENT.START_DATE);
        builder.addColumn(STATEMENT.END_DATE);
        builder.addColumn(StatementDataProvider.TOTAL_COUNT);
        builder.addColumn(StatementDataProvider.PROCESSED_COUNT);
        builder.addColumn(StatementDataProvider.IGNORED_COUNT);
        builder.addColumn(StatementDataProvider.ERROR_COUNT);
        builder.addColumn(STATEMENT.ERROR);
        builder.addAuditColumns(STATEMENT);
        builder.sortDesc(STATEMENT.ID);
        return builder.build(dataProvider);
    }

    private GridViewLayout buildTop() {
        GridViewLayout layout = new GridViewLayout();
        layout.setRefreshAction((e) -> refresh());

        return layout;
    }

    private Link generateDownloadLink(Record record) {
        Link downloadLink = new Link();
        downloadLink.setCaption(record.get(STATEMENT.FILE_NAME));

        CloudFile cloudFile = new CloudFile(record.get(STATEMENT.FILE_ID), record.get(STATEMENT.FILE_NAME));
        CloudFileDownloader onDemandFileDownloader = new CloudFileDownloader(fileApiClient, () -> cloudFile, file -> {
            Notifications.trayNotification("File downloaded: " + file.getName());
        });
        onDemandFileDownloader.extend(downloadLink);

        return downloadLink;
    }

    private void refresh() {
        dataProvider.refreshAll();
    }

    private static StyleGenerator<Record> statusStyle() {
        return item -> {
            String status = item.get(STATEMENT.STATUS);
            if (StatementConstants.STATEMTN_STATUS_PROCESSED.equals(status)) {
                return BackofficeTheme.TEXT_SUCCESS;
            } else if (StatementConstants.STATEMTN_STATUS_NEW.equals(status)) {
                return BackofficeTheme.TEXT_ACTIVE;
            } else if (StatementConstants.STATEMTN_STATUS_FAILED.equals(status)) {
                return BackofficeTheme.TEXT_DANGER;
            } else {
                return "";
            }
        };
    }
}
