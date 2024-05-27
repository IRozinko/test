package fintech.bo.spain.alfa.dc;

import fintech.bo.api.client.CalendarApiClient;
import fintech.bo.api.client.CmsApiClient;
import fintech.bo.components.dc.ChangePortfolioComponent;
import fintech.bo.components.dc.DcComponents;
import fintech.bo.components.dc.PromisedToPayComponent;
import fintech.bo.components.dc.SendEmailComponent;
import fintech.bo.components.dc.SendSmsComponent;
import fintech.bo.spain.alfa.dc.rescheduling.DebtManualReschedulingComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DcSetup {

    @Autowired
    private DcComponents dcComponents;

    @Autowired
    private CalendarApiClient calendarApiClient;

    @Autowired
    private CmsApiClient cmsApiClient;

    public synchronized void init() {
        dcComponents.registerBulkAction("PromiseToPay", () -> new PromisedToPayComponent(calendarApiClient));
        dcComponents.registerBulkAction("ChangePortfolio", ChangePortfolioComponent::new);
        dcComponents.registerBulkAction("SendEmail", () -> new SendEmailComponent(cmsApiClient, true));
        dcComponents.registerBulkAction("SendSms", () -> new SendSmsComponent(cmsApiClient));
        dcComponents.registerBulkAction("LogActivity", EmptyBulkActionComponent::new);
        dcComponents.registerBulkAction("RemoveNextAction", EmptyBulkActionComponent::new);
        dcComponents.registerBulkAction("Reschedule", DcReschedulingComponent::new);
        dcComponents.registerBulkAction("ManualReschedule", DebtManualReschedulingComponent::new);
        dcComponents.registerBulkAction("ExhaustPopup", EmptyBulkActionComponent::new);
    }

}
