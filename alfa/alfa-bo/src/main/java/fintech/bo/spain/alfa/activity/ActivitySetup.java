package fintech.bo.spain.alfa.activity;

import fintech.bo.api.client.CmsApiClient;
import fintech.bo.components.activity.ActivityComponents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivitySetup {

    @Autowired
    private ActivityComponents activityComponents;

    @Autowired
    private CmsApiClient cmsApiClient;

    public synchronized void init() {
        activityComponents.registerBulkAction("SendSms", () -> new SendSmsComponent("SendSms", cmsApiClient));
        activityComponents.registerBulkAction("SendEmail", () -> new SendEmailComponent("SendEmail", cmsApiClient));
    }
}
