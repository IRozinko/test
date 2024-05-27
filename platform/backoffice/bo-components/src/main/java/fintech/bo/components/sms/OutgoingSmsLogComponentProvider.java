package fintech.bo.components.sms;

import fintech.bo.components.views.BoComponent;
import fintech.bo.components.views.BoComponentMetadata;
import fintech.bo.components.views.BoComponentProviderSupport;
import fintech.bo.components.views.StandardFeatures;
import fintech.bo.components.views.StandardScopes;
import org.springframework.stereotype.Component;

@Component
public class OutgoingSmsLogComponentProvider extends BoComponentProviderSupport {

    @Override
    public BoComponent newInstance() {
        return new OutgoingSmsLogComponent();
    }

    @Override
    public BoComponentMetadata metadata() {
        return new BoComponentMetadata("Outgoing SMS")
            .withScope(StandardScopes.SCOPE_CLIENT)
            .withFeature(StandardFeatures.FEATURE_ADVANCED_DATA);
    }
}
