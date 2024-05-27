package fintech.crm.contacts.impl;

import fintech.TimeMachine;
import fintech.crm.client.db.ClientEntity;
import fintech.crm.client.db.ClientRepository;
import fintech.crm.contacts.*;
import fintech.crm.contacts.db.EmailContactEntity;
import fintech.crm.contacts.db.EmailContactRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.crm.db.Entities.emailContact;
import static java.lang.String.format;

@Slf4j
@Component
class EmailContactServiceBean implements EmailContactService {

    private final EmailContactRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final ClientRepository clientRepository;

    @Autowired
    public EmailContactServiceBean(EmailContactRepository repository, ApplicationEventPublisher eventPublisher, ClientRepository clientRepository) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional
    public Long addEmailContact(AddEmailContactCommand command) {
        Optional<EmailContactEntity> existingEmailContactEntity = findExistingEmailContact(command.getClientId(), command.getEmail());

        if (existingEmailContactEntity.isPresent()) {
            return existingEmailContactEntity.get().getId();
        }

        ClientEntity client = clientRepository.getRequired(command.getClientId());

        EmailContactEntity entity = new EmailContactEntity();
        entity.setEmail(command.getEmail().trim());
        entity.setClient(client);

        Long id = repository.saveAndFlush(entity).getId();
        client.addEmail(entity);
        return id;
    }

    @Override
    @Transactional
    public List<EmailContact> findAllEmailContacts(Long clientId) {
        return repository.findAll(emailContact.client.id.eq(clientId))
            .stream().map(EmailContactEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<EmailContact> findPrimaryEmail(Long clientId) {
        return repository.getOptional(emailContact.client.id.eq(clientId).and(emailContact.primary.isTrue()))
            .map(EmailContactEntity::toValueObject);
    }

    @Override
    @Transactional
    public void makeEmailPrimary(Long emailContactId) throws DuplicatePrimaryEmailException {
        EmailContactEntity entity = repository.getRequired(emailContactId);
        if (!isEmailAvailableForClient(entity.getClient().getId(), entity.getEmail())) {
            throw new DuplicatePrimaryEmailException(format("Email already in use: %s", entity.getEmail()));
        }
        repository.findAll(emailContact.client.id.eq(entity.getClient().getId())).forEach(emailContact -> {
            emailContact.setPrimary(false);
        });
        entity.setPrimary(true);
        eventPublisher.publishEvent(new ClientPrimaryEmailUpdatedEvent(entity.toValueObject()));
    }

    @Override
    public boolean isEmailAvailableForClient(Long clientId, String email) {
        return !repository.exists(
            emailContact.email.eq(email)
                .and(emailContact.client.id.ne(clientId)
                    .and(emailContact.client.deleted.isFalse())));
    }

    @Override
    public List<EmailContact> findByEmail(String email) {
        return repository.findAll(
            emailContact.email.eq(email)
                .and(emailContact.client.deleted.isFalse()))
            .stream()
            .map(EmailContactEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Override
    public List<EmailContact> findByEmailNormalized(String email) {
        return repository.findAll(
            emailContact.email.lower().eq(email.toLowerCase())
                .and(emailContact.client.deleted.isFalse()))
            .stream()
            .map(EmailContactEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<EmailContactEntity> findExistingEmailContact(Long clientId, String email) {
        return repository.getOptional(
            emailContact.client.id.eq(clientId)
                .and(emailContact.email.lower().eq(email.toLowerCase())));
    }

    @Override
    @Transactional
    public void verifyEmail(EmailVerificationCommand command) {
        log.info("Verifying email: [{}]", command);
        findExistingEmailContact(command.getClientId(), command.getEmail()).ifPresent(emailContactEntity -> {
            if (emailContactEntity.isVerified()) {
                throw new EmailAlreadyVerifiedException("Email is already verified");
            } else {
                emailContactEntity.setVerified(true);
                emailContactEntity.setVerifiedAt(TimeMachine.now());
                repository.save(emailContactEntity);
            }
        });
    }


}
