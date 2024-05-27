package fintech.spain.alfa.product.marketing;

import fintech.JsonUtils;
import fintech.marketing.MarketingSettingsProvider;
import fintech.settings.SettingsService;
import fintech.settings.commands.UpdatePropertyCommand;
import fintech.spain.alfa.product.settings.AlfaSettings;
import fintech.spain.notification.NotificationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class AlfaMarketingSettingsProvider implements MarketingSettingsProvider {

    @Autowired
    private SettingsService settings;

    @Override
    public NotificationConfig getMarketingNotificationConfig() {
        return settings.getJson(AlfaSettings.NOTIFICATION_SETTINGS, AlfaSettings.NotificationSettings.class).getMarketingService();
    }

    @Override
    public void setMarketingNotificationConfig(NotificationConfig marketingSettings) {
        AlfaSettings.NotificationSettings notificationSettings = settings.getJson(AlfaSettings.NOTIFICATION_SETTINGS, AlfaSettings.NotificationSettings.class);
        NotificationConfig marketingConfig = new NotificationConfig();
        marketingConfig.setEmailReplyTo(marketingSettings.getEmailReplyTo());
        marketingConfig.setEmailFrom(marketingSettings.getEmailFrom());
        marketingConfig.setEmailFromName(marketingSettings.getEmailFromName());
        marketingConfig.setSmsSenderId(marketingSettings.getSmsSenderId());
        notificationSettings.setMarketingService(marketingConfig);
        UpdatePropertyCommand command = new UpdatePropertyCommand();
        command.setName(AlfaSettings.NOTIFICATION_SETTINGS);
        command.setTextValue(JsonUtils.writeValueAsString(notificationSettings));
        settings.update(command);
    }
}
