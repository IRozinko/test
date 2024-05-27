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
public class LocPreOfferCallTask extends CommonTaskView {
    public final static String TYPE = "LocPreOfferCall";
    public final static String TYPE_RECENT_INSTANTOR = "LocPreOfferCall_RecentInstantor";

    /**
     * TODO:
     * "Resolutions:
     * No Answer (or Postpone?),
     * Client Interested,
     * Client Not Interested.
     * <p>
     * Comment section always displayed.
     * The resolution ""Client Not Interested"" should have ""Reasons"" dropdown (mandatory to complete):
     * ""Does not need money"",
     * ""Maybe will take later"",
     * ""Already has similar product""
     * Other.
     */
    private final DormantsLocFacadeApiClient apiClient;

    public LocPreOfferCallTask() {
        this.apiClient = ApiAccessor.gI().get(DormantsLocFacadeApiClient.class);
    }

    private void setTitle(BusinessObjectLayout layout, TaskRecord task) {
        switch (task.getTaskType()) {
            case TYPE:
                layout.setTitle(String.format("%s - %s", "LocPreOfferCall-InstantorNeeded", task.getStatus()));
                break;
            case TYPE_RECENT_INSTANTOR:
                layout.setTitle(String.format("%s - %s", "LocPreOfferCall", task.getStatus()));
                break;
        }
    }

    @Override
    public Component buildView(BusinessObjectLayout baseLayout) {
        setTitle(baseLayout, getTask());

        VerticalLayout layout = new VerticalLayout();
        baseLayout.addActionMenuItem("Resend Pre Offer Email", item -> resendPreOfferEmail(getTask()));
        baseLayout.addActionMenuItem("Resend Pre Offer SMS", item -> resendPreOfferSms(getTask()));

        layout.addComponent(getHelper().callClientComponent(getTask().getClientId()));
        layout.addComponent(getHelper().completeTaskComponent(getTask(), resolutions()));
        return layout;
    }

    private void resendPreOfferEmail(TaskRecord task) {
        SendCmsNotificationRequest request = new SendCmsNotificationRequest(task.getApplicationId());
        Call<Void> call = apiClient.resendPreOfferEmail(request);
        BackgroundOperations.callApi("Resending Pre Offer Email", call, t -> Notifications.trayNotification("Email sent"), Notifications::errorNotification);
    }

    private void resendPreOfferSms(TaskRecord task) {
        SendCmsNotificationRequest request = new SendCmsNotificationRequest(task.getApplicationId());
        Call<Void> call = apiClient.resendPreOfferSms(request);
        BackgroundOperations.callApi("Resending Pre Offer SMS", call, t -> Notifications.trayNotification("SMS sent"), Notifications::errorNotification);
    }
}
