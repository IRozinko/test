package fintech.marketing.bo;

import com.vaadin.data.HasValue;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import fintech.TimeMachine;
import fintech.bo.api.model.marketing.SaveMarketingCampaignRequest;
import fintech.bo.components.Formats;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.marketing.tables.records.MarketingCampaignRecord;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MultipartBody;
import retrofit2.Call;

import static java.lang.Boolean.TRUE;

@Slf4j
public class ResendMarketingCampaignDialog extends EditMarketingCampaignDialog {

    public ResendMarketingCampaignDialog(MarketingCampaignRecord item) {
        super(item);
        setCaption("Resend campaign");
        getBinder().getBean().setAutomated(false);
        getBinder().getBean().setScheduleType(null);
    }

    @Override
    public Component schedulingComponent() {
        VerticalLayout root = new VerticalLayout();
        root.setSpacing(false);
        root.setMargin(new MarginInfo(true, true, true, true));

        RadioButtonGroup<Boolean> group = new RadioButtonGroup<>();
        group.setItems(true, false);
        group.setItemCaptionGenerator((ItemCaptionGenerator<Boolean>) item -> TRUE.equals(item) ? "Trigger now" : "Schedule");

        root.addComponent(group);

        VerticalLayout scheduleLayout = new VerticalLayout();
        scheduleLayout.setSpacing(false);
        scheduleLayout.setMargin(new MarginInfo(true, true, true, true));
        DateTimeField nextActionAt = new DateTimeField();
        nextActionAt.setValue(TimeMachine.now());
        getBinder().forField(nextActionAt)
            .asRequired()
            .bind(SaveMarketingCampaignRequest::getTriggerDate, SaveMarketingCampaignRequest::setTriggerDate);
        nextActionAt.setDateFormat(Formats.DATE_TIME_FORMAT);
        nextActionAt.setTextFieldEnabled(false);
        scheduleLayout.addComponent(new HorizontalLayout(new Label("Date"), nextActionAt));
        group.addValueChangeListener((HasValue.ValueChangeListener<Boolean>) event -> {
            for (int i = 0; i < scheduleLayout.getComponentCount(); i++) {
                scheduleLayout.getComponent(i).setEnabled(!event.getValue());
            }
        });
        getBinder().forField(group)
            .bind(SaveMarketingCampaignRequest::getTriggerNow, SaveMarketingCampaignRequest::setTriggerNow);
        root.addComponent(scheduleLayout);
        return root;
    }

    @Override
    protected void processRequest(MultipartBody body) {
        Call<Void> call = getApiClient().resendMarketingCampaign(body);
        BackgroundOperations.callApi("Saving campaign", call, v -> {
            Notifications.trayNotification("Saved");
            close();
        }, Notifications::errorNotification);

    }

}
