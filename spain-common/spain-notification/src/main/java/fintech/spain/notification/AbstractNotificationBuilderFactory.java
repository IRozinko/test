package fintech.spain.notification;

import fintech.cms.CmsContextBuilder;
import fintech.cms.NotificationRenderer;
import fintech.crm.contacts.EmailContactService;
import fintech.crm.contacts.PhoneContactService;
import fintech.notification.NotificationService;

public abstract class AbstractNotificationBuilderFactory implements NotificationBuilderFactory {

    protected final NotificationRenderer notificationRenderer;
    protected final NotificationService notificationService;
    protected final CmsContextBuilder contextBuilder;
    private final EmailContactService emailContactService;
    private final PhoneContactService phoneContactService;

    protected AbstractNotificationBuilderFactory(NotificationRenderer notificationRenderer, NotificationService notificationService,
                                                 CmsContextBuilder contextBuilder, EmailContactService emailContactService, PhoneContactService phoneContactService) {
        this.notificationRenderer = notificationRenderer;
        this.notificationService = notificationService;
        this.contextBuilder = contextBuilder;
        this.emailContactService = emailContactService;
        this.phoneContactService = phoneContactService;
    }

    public NotificationBuilder newNotification(Long clientId, NotificationConfig config) {
        NotificationBuilder notificationBuilder = new NotificationBuilder(contextBuilder, notificationRenderer, notificationService);
        notificationBuilder.clientId(clientId);
        notificationBuilder.emailFrom(config.getEmailFrom());
        notificationBuilder.emailFromName(config.getEmailFromName());
        notificationBuilder.emailReplyTo(config.getEmailReplyTo());
        notificationBuilder.smsSenderId(config.getSmsSenderId());

        phoneContactService.findPrimaryPhone(clientId)
            .ifPresent(phoneContact -> notificationBuilder.smsTo(phoneContact.getPhoneNumber()));
        emailContactService.findPrimaryEmail(clientId)
            .ifPresent(emailContact -> notificationBuilder.emailTo(emailContact.getEmail()));
        return notificationBuilder;
    }
}
