package fintech.spain.alfa.product.documents.impl;

import fintech.TimeMachine;
import fintech.Validate;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.documents.IdentityDocumentNumberUtils;
import fintech.spain.alfa.product.documents.*;
import fintech.spain.alfa.product.db.Entities;
import fintech.spain.alfa.product.db.IdentificationDocumentEntity;
import fintech.spain.alfa.product.db.IdentificationDocumentRepository;
import fintech.spain.alfa.product.documents.*;
import fintech.spain.alfa.product.documents.events.IdentificationDocumentSavedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static fintech.spain.alfa.product.db.Entities.identificationDocument;

@Slf4j
@Component
@Transactional
public class IdentificationDocumentsServiceBean implements IdentificationDocumentsService {

    @Autowired
    private IdentificationDocumentRepository identificationDocumentRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ClientService clientService;

    @Override
    public Long saveIdentificationDocument(SaveIdentificationDocumentCommand command) {
        validate(command);

        log.info("Saving identification document [{}]", command);

        IdentificationDocumentEntity documentEntity = new IdentificationDocumentEntity();
        documentEntity.setClientId(command.getClientId());
        documentEntity.setTaskId(command.getTaskId());
        documentEntity.setDocumentType(command.getDocumentType().name());
        documentEntity.setDocumentNumber(command.getDocumentNumber());
        documentEntity.setSurname1(command.getSurname1());
        documentEntity.setSurname2(command.getSurname2());
        documentEntity.setName(command.getName());
        documentEntity.setGender(command.getGender());
        documentEntity.setNationality(command.getNationality());
        documentEntity.setDateOfBirth(command.getDateOfBirth());
        documentEntity.setExpirationDate(command.getExpirationDate());
        documentEntity.setStreet(command.getStreet());
        documentEntity.setHouse(command.getHouse());
        documentEntity.setCity(command.getCity());
        documentEntity.setProvince(command.getProvince());
        documentEntity.setPlaceOfBirth(command.getPlaceOfBirth());
        documentEntity.setFrontFileId(command.getFrontFileId());
        documentEntity.setFrontFileName(command.getFrontFileName());
        documentEntity.setBackFileId(command.getBackFileId());
        documentEntity.setBackFileName(command.getBackFileName());
        documentEntity.setCustomerServiceAssessment(command.getCustomerServiceAssessment());

        identificationDocumentRepository.save(documentEntity);

        if (command.isNotifyOnSave()) {
            IdentificationDocumentSavedEvent event = new IdentificationDocumentSavedEvent().setClientId(documentEntity.getClientId());
            log.info("Publishing event [{}]", event);
            eventPublisher.publishEvent(event);
        }

        return documentEntity.getId();
    }

    private void validate(SaveIdentificationDocumentCommand command) {
        Validate.notNull(command.getClientId(), "Null client id");
        Validate.notNull(command.getFrontFileId(), "Null front file id");
        Validate.notEmpty(command.getFrontFileName(), "empty front file name");
        Validate.notNull(command.getDocumentType(), "Null type");
        Validate.notEmpty(command.getDocumentNumber(), "Null document number");
        Validate.notEmpty(command.getName(), "Empty name");
        Validate.notEmpty(command.getSurname1(), "Empty surname 1");
        Validate.notNull(command.getDateOfBirth(), "Null date of birth");
        Client client = clientService.get(command.getClientId());
        if (DocumentType.DNI.equals(command.getDocumentType()) || DocumentType.PASSPORT.equals(command.getDocumentType())) {
            Validate.notNull(command.getExpirationDate(), "Null expiration date");
        }
        Validate.isTrue(client.getGender().name().equalsIgnoreCase(command.getGender()), "Gender does not match");
        Validate.isTrue(client.getFirstName().equalsIgnoreCase(command.getName()), "First name does not match");
        Validate.isTrue(client.getLastName().equalsIgnoreCase(command.getSurname1()), "LastName does not match");
        Validate.isTrue(client.getDateOfBirth().equals(command.getDateOfBirth()), "Date of birth does not match");
        if (!DocumentType.PASSPORT.equals(command.getDocumentType())) {
            Validate.isTrue(IdentityDocumentNumberUtils.isValidDniOrNie(command.getDocumentNumber()), "Invalid DNI/NIE");
        }
    }

    @Override
    public void invalidateIdentificationDocument(InvalidateIdentificationDocument command) {
        log.info("Invalidating identification document [{}]", command);

        Validate.notNull(command.getIdentificationDocumentId(), "Null identification document id");

        IdentificationDocumentEntity entity = identificationDocumentRepository.findOne(command.getIdentificationDocumentId());

        entity.setValid(false);
        entity.setValidatedAt(TimeMachine.now());

        identificationDocumentRepository.save(entity);
    }

    @Override
    public void validateIdentificationDocument(ValidateIdentificationDocument command) {
        log.info("Validating identification document [{}]", command);

        Validate.notNull(command.getIdentificationDocumentId(), "Null identification document id");
        Validate.notNull(command.getClientId(), "Null client id");

        List<IdentificationDocumentEntity> entities = identificationDocumentRepository.findAll(
            Entities.identificationDocument.clientId.eq(command.getClientId()).and(
                Entities.identificationDocument.id.eq(command.getIdentificationDocumentId()).or(Entities.identificationDocument.isValid.isTrue())
            ));

        entities.stream()
            .filter(r -> r.isValid() && !r.getId().equals(command.getIdentificationDocumentId()))
            .forEach(r -> invalidateIdentificationDocument(new InvalidateIdentificationDocument().setIdentificationDocumentId(r.getId())));

        IdentificationDocumentEntity record = entities.stream()
            .filter(r -> r.getId().equals(command.getIdentificationDocumentId()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Identification document not found"));

        record.setValid(true);
        record.setValidatedAt(TimeMachine.now());

        identificationDocumentRepository.save(record);
    }

    @Override
    public Optional<IdentificationDocumentEntity> findLatestIdentificationDocument(long clientId) {
        return identificationDocumentRepository.findFirst(identificationDocument.clientId.eq(clientId), identificationDocument.id.desc());
    }
}
