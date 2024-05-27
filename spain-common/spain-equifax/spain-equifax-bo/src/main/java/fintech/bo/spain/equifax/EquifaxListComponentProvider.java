package fintech.bo.spain.equifax;

import fintech.bo.components.views.*;
import org.springframework.stereotype.Component;

@Component
public class EquifaxListComponentProvider extends BoComponentProviderSupport {

    @Override
    public BoComponent newInstance() {
        return new EquifaxListComponent();
    }

    @Override
    public BoComponentMetadata metadata() {
        return new BoComponentMetadata("Equifax")
            .withScope(StandardScopes.SCOPE_CLIENT)
            .withFeature(StandardFeatures.FEATURE_ADVANCED_DATA);
    }
}
