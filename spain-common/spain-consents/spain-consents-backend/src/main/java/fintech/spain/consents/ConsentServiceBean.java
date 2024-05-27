package fintech.spain.consents;

import fintech.TimeMachine;
import fintech.Validate;
import fintech.spain.consents.db.ConsentEntity;
import fintech.spain.consents.db.ConsentRepository;
import fintech.spain.consents.db.TermRepository;
import fintech.spain.consents.db.TermsEntity;
import fintech.spain.consents.model.Consent;
import fintech.spain.consents.model.Terms;
import fintech.spain.consents.model.UpdateConsentCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static fintech.spain.consents.db.Entities.consent;
import static fintech.spain.consents.db.Entities.terms;

@Service
@Transactional
public class ConsentServiceBean implements ConsentService {

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private ConsentRepository consentRepository;


    @Override
    public Terms saveTerms(String name, String version, String text) {
        Validate.isTrue(!termRepository.exists(terms.name.eq(name).and(terms.version.eq(version))), "This version of terms already exists");
        TermsEntity entity = new TermsEntity();
        entity.setName(name);
        entity.setText(text);
        entity.setVersion(version);
        entity.setChangedAt(TimeMachine.now());
        entity.setText(text);
        TermsEntity savedTerms = termRepository.save(entity);
        return savedTerms.toValueObject();
    }

    public List<Consent> getClientConsents(Long clientId) {
        return consentRepository.findAll(consent.clientId.eq(clientId)).stream()
            .map(ConsentEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Override
    public void updateConsent(UpdateConsentCommand command) {
        ConsentEntity consentEntity = new ConsentEntity();
        consentEntity.setClientId(command.getClientId());
        consentEntity.setName(command.getName());
        consentEntity.setAccepted(command.isAccepted());
        consentEntity.setSource(command.getSource());
        consentEntity.setChangedAt(TimeMachine.now());

        if (command.getVersion() == null) {
            String version = getLatestTermVersion(command.getName());
            consentEntity.setVersion(version);
        } else {
            consentEntity.setVersion(command.getVersion());
        }

        consentRepository.save(consentEntity);
    }

    private String getLatestTermVersion(String name) {
        return termRepository.findFirst(terms.name.eq(name), terms.changedAt.desc())
            .map(TermsEntity::getVersion)
            .orElseThrow(() -> new RuntimeException("Can't find terms for consent " + name));
    }
}
