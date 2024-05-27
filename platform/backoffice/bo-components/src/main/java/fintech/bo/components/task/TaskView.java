package fintech.bo.components.task;

import com.vaadin.ui.Component;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.db.jooq.task.tables.records.TaskRecord;

public interface TaskView {

    Component build(BusinessObjectLayout baseView, TaskRecord task);

}
