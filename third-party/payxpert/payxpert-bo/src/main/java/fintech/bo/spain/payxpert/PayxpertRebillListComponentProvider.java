package fintech.bo.spain.payxpert;

import fintech.bo.components.views.BoComponent;
import fintech.bo.components.views.BoComponentMetadata;
import fintech.bo.components.views.BoComponentProviderSupport;
import fintech.bo.components.views.StandardFeatures;
import fintech.bo.components.views.StandardScopes;
import org.springframework.stereotype.Component;

@Component
public class PayxpertRebillListComponentProvider extends BoComponentProviderSupport {

    @Override
    public BoComponent newInstance() {
        return new PayxpertRebillListComponent();
    }

    @Override
    public BoComponentMetadata metadata() {
        return new BoComponentMetadata("PayXpert Rebills")
            .withScope(StandardScopes.SCOPE_CLIENT)
            .withFeature(StandardFeatures.FEATURE_ADVANCED_DATA);
    }
}
