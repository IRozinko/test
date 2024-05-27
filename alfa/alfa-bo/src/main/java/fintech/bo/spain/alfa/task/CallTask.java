package fintech.bo.spain.alfa.task;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.task.CommonTaskView;

public class CallTask extends CommonTaskView {

    @Override
    public Component buildView(BusinessObjectLayout baseLayout) {
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(getHelper().callClientComponent(getTask().getClientId()));
        layout.addComponent(getHelper().completeTaskComponent(getTask(), resolutions()));
        return layout;
    }

}
