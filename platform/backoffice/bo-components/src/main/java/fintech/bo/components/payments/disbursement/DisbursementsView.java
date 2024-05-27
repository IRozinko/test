package fintech.bo.components.payments.disbursement;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.Formats;
import fintech.bo.components.common.SearchField;
import fintech.bo.components.layouts.GridViewLayout;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Slf4j
@SpringView(name = DisbursementsView.NAME)
public class DisbursementsView extends VerticalLayout implements View {

    public static final String NAME = "disbursements";

    @Autowired
    private DisbursementComponents disbursementComponents;

    private Grid<Record> grid;
    private SearchField search;
    private ComboBox<String> status;
    private DateField valueDate;
    private DisbursementDataProvider dataProvider;

    @PostConstruct
    public void init() {
        dataProvider = disbursementComponents.dataProvider();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Disbursements");
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildTop(GridViewLayout layout) {
        search = layout.searchFieldWithOptions();
        search.addValueChangeListener(event -> refresh());
        search.addFieldOptions(dataProvider.getSearchFieldsNames());

        status = disbursementComponents.statusComboBox();
        status.setCaption("Status");
        status.addValueChangeListener(event -> refresh());

        valueDate = new DateField("Value date");
        valueDate.setDateFormat(Formats.DATE_FORMAT);
        valueDate.setPlaceholder(Formats.DATE_FORMAT);
        valueDate.addValueChangeListener(event -> refresh());

        layout.addTopComponent(search);
        layout.addTopComponent(status);
        layout.addTopComponent(valueDate);
        layout.setRefreshAction(e -> refresh());
    }

    private void buildGrid(GridViewLayout layout) {
        grid = disbursementComponents.grid(dataProvider);
        layout.setContent(grid);
    }

    private void refresh() {
        dataProvider.setTextFilter(search.getValue());
        dataProvider.setStatusDetail(status.getValue());
        dataProvider.setValueDate(valueDate.getValue());
        grid.getDataProvider().refreshAll();
    }


}
