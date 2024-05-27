package fintech.bo.components.callcenter;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import fintech.bo.components.PropertyLayout;
import org.jooq.Record;

import static fintech.bo.db.jooq.presence.Tables.OUTBOUND_LOAD;

public class OutboundLoadInfoDialog extends Window {

    public OutboundLoadInfoDialog(Record record) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);

        PropertyLayout propertyLayout = new PropertyLayout("Outbound Load");
        propertyLayout.add("Id", record.get(OUTBOUND_LOAD.ID));
        propertyLayout.add("Service Id", record.get(OUTBOUND_LOAD.SERVICE_ID));
        propertyLayout.add("Load Id", record.get(OUTBOUND_LOAD.LOAD_ID));
        propertyLayout.add("Status", record.get(OUTBOUND_LOAD.STATUS));
        propertyLayout.add("Added At", record.get(OUTBOUND_LOAD.ADDED_AT));
        propertyLayout.add("Description", record.get(OUTBOUND_LOAD.DESCRIPTION));
        propertyLayout.add("Updated At", record.get(OUTBOUND_LOAD.UPDATED_AT));

        layout.addComponent(new Panel(propertyLayout));
        setContent(layout);
        setWidth(400, Unit.PIXELS);
        center();
    }

}
