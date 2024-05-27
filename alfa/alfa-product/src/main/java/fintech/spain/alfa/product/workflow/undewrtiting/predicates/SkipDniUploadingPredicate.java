package fintech.spain.alfa.product.workflow.undewrtiting.predicates;

import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.settings.AlfaSettings;
import fintech.workflow.spi.ActivityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class SkipDniUploadingPredicate implements Predicate<ActivityContext> {

    @Autowired
    private LoanService loanService;

    @Autowired
    private SettingsService settingsService;

    @Override
    public boolean test(ActivityContext context) {
        long clientId = context.getClientId();
        // The document should be requested only to the clients that have never had a loan in Alfa before (but they could have multiple applications in the past).
        return !featureEnabled() || hasLoans(clientId);
    }

    private boolean featureEnabled() {
        return settingsService.getBoolean(AlfaSettings.ENABLE_DNI_UPLOADING);
    }

    private boolean hasLoans(long clientId) {
        return !(loanService.findLoans(LoanQuery.allLoans(clientId)).isEmpty());
    }
}
