package fintech.bo.components.emails;

import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.JooqClientDataService;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.bo.db.jooq.email.Tables.LOG;
import static fintech.bo.db.jooq.notification.Tables.NOTIFICATION_;

@Component
public class EmailsComponents {

    @Autowired
    private DSLContext db;

    @Autowired
    private JooqClientDataService jooqClientDataService;

    public EmailLogDataProvider emailLogDataProvider() {
        return new EmailLogDataProvider(db, jooqClientDataService);
    }

    public Grid<Record> emailLogGrid(EmailLogDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addActionColumn("Open", record -> {
            EmailPreviewDialog dialog = new EmailPreviewDialog(record.get(LOG.SUBJECT), record.get(LOG.BODY));
            UI.getCurrent().addWindow(dialog);
        });
        builder.addColumn(LOG.ID);
        builder.addColumn(NOTIFICATION_.CMS_KEY).setWidth(250);
        builder.addColumn(LOG.SUBJECT).setWidth(250);
        builder.addColumn(LOG.CREATED_BY);
        builder.addColumn(LOG.SEND_TO);
        builder.addColumn(LOG.SENDING_STATUS);
        builder.addColumn(LOG.ERROR);
        builder.addColumn(LOG.ATTEMPTS);
        builder.addColumn(LOG.ATTACHMENT_FILE_IDS);
        builder.addAuditColumns(LOG);
        builder.sortDesc(LOG.CREATED_AT);
        return builder.build(dataProvider);
    }
}
