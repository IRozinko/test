package fintech.bo.components.dowjones;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.dialogs.InfoDialog;
import org.jooq.Record;

public class DowJonesDialog {

    private final Panel scroll;

    public DowJonesDialog(Record record) {
        PropertyLayout props = new PropertyLayout("DowJones results");

        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(props);
        scroll = new Panel();
        scroll.setContent(vl);
    }

    public InfoDialog view() {
        InfoDialog dialog = Dialogs.preview("DowJones results", scroll);
        dialog.setWidth(700, Sizeable.Unit.PIXELS);
        dialog.setHeight(700, Sizeable.Unit.PIXELS);
        return dialog;
    }

}
