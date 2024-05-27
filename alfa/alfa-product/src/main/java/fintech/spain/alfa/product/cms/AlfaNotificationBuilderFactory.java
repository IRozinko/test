package fintech.spain.alfa.product.cms;

import fintech.cms.NotificationRenderer;
import fintech.crm.contacts.EmailContactService;
import fintech.crm.contacts.PhoneContactService;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.notification.NotificationService;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.settings.AlfaSettings;
import fintech.spain.notification.AbstractNotificationBuilderFactory;
import fintech.spain.notification.NotificationBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AlfaNotificationBuilderFactory extends AbstractNotificationBuilderFactory {

    private final LoanService loanService;
    private final SettingsService settingsService;

    @Autowired
    public AlfaNotificationBuilderFactory(LoanService loanService, SettingsService settingsService,
                                          NotificationRenderer notificationRenderer, NotificationService notificationService,
                                          AlfaCmsContextBuilder contextBuilder, EmailContactService emailContactService,
                                          PhoneContactService phoneContactService) {
        super(notificationRenderer, notificationService, contextBuilder, emailContactService, phoneContactService);
        this.loanService = loanService;
        this.settingsService = settingsService;
    }

    public NotificationBuilder fromLoan(Long loanId) {
        Loan loan = loanService.getLoan(loanId);
        Long clientId = loan.getClientId();

        if (loan.getOverdueDays() <= 0) {
            return fromCustomerService(clientId);
        } else {
            return fromDebtCollection(clientId);
        }
    }

    public NotificationBuilder fromCustomerService(Long clientId) {
        AlfaSettings.NotificationSettings settings = settingsService.getJson(AlfaSettings.NOTIFICATION_SETTINGS, AlfaSettings.NotificationSettings.class);
        return newNotification(clientId, settings.getCustomerService());
    }

    public NotificationBuilder fromDebtCollection(Long clientId) {
        AlfaSettings.NotificationSettings settings = settingsService.getJson(AlfaSettings.NOTIFICATION_SETTINGS, AlfaSettings.NotificationSettings.class);
        return newNotification(clientId, settings.getDebtCollection());
    }

    public NotificationBuilder fromPreLegal(Long clientId) {
        AlfaSettings.NotificationSettings settings = settingsService.getJson(AlfaSettings.NOTIFICATION_SETTINGS, AlfaSettings.NotificationSettings.class);
        return newNotification(clientId, settings.getPreLegal());
    }

    public NotificationBuilder fromLegal(Long clientId) {
        AlfaSettings.NotificationSettings settings = settingsService.getJson(AlfaSettings.NOTIFICATION_SETTINGS, AlfaSettings.NotificationSettings.class);
        return newNotification(clientId, settings.getLegal());
    }

    public NotificationBuilder fromExtraLegal(Long clientId) {
        AlfaSettings.NotificationSettings settings = settingsService.getJson(AlfaSettings.NOTIFICATION_SETTINGS, AlfaSettings.NotificationSettings.class);
        return newNotification(clientId, settings.getExtraLegal());
    }

    public NotificationBuilder contactMe() {
        AlfaSettings.NotificationSettings settings = settingsService.getJson(AlfaSettings.NOTIFICATION_SETTINGS, AlfaSettings.NotificationSettings.class);
        return new NotificationBuilder(contextBuilder, notificationRenderer, notificationService)
            .emailTo(settings.getCustomerService().getEmailFrom());
    }

}
