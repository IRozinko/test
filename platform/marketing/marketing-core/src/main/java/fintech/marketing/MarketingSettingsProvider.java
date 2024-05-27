package fintech.marketing;

import fintech.spain.notification.NotificationConfig;

public interface MarketingSettingsProvider {

    NotificationConfig getMarketingNotificationConfig();

    void setMarketingNotificationConfig(NotificationConfig cfg);
}
