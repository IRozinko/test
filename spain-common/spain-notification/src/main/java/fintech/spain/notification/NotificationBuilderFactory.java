package fintech.spain.notification;

public interface NotificationBuilderFactory {

    NotificationBuilder newNotification(Long clientId, NotificationConfig config);

}
