package fintech.dc.spi;

import fintech.dc.spi.handlers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DcDefaults {

    @Autowired
    private DcRegistry dcRegistry;

    public void init() {
        dcRegistry.registerConditionHandler("Dpd", DpdCondition.class);
        dcRegistry.registerConditionHandler("PromiseDpd", PromiseDpdCondition.class);
        dcRegistry.registerConditionHandler("CurrentStatus", CurrentStatusCondition.class);
        dcRegistry.registerConditionHandler("CurrentPortfolio", CurrentPortfolioCondition.class);
        dcRegistry.registerConditionHandler("NotCurrentStatus", NotCurrentStatusCondition.class);
        dcRegistry.registerConditionHandler("TotalDue", TotalDueCondition.class);
        dcRegistry.registerConditionHandler("TotalOutstanding", TotalOutstandingCondition.class);
        dcRegistry.registerConditionHandler("LoanStatusDetail", LoanStatusDetailCondition.class);
        dcRegistry.registerConditionHandler("LoanStatusDetails", LoanStatusDetailsCondition.class);
        dcRegistry.registerConditionHandler("TriggerFrequency", TriggerFrequencyCondition.class);
        dcRegistry.registerConditionHandler("CurrentTime", CurrentTimeCondition.class);

        dcRegistry.registerActionHandler("ChangePortfolio", ChangePortfolioAction.class);
        dcRegistry.registerActionHandler("ChangeStatus", ChangeStatusAction.class);
        dcRegistry.registerActionHandler("SetNextAction", SetNextActionAction.class);
        dcRegistry.registerActionHandler("RemoveNextAction", RemoveNextActionAction.class);
        dcRegistry.registerActionHandler("Noop", NoopAction.class);

        dcRegistry.registerBulkActionHandler("PromiseToPay", PromiseToPayBulkAction.class);
        dcRegistry.registerBulkActionHandler("ChangePortfolio", ChangePortfolioBulkAction.class);
        dcRegistry.registerBulkActionHandler("RemoveNextAction", RemoveNextActionBulkAction.class);
    }
}
