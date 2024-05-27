package fintech.marketing.bo;

import com.vaadin.data.Binder;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import fintech.bo.api.model.marketing.MarketingSettings;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import lombok.SneakyThrows;

public class EditSettingsDialog extends ActionDialog {

    private final MarketingApiClient marketingApiClient;
    private final Binder<MarketingSettings> binder;

    @SneakyThrows
    public EditSettingsDialog(MarketingApiClient marketingApiClient) {
        super("Marketing settings", "Save");
        this.marketingApiClient = marketingApiClient;
        binder = new Binder<>();
        binder.setBean(marketingApiClient.getSettings().execute().body());
        setDialogContent(editor());
        setWidth(350, Unit.PIXELS);
        setHeight(300, Unit.PIXELS);
    }

    private Component editor() {
        FormLayout form = new FormLayout();
        form.setMargin(true);

        TextField emailFromAddress = new TextField("Email from address");
        binder.forField(emailFromAddress)
            .asRequired()
            .bind(MarketingSettings::getEmailFrom, MarketingSettings::setEmailFrom);
        form.addComponent(emailFromAddress);

        TextField emailFromName = new TextField("Email from name");
        binder.forField(emailFromName)
            .asRequired()
            .bind(MarketingSettings::getEmailFromName, MarketingSettings::setEmailFromName);
        form.addComponent(emailFromName);

        TextField emailReplyTo = new TextField("Email reply to");
        binder.forField(emailReplyTo)
            .asRequired()
            .bind(MarketingSettings::getEmailReplyTo, MarketingSettings::setEmailReplyTo);
        form.addComponent(emailReplyTo);

        TextField smsSenderName = new TextField("SMS sender name");
        binder.forField(smsSenderName)
            .asRequired()
            .bind(MarketingSettings::getSmsSenderId, MarketingSettings::setSmsSenderId);
        form.addComponent(smsSenderName);

        return form;
    }

    @Override
    protected void executeAction() {
        if (binder.validate().isOk()) {
            BackgroundOperations.callApi("Saving settings", marketingApiClient.saveSettings(binder.getBean()), t -> {
                Notifications.trayNotification("Settings saved");
                close();
            }, Notifications::errorNotification);
        }
    }
}
