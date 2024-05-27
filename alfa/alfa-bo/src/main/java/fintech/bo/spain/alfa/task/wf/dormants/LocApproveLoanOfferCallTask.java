package fintech.bo.spain.alfa.task.wf.dormants;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.task.CommonTaskView;
import fintech.bo.db.jooq.task.tables.records.TaskRecord;
import fintech.bo.spain.alfa.api.DormantsLocFacadeApiClient;
import fintech.spain.alfa.bo.model.SendCmsNotificationRequest;
import retrofit2.Call;

@SuppressWarnings("Duplicates")
public class LocApproveLoanOfferCallTask extends CommonTaskView {

    private final DormantsLocFacadeApiClient apiClient;

    public LocApproveLoanOfferCallTask() {
        this.apiClient = ApiAccessor.gI().get(DormantsLocFacadeApiClient.class);
    }

    @Override
    public Component buildView(BusinessObjectLayout baseLayout) {
        VerticalLayout layout = new VerticalLayout();

        baseLayout.addActionMenuItem("Resend Offer Email", item -> resendOfferEmail(getTask()));
        baseLayout.addActionMenuItem("Resend Offer SMS", item -> resendOfferSms(getTask()));

        layout.addComponent(getHelper().callClientComponent(getTask().getClientId()));
        layout.addComponent(getHelper().completeTaskComponent(getTask(), resolutions()));
        return layout;
    }

    private void resendOfferEmail(TaskRecord task) {
        SendCmsNotificationRequest request = new SendCmsNotificationRequest(task.getApplicationId());
        Call<Void> call = apiClient.resendOfferEmail(request);
        BackgroundOperations.callApi("Resending Offer Email", call, t -> Notifications.trayNotification("Email sent"), Notifications::errorNotification);
    }

    private void resendOfferSms(TaskRecord task) {
        SendCmsNotificationRequest request = new SendCmsNotificationRequest(task.getApplicationId());
        Call<Void> call = apiClient.resendOfferSms(request);
        BackgroundOperations.callApi("Resending Offer SMS", call, t -> Notifications.trayNotification("SMS sent"), Notifications::errorNotification);
    }

}
