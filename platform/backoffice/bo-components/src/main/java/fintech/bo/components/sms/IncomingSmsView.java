package fintech.bo.components.sms;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.db.jooq.sms.tables.records.IncomingRecord;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

import static fintech.bo.db.jooq.sms.Tables.INCOMING;


@Slf4j
@SpringView(name = IncomingSmsView.NAME)
public class IncomingSmsView extends VerticalLayout implements View {

    public static final String NAME = "incoming-sms";


    @Autowired
    private DSLContext db;
    private IncomingSmsDataProvider dataProvider;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        setCaption("Incoming SMS");

        removeAllComponents();
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildGrid(GridViewLayout layout) {
        dataProvider = new IncomingSmsDataProvider(db);
        JooqGridBuilder<IncomingRecord> builder = new JooqGridBuilder<>();
        builder.addColumn(INCOMING.ID);
        builder.addColumn(INCOMING.SOURCE);
        builder.addColumn(INCOMING.PHONE_NUMBER);
        builder.addColumn(INCOMING.TEXT);
        builder.addAuditColumns(INCOMING);
        builder.sortDesc(INCOMING.ID);
        layout.setContent(builder.build(dataProvider));
    }

    private void buildTop(GridViewLayout layout) {
        layout.setRefreshAction((e) -> refresh());
    }

    private void refresh() {
        dataProvider.refreshAll();
    }
}
