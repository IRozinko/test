package fintech.bo.components.invoice;

import com.vaadin.addon.daterangefield.DateRangeField;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.common.Fields;
import fintech.bo.components.layouts.GridViewLayout;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@SpringView(name = InvoicesView.NAME)
public class InvoicesView extends VerticalLayout implements View {

    public static final String NAME = "invoices";

    @Autowired
    private InvoiceComponents invoiceComponents;

    private Grid<Record> grid;
    private ComboBox<String> status;
    private ComboBox<String> statusDetail;
    private DateRangeField dueDate;
    private DateRangeField closeDate;
    private InvoiceDataProvider dataProvider;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Invoices");
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildTop(GridViewLayout layout) {
        status = invoiceComponents.statusComboBox();
        status.setCaption("Status");
        status.addValueChangeListener(event -> refresh());
        layout.addTopComponent(status);

        statusDetail = invoiceComponents.statusDetailComboBox();
        statusDetail.setCaption("Status detail");
        statusDetail.addValueChangeListener(event -> refresh());
        layout.addTopComponent(statusDetail);

        dueDate = Fields.dateRangeField("Due date");
        dueDate.addValueChangeListener(event -> refresh());
        layout.addTopComponent(dueDate);

        closeDate = Fields.dateRangeField("Close date");
        closeDate.addValueChangeListener(event -> refresh());
        layout.addTopComponent(closeDate);

        layout.setRefreshAction((e) -> refresh());
    }

    private void buildGrid(GridViewLayout layout) {
        dataProvider = invoiceComponents.dataProvider();
        grid = invoiceComponents.grid(dataProvider);
        layout.setContent(grid);
    }

    private void refresh() {
        dataProvider.setStatusDetail(statusDetail.getValue());
        dataProvider.setStatus(status.getValue());
        dataProvider.setDueDateFrom(dueDate.getBeginDate());
        dataProvider.setDueDateTo(dueDate.getEndDate());
        dataProvider.setCloseDateFrom(closeDate.getBeginDate());
        dataProvider.setCloseDateTo(closeDate.getEndDate());
        grid.getDataProvider().refreshAll();
    }
}
