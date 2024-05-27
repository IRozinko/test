package fintech.bo.spain.experian;

import fintech.bo.components.views.*;
import org.springframework.stereotype.Component;

@Component
public class ExperianCaisOperacionesListComponentProvider extends BoComponentProviderSupport {

    @Override
    public BoComponent newInstance() {
        return new ExperianCaisOperacionesListComponent();
    }

    @Override
    public BoComponentMetadata metadata() {
        return new BoComponentMetadata("Experian CAIS Operaciones")
            .withScope(StandardScopes.SCOPE_CLIENT)
            .withFeature(StandardFeatures.FEATURE_ADVANCED_DATA);
    }
}
