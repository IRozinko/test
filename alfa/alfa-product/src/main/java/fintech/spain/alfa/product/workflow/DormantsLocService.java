package fintech.spain.alfa.product.workflow;

import fintech.Validate;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationType;
import fintech.spain.platform.web.SpecialLinkType;
import fintech.spain.alfa.product.cms.CmsSetup;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DormantsLocService {

    private final LoanApplicationService applicationService;
    private final AlfaNotificationBuilderFactory notificationFactory;
    private final AlfaCmsModels cmsModels;


    public DormantsLocService(
        LoanApplicationService applicationService,
        AlfaNotificationBuilderFactory notificationFactory,
        AlfaCmsModels cmsModels
    ) {
        this.applicationService = applicationService;
        this.notificationFactory = notificationFactory;
        this.cmsModels = cmsModels;
    }

    public void resendPreOfferEmail(long applicationId) {
        sendNotificationInternal(applicationId, CmsSetup.LOC_PRE_OFFER_EMAIL_RESEND);
    }

    public void resendPreOfferSms(long applicationId) {
        sendNotificationInternal(applicationId, CmsSetup.LOC_PRE_OFFER_SMS_RESEND);
    }

    public void resendOfferEmail(long applicationId) {
        sendNotificationInternal(applicationId, CmsSetup.LOC_LOAN_OFFER_EMAIL_RESEND);
    }

    public void resendOfferSms(long applicationId) {
        sendNotificationInternal(applicationId, CmsSetup.LOC_LOAN_OFFER_SMS_RESEND);
    }

    private void sendNotificationInternal(Long applicationId, String cmsKey) {
        log.info("Sending {}, for application [{}]", cmsKey, applicationId);
        LoanApplication application = applicationService.get(applicationId);
        Validate.isTrue(application.getType() == LoanApplicationType.LINE_OF_CREDIT, "Only new loan application type supported");

        notificationFactory.fromCustomerService(application.getClientId())
            .loanApplicationId(applicationId)
            .render(cmsKey, cmsModels.specialLink(applicationId, SpecialLinkType.LOC_SPECIAL_OFFER))
            .send();
    }

}
