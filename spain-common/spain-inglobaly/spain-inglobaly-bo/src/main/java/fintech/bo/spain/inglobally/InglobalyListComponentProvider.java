package fintech.bo.spain.inglobally;

import fintech.bo.components.views.*;
import org.springframework.stereotype.Component;

@Component
public class InglobalyListComponentProvider extends BoComponentProviderSupport {

    @Override
    public BoComponent newInstance() {
        return new InglobalyListComponent();
    }

    @Override
    public BoComponentMetadata metadata() {
        return new BoComponentMetadata("Inglobaly")
            .withScope(StandardScopes.SCOPE_CLIENT)
            .withFeature(StandardFeatures.FEATURE_ADVANCED_DATA);
    }
}
