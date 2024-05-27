package fintech.spain.consents;

import fintech.spain.consents.model.Consent;
import fintech.spain.consents.model.Terms;
import fintech.spain.consents.model.UpdateConsentCommand;

import java.util.List;

public interface ConsentService {

    Terms saveTerms(String name, String version, String text);

    List<Consent> getClientConsents(Long clientId);

    void updateConsent(UpdateConsentCommand command);

}
