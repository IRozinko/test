package fintech.bo.components.sms;

import com.vaadin.ui.Grid;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.dialogs.Dialogs;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.bo.db.jooq.notification.Tables.NOTIFICATION_;
import static fintech.bo.db.jooq.sms.Tables.LOG;

@Component
public class SmsComponents {

    @Autowired
    private DSLContext db;

    @Autowired
    private JooqClientDataService jooqClientDataService;

    public SmsLogDataProvider smsLogDataProvider() {
        return new SmsLogDataProvider(db, jooqClientDataService);
    }

    public Grid<Record> smsLogGrid(SmsLogDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addActionColumn("Open", record -> {
            Dialogs.showText("SMS text", record.get(LOG.SMS_TEXT));
        });
        builder.addColumn(LOG.ID);
        builder.addColumn(NOTIFICATION_.CMS_KEY).setWidth(250);
        builder.addColumn(LOG.SMS_TEXT).setWidth(250);
        builder.addColumn(LOG.CREATED_BY);
        builder.addColumn(LOG.SEND_TO);
        builder.addColumn(LOG.SENDING_STATUS);
        builder.addColumn(LOG.DELIVERY_REPORT_STATUS);
        builder.addColumn(LOG.ERROR);
        builder.addColumn(LOG.ATTEMPTS);
        builder.addAuditColumns(LOG);
        builder.sortDesc(LOG.CREATED_AT);
        return builder.build(dataProvider);
    }
}
