package fintech.bo.spain.alfa.task;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.task.CommonTaskView;

public class ExtensionSaleCallTask extends CommonTaskView {

    @Override
    public Component buildView(BusinessObjectLayout baseLayout) {
        return new VerticalLayout(
            getHelper().callClientComponent(getTask().getClientId()),
            getHelper().completeTaskComponent(getTask(), resolutions())
        );
    }
}
