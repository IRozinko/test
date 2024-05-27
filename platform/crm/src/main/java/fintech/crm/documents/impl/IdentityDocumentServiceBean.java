package fintech.crm.documents.impl;

import com.querydsl.core.types.Predicate;
import fintech.Validate;
import fintech.crm.client.db.ClientRepository;
import fintech.crm.country.db.CountryEntity;
import fintech.crm.country.db.CountryRepository;
import fintech.crm.country.impl.CountryNotValidException;
import fintech.crm.documents.AddIdentityDocumentCommand;
import fintech.crm.documents.ClientPrimaryIdentityDocumentUpdatedEvent;
import fintech.crm.documents.DuplicateDocumentNumberException;
import fintech.crm.documents.IdentityDocument;
import fintech.crm.documents.IdentityDocumentService;
import fintech.crm.documents.db.IdentityDocumentEntity;
import fintech.crm.documents.db.IdentityDocumentRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.crm.db.Entities.identityDocument;

@Component
class IdentityDocumentServiceBean implements IdentityDocumentService {

    private final IdentityDocumentRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final ClientRepository clientRepository;
    private final CountryRepository countryRepository;

    @Autowired
    public IdentityDocumentServiceBean(IdentityDocumentRepository repository, ApplicationEventPublisher eventPublisher,
                                       ClientRepository clientRepository, CountryRepository countryRepository) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.clientRepository = clientRepository;
        this.countryRepository = countryRepository;
    }

    @Override
    @Transactional
    public Long addDocument(AddIdentityDocumentCommand command) throws DuplicateDocumentNumberException {
        Validate.notBlank(command.getNumber(), "Empty document number");
        String number = normalize(command.getNumber());
        Optional<IdentityDocumentEntity> existingIdentityDocumentEntity = findExistingIdentityDocument(command.getClientId(), number, command.getType());

        if (existingIdentityDocumentEntity.isPresent()) {
            return existingIdentityDocumentEntity.get().getId();
        }

        IdentityDocumentEntity entity = new IdentityDocumentEntity();
        entity.setClient(clientRepository.getRequired(command.getClientId()));
        entity.setType(command.getType());
        entity.setNumber(number);
        entity.setNationality(findCountryOfNationality(command.getCountryCodeOfNationality()));
        return repository.saveAndFlush(entity).getId();
    }

    @Transactional
    @Override
    public void makeDocumentPrimary(Long documentId) throws DuplicateDocumentNumberException {
        IdentityDocumentEntity document = repository.getRequired(documentId);
        if (!isDocumentNumberAvailableForClient(document.getClient().getId(), document.getNumber(), document.getType())) {
            throw new DuplicateDocumentNumberException("Document number already in use");
        }
        Predicate clientPrimaryDocuments = identityDocument.client.id.eq(document.getClient().getId()).and(identityDocument.primary.isTrue());
        repository.findAll(clientPrimaryDocuments).forEach(entity -> entity.setPrimary(false));

        document.setPrimary(true);
        document.getClient().setDocumentNumber(document.getNumber());
        eventPublisher.publishEvent(new ClientPrimaryIdentityDocumentUpdatedEvent(document.toValueObject()));
    }

    @Override
    public Optional<IdentityDocument> findPrimaryDocument(Long clientId, String type) {
        return repository.getOptional(identityDocument.client.id.eq(clientId)
            .and(identityDocument.type.eq(type))
            .and(identityDocument.primary.isTrue()))
            .map(IdentityDocumentEntity::toValueObject);
    }

    @Override
    public List<IdentityDocument> findPrimaryDocuments(Long clientId) {
        return repository.findAll(identityDocument.client.id.eq(clientId)
            .and(identityDocument.primary.isTrue()))
            .stream()
            .map(IdentityDocumentEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<IdentityDocument> findByNumber(String documentNumber, String type, boolean primary) {
        return repository.findAll(identityDocument.number.eq(normalize(documentNumber))
            .and(identityDocument.type.eq(type))
            .and(identityDocument.primary.eq(primary))
            .and(identityDocument.client.deleted.isFalse()))
            .stream()
            .map(IdentityDocumentEntity::toValueObject)
            .findFirst();
    }

    @Override
    public boolean isDocumentNumberAvailable(String documentNumber, String documentType) {
        return !repository.exists(
            identityDocument.number.eq(normalize(documentNumber))
                .and(identityDocument.type.eq(documentType))
                .and(identityDocument.primary.isTrue())
                .and(identityDocument.client.deleted.isFalse()));
    }

    private CountryEntity findCountryOfNationality(String countryCodeOfNationality) {
        if (StringUtils.isNotBlank(countryCodeOfNationality)) {
            return countryRepository.findByCodeIgnoreCase(countryCodeOfNationality)
                .orElseThrow(() -> new CountryNotValidException("Country code " + countryCodeOfNationality + " not valid for identity document nationality"));
        }
        return null;
    }

    private String normalize(String number) {
        return StringUtils.upperCase(StringUtils.trim(number));
    }

    private Optional<IdentityDocumentEntity> findExistingIdentityDocument(Long clientId, String documentNumber, String documentType) {
        return repository.getOptional(
            identityDocument.client.id.eq(clientId)
                .and(identityDocument.number.eq(normalize(documentNumber)))
                .and(identityDocument.type.eq(documentType)));
    }

    private boolean isDocumentNumberAvailableForClient(Long clientId, String documentNumber, String documentType) {
        return !repository.exists(
            identityDocument.number.eq(normalize(documentNumber))
                .and(identityDocument.type.eq(documentType))
                .and(identityDocument.primary.isTrue())
                .and(identityDocument.client.id.ne(clientId))
                .and(identityDocument.client.deleted.isFalse()));
    }
}
