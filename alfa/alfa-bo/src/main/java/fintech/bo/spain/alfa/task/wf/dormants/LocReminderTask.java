package fintech.bo.spain.alfa.task.wf.dormants;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.task.CommonTaskView;

public class LocReminderTask extends CommonTaskView {

    public final static String SET_PWD_REMINDER_TYPE = "LocPrestoSetPwdReminderCall";
    public static final String INSTANTOR_REVIEW_REMINDER_TYPE = "LocInstantorReviewReminderCall";
    public static final String INSTANTOR_FORM_REMINDER_TYPE = "LocInstantorFormReminderCall";

    @Override
    public Component buildView(BusinessObjectLayout baseLayout) {
        return new VerticalLayout(
            getHelper().callClientComponent(getTask().getClientId()),
            getHelper().completeTaskComponent(getTask(), resolutions())
        );
    }

}
