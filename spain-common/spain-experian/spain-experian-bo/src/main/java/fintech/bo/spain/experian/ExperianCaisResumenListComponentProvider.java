package fintech.bo.spain.experian;

import fintech.bo.components.views.*;
import org.springframework.stereotype.Component;

@Component
public class ExperianCaisResumenListComponentProvider extends BoComponentProviderSupport {

    @Override
    public BoComponent newInstance() {
        return new ExperianCaisResumenListComponent();
    }

    @Override
    public BoComponentMetadata metadata() {
        return new BoComponentMetadata("Experian CAIS Resumen")
            .withScope(StandardScopes.SCOPE_CLIENT)
            .withFeature(StandardFeatures.FEATURE_ADVANCED_DATA);
    }
}
