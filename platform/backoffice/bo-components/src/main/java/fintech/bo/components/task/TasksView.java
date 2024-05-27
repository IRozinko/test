package fintech.bo.components.task;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.common.SearchField;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.security.SecuredView;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Slf4j
@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.TASK_VIEW_LIST})
@SpringView(name = TasksView.NAME)
public class TasksView extends VerticalLayout implements View {

    public static final String NAME = "tasks";

    @Autowired
    private TaskComponents taskComponents;

    private Grid<Record> grid;
    private TaskDataProvider dataProvider;
    private SearchField search;
    private ComboBox<String> status;
    private ComboBox<String> taskType;

    @PostConstruct
    public void init() {
        dataProvider = taskComponents.taskDataProvider();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Tasks");
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);
    }

    private void buildTop(GridViewLayout layout) {
        search = layout.searchFieldWithOptions();
        search.addValueChangeListener(event -> refresh());
        search.addFieldOptions(dataProvider.getSearchFieldsNames());

        status = taskComponents.taskStatusComboBox();
        status.setCaption("Status");
        status.addValueChangeListener(event -> refresh());

        taskType = taskComponents.taskTypeComboBox();
        taskType.setCaption("Type");
        taskType.setWidth(200, Unit.PIXELS);
        taskType.addValueChangeListener(event -> refresh());

        layout.setRefreshAction((e) -> refresh());

        layout.addTopComponent(search);
        layout.addTopComponent(status);
        layout.addTopComponent(taskType);
    }

    private void buildGrid(GridViewLayout layout) {
        grid = taskComponents.taskGrid(dataProvider);
        layout.setContent(grid);
    }

    private void refresh() {
        dataProvider.setTextFilter(search.getValue());
        dataProvider.setStatus(status.getValue());
        dataProvider.setTaskType(taskType.getValue());
        grid.getDataProvider().refreshAll();
    }
}
