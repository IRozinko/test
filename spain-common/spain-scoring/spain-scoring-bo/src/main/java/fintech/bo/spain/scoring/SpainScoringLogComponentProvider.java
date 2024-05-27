package fintech.bo.spain.scoring;

import fintech.bo.components.views.*;
import org.springframework.stereotype.Component;

@Component
public class SpainScoringLogComponentProvider extends BoComponentProviderSupport {

    @Override
    public BoComponent newInstance() {
        return new SpainScoringLogComponent();
    }

    @Override
    public BoComponentMetadata metadata() {
        return new BoComponentMetadata("Scoring")
            .withScope(StandardScopes.SCOPE_CLIENT)
            .withFeature(StandardFeatures.FEATURE_ADVANCED_DATA);
    }
}
