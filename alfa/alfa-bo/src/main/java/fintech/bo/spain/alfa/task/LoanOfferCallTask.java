package fintech.bo.spain.alfa.task;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.task.CommonTaskView;
import fintech.bo.db.jooq.task.tables.records.TaskRecord;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.spain.alfa.bo.model.SendCmsNotificationRequest;
import retrofit2.Call;

public class LoanOfferCallTask extends CommonTaskView {

    private final AlfaApiClient alfaApiClient;

    public LoanOfferCallTask() {
        this.alfaApiClient = ApiAccessor.gI().get(AlfaApiClient.class);
    }

    @Override
    public Component buildView(BusinessObjectLayout baseLayout) {
        VerticalLayout layout = new VerticalLayout();
        baseLayout.addActionMenuItem("Resend offer SMS", item -> sendOfferSms(getTask()));
        layout.addComponent(getHelper().callClientComponent(getTask().getClientId()));
        layout.addComponent(getHelper().completeTaskComponent(getTask(), resolutions()));
        return layout;
    }

    private void sendOfferSms(TaskRecord task) {
        SendCmsNotificationRequest request = new SendCmsNotificationRequest(task.getApplicationId());
        Call<Void> call = alfaApiClient.sendOfferSms(request);
        BackgroundOperations.callApi("Sending offer SMS", call, t -> Notifications.trayNotification("SMS sent"), Notifications::errorNotification);
    }


}
