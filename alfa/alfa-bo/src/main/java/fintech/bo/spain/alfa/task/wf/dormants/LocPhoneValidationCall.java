package fintech.bo.spain.alfa.task.wf.dormants;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.task.CommonTaskView;

public class LocPhoneValidationCall extends CommonTaskView {

    @Override
    public Component buildView(BusinessObjectLayout baseLayout) {
        return new VerticalLayout(
            getHelper().callClientComponent(getTask().getClientId()),
            getHelper().completeTaskComponent(getTask(), resolutions())
        );
    }

}
