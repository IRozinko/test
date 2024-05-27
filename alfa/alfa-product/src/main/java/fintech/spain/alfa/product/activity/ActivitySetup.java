package fintech.spain.alfa.product.activity;

import fintech.activity.spi.ActivityRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivitySetup {

    @Autowired
    private ActivityRegistry activityRegistry;

    public void setUp() {
        activityRegistry.registerBulkActionHandler("SendSms", ActivitySendSmsBulkAction.class);
        activityRegistry.registerBulkActionHandler("SendEmail", ActivitySendEmailBulkAction.class);
    }
}
