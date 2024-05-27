package fintech.bo.components.application;

import com.vaadin.addon.daterangefield.DateRangeField;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.common.Fields;
import fintech.bo.components.common.SearchField;
import fintech.bo.components.layouts.GridViewLayout;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Slf4j
public abstract class AbstractLoanApplicationsView extends VerticalLayout implements View {

    public static final String NAME = "loan-applications";

    protected final LoanApplicationComponents applicationComponents;

    protected Grid<Record> grid;
    protected SearchField search;
    protected ComboBox<String> type;
    protected ComboBox<String> status;
    protected ComboBox<String> workflowStep;
    protected ComboBox<String> sourceType;
    protected ComboBox<String> sourceName;
    protected ComboBox<String> closeReason;
    protected DateRangeField submitDate;
    protected DateRangeField closeDate;
    protected LoanApplicationDataProvider dataProvider;

    protected AbstractLoanApplicationsView(LoanApplicationComponents applicationComponents) {
        this.applicationComponents = Objects.requireNonNull(applicationComponents);
    }

    @PostConstruct
    private void init(){
        dataProvider = applicationComponents.dataProvider();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Loan Applications");
        GridViewLayout layout = new GridViewLayout();

        buildGrid(layout);
        buildTop(layout);
        addComponentsAndExpand(layout);
    }

    protected void buildTop(GridViewLayout layout) {
        search = layout.searchFieldWithOptions();
        search.addValueChangeListener(event -> refresh());
        search.addFieldOptions(dataProvider.getSearchFieldsNames());

        type = applicationComponents.typeComboBox();
        type.addValueChangeListener(event -> refresh());

        status = applicationComponents.statusComboBox();
        status.addValueChangeListener(event -> refresh());

        workflowStep = applicationComponents.workflowStepComboBox();
        workflowStep.addValueChangeListener(event -> refresh());

        sourceType = applicationComponents.sourceType();
        sourceType.addValueChangeListener(event -> refresh());

        sourceName = applicationComponents.sourceName();
        sourceName.addValueChangeListener(event -> refresh());

        closeReason = applicationComponents.closeReason();
        closeReason.addValueChangeListener(event -> refresh());

        submitDate = Fields.dateRangeField("Submit date");
        submitDate.addValueChangeListener(event -> refresh());

        closeDate = Fields.dateRangeField("Close date");
        closeDate.addValueChangeListener(event -> refresh());

        layout.addTopComponent(search);
        layout.addTopComponent(type);
        layout.addTopComponent(status);
        layout.addTopComponent(workflowStep);
        layout.addTopComponent(sourceType);
        layout.addTopComponent(sourceName);
        layout.addTopComponent(closeReason);
        layout.addTopComponent(submitDate);
        layout.addTopComponent(closeDate);
        layout.setRefreshAction(e -> refresh());
    }

    protected void buildGrid(GridViewLayout layout) {
        grid = applicationComponents.grid(dataProvider);
        layout.setContent(grid);
    }

    protected void refresh() {
        refreshSearchValues();
        refreshDataProvider();
    }

    protected void refreshSearchValues() {
        dataProvider.setTextFilter(search.getValue());
        dataProvider.setType(type.getValue());
        dataProvider.setStatusDetail(status.getValue());
        dataProvider.setWorkflowStep(workflowStep.getValue());
        dataProvider.setSourceType(sourceType.getValue());
        dataProvider.setSourceName(sourceName.getValue());
        dataProvider.setCloseReason(closeReason.getValue());
        dataProvider.setSubmitDateFrom(submitDate.getBeginDate());
        dataProvider.setSubmitDateTo(submitDate.getEndDate());
        dataProvider.setCloseDateFrom(closeDate.getBeginDate());
        dataProvider.setCloseDateTo(closeDate.getEndDate());
    }

    protected void refreshDataProvider() {
        grid.getDataProvider().refreshAll();
    }

    public LoanApplicationDataProvider getDataProvider() {
        return dataProvider;
    }
}
