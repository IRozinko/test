package fintech.bo.spain.alfa.loan;

import fintech.bo.components.views.BoComponent;
import fintech.bo.components.views.BoComponentMetadata;
import fintech.bo.components.views.BoComponentProviderSupport;
import fintech.bo.components.views.StandardScopes;
import org.springframework.stereotype.Component;

@Component
public class PaymentSimulationComponentProvider extends BoComponentProviderSupport {

    @Override
    public BoComponent newInstance() {
        return new PaymentSimulationComponent();
    }

    @Override
    public BoComponentMetadata metadata() {
        return new BoComponentMetadata("Payment simulation")
            .withScope(StandardScopes.SCOPE_LOAN);
    }

}
