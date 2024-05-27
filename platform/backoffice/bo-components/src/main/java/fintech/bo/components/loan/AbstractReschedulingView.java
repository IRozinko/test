package fintech.bo.components.loan;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.common.SearchField;
import fintech.bo.components.layouts.GridViewLayout;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Slf4j
public abstract class AbstractReschedulingView extends VerticalLayout implements View {

    public static final String NAME = "rescheduling";

    private final ReschedulingComponents reschedulingComponents;

    private Grid<Record> grid;
    protected SearchField search;
    protected ComboBox<String> status;
    private ReschedulingDataProvider dataProvider;

    protected AbstractReschedulingView(ReschedulingComponents reschedulingComponents) {
        this.reschedulingComponents = Objects.requireNonNull(reschedulingComponents);
    }

    @PostConstruct
    public void init() {
        dataProvider = reschedulingComponents.dataProvider();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Rescheduled Loans");
        GridViewLayout layout = new GridViewLayout();

        buildGrid(layout);
        buildTop(layout);
        addComponentsAndExpand(layout);
    }

    protected void buildTop(GridViewLayout layout) {
        search = layout.searchFieldWithOptions();
        search.addValueChangeListener(event -> refresh());
        search.addFieldOptions(dataProvider.getSearchFieldsNames());

        status = reschedulingComponents.statusComboBox();
        status.setCaption("Status");
        status.addValueChangeListener(event -> refresh());


        layout.addTopComponent(search);
        layout.addTopComponent(status);
        layout.setRefreshAction((e) -> refresh());
    }

    protected void buildGrid(GridViewLayout layout) {
        grid = reschedulingComponents.grid(dataProvider);
        layout.setContent(grid);
    }

    protected void refresh() {
        refreshSearchValues();
        refreshDataProvider();
    }

    protected void refreshSearchValues() {
        dataProvider.setTextFilter(search.getValue());
        dataProvider.setStatus(status.getValue());
    }

    protected void refreshDataProvider() {
        grid.getDataProvider().refreshAll();
    }

    public ReschedulingDataProvider getDataProvider() {
        return dataProvider;
    }
}
