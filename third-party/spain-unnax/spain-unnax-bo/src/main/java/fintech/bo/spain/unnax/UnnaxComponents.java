package fintech.bo.spain.unnax;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import fintech.bo.components.JooqGridBuilder;
import fintech.spain.unnax.db.jooq.tables.records.CallbackRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.spain.unnax.db.jooq.Tables.CALLBACK;

@Component
public class UnnaxComponents {

    @Autowired
    private Queries queries;

    public Grid<CallbackRecord> grid(UnnaxLogDataProvider dataProvider, UnnaxView view) {
        JooqGridBuilder<CallbackRecord> builder = new JooqGridBuilder<>();
        builder.addActionColumn("View", callbackRecord -> showEventDetails(callbackRecord, view));
        builder.addColumn(CALLBACK.EVENT).setWidth(350);
        builder.addColumn(CALLBACK.DATE);
        builder.addColumn(CALLBACK.DATA).setWidthUndefined();
        return builder.build(dataProvider);
    }

    private void showEventDetails(CallbackRecord record, UnnaxView view) {
        ViewCallbackDialog dialog = new ViewCallbackDialog(record, queries);
        dialog.addCloseListener(e -> view.refresh());
        UI.getCurrent().addWindow(dialog);
    }

    public ComboBox<String> callbackFilter() {
        ComboBox<String> comboBox = new ComboBox<>("Callback");
        comboBox.setItems(queries.callbackTypes());
        return comboBox;
    }

}
