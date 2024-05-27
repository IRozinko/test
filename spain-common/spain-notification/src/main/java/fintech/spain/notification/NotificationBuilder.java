package fintech.spain.notification;

import com.google.common.collect.ImmutableList;
import fintech.TimeMachine;
import fintech.cms.CmsContextBuilder;
import fintech.cms.CmsNotification;
import fintech.cms.NotificationRenderer;
import fintech.cms.spi.CmsItem;
import fintech.email.Email;
import fintech.notification.Notification;
import fintech.notification.NotificationService;
import fintech.sms.Sms;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Setter
@Accessors(fluent = true, chain = true)
public class NotificationBuilder {
    private String emailTo;
    private String emailFrom;
    private String emailFromName;
    private String emailReplyTo;
    private String emailSubject;
    private String emailBody;
    private List<Long> emailAttachmentFileIds = ImmutableList.of();

    private String smsTo;
    private String smsSenderId;
    private String smsText;
    private LocalDateTime sendAt = TimeMachine.now();

    private String cmsKey;

    private Long clientId;
    private Long loanId;
    private Long loanApplicationId;
    private Long debtId;
    private Long taskId;

    private final CmsContextBuilder contextBuilder;
    private final NotificationRenderer notificationRenderer;
    private final NotificationService notificationService;

    public NotificationBuilder(CmsContextBuilder contextBuilder, NotificationRenderer notificationRenderer, NotificationService notificationService) {
        this.contextBuilder = contextBuilder;
        this.notificationRenderer = notificationRenderer;
        this.notificationService = notificationService;
    }

    public NotificationBuilder renderAnonymous(String cmsKey, Map<String, Object> context) {
        cmsKey(cmsKey);
        return renderAnonymous(context);
    }

    public NotificationBuilder renderAnonymous(Map<String, Object> context) {
        Map<String, Object> notificationContext = contextBuilder.anonymousNotificationContext(context);
        render(notificationContext, contextBuilder.companyLocale(), (cmsKey != null));
        return this;
    }

    public NotificationBuilder render(String cmsKey, Map<String, Object> context) {
        cmsKey(cmsKey);
        return render(context);
    }

    public NotificationBuilder render(Map<String, Object> context) {
       return render(context, (cmsKey != null));
    }

    public NotificationBuilder render(Map<String, Object> context, boolean useTemplate) {
        Map<String, Object> notificationContext = contextBuilder.basicContext(clientId, context);
        render(notificationContext, contextBuilder.companyLocale(), useTemplate);
        return this;
    }

    public Long send() {
        return notificationService.send(build());
    }

    private void render(Map<String, Object> context, String locale, boolean useTemplate) {
        Optional<CmsNotification> cmsNotification = useTemplate ?
            notificationRenderer.render(cmsKey, context, locale) :
            notificationRenderer.render(cmsItem(), context, locale);

        if (!cmsNotification.isPresent()) {
            log.warn("Notification template [{}] not found", cmsKey);
            return;
        }

        cmsNotification.get().getEmail().ifPresent(e -> {
            emailSubject(e.getSubject());
            emailBody(e.getBody());
        });
        cmsNotification.get().getSms().ifPresent(s ->
            smsText(s.getText())
        );

    }

    private CmsItem cmsItem() {
        return new CmsItem()
            .setSmsTextTemplate(smsText)
            .setEmailSubjectTemplate(emailSubject)
            .setEmailBodyTemplate(emailBody);
    }

    private Notification build() {
        Notification notification = new Notification();
        notification.setLoanId(loanId);
        notification.setLoanApplicationId(loanApplicationId);
        notification.setDebtId(debtId);
        notification.setClientId(clientId);
        notification.setTaskId(taskId);
        notification.setCmsKey(cmsKey);

        if (!StringUtils.isBlank(emailTo) && !StringUtils.isBlank(emailSubject) && !StringUtils.isBlank(emailBody)) {
            Email email = new Email();
            email.setFrom(emailFrom);
            email.setFromName(emailFromName);
            email.setTo(emailTo);
            email.setSubject(emailSubject);
            email.setBody(emailBody);
            email.setReplyTo(emailReplyTo);
            email.setSendAt(sendAt);
            email.setAttachmentFileIds(emailAttachmentFileIds);
            notification.setEmail(email);
        }

        if (!StringUtils.isBlank(smsTo) && !StringUtils.isBlank(smsText)) {
            Sms sms = new Sms();
            sms.setSendAt(sendAt);
            sms.setTo(smsTo);
            sms.setText(smsText);
            sms.setSenderId(smsSenderId);
            notification.setSms(sms);
        }

        return notification;
    }
}
