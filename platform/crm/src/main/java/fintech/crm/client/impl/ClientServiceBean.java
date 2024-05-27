package fintech.crm.client.impl;

import com.google.common.base.MoreObjects;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.crm.CrmConstants;
import fintech.crm.client.Client;
import fintech.crm.client.ClientRegisteredEvent;
import fintech.crm.client.ClientSegmentEmbeddable;
import fintech.crm.client.ClientService;
import fintech.crm.client.CreateClientCommand;
import fintech.crm.client.UpdateClientCommand;
import fintech.crm.client.db.ClientEntity;
import fintech.crm.client.db.ClientRepository;
import fintech.crm.client.model.ChangeAcceptMarketingCommand;
import fintech.crm.db.Entities;
import fintech.crm.marketing.event.ClientMarketingConsentChanged;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.crm.CrmConstants.BLOCK_COMMUNICATION;

@Slf4j
@Transactional
@Component
class ClientServiceBean implements ClientService {

    private final ClientRepository clientRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    @Autowired
    public ClientServiceBean(ClientRepository clientRepository, ApplicationEventPublisher eventPublisher) {
        this.clientRepository = clientRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Long create(CreateClientCommand command) {
        ClientEntity client = new ClientEntity();
        String clientNumber = command.getClientNumber();
        client.setNumber(clientNumber);
        client.setLocale(CrmConstants.DEFAULT_LOCALE);
        client = clientRepository.saveAndFlush(client);
        eventPublisher.publishEvent(new ClientRegisteredEvent(client.toValueObject()));
        return client.getId();
    }

    @Override
    public void update(UpdateClientCommand command) {
        ClientEntity client = clientRepository.getRequired(command.getClientId());
        client.setTitle(command.getTitle());
        client.setFirstName(command.getFirstName());
        client.setSecondFirstName(command.getSecondFirstName());
        client.setLastName(command.getLastName());
        client.setSecondLastName(command.getSecondLastName());
        client.setMaidenName(command.getMaidenName());
        client.setGender(command.getGender());
        client.setDateOfBirth(command.getDateOfBirth());

        client.setAcceptTerms(command.isAcceptTerms());
        client.setAcceptVerification(command.isAcceptVerification());
        client.setAcceptPrivacyPolicy(command.isAcceptPrivacyPolicy());
        client.setBlockCommunication(command.isBlockCommunication());
        client.setExcludedFromASNEF(command.isExcludedFromASNEF());
        client.setTransferredToLoc(command.isTransferredToLoc());

        if (client.isAcceptMarketing() != command.isAcceptMarketing()) {
            updateAcceptMarketing(new ChangeAcceptMarketingCommand(client.getId(), command.isAcceptMarketing()));
        }

        command.getAttributes().forEach((key, value) -> client.getAttributes().put(key, MoreObjects.firstNonNull(value, "")));
    }

    @Override
    public void updateAttributes(UpdateClientCommand command) {
        ClientEntity client = clientRepository.getRequired(command.getClientId());
        command.getAttributes().forEach((key, value) -> client.getAttributes().put(key, MoreObjects.firstNonNull(value, "")));
    }

    @Override
    public void updateBlockCommunication(Long clientId, boolean isBlockCommunication, String reason) {
        Validate.notNull(clientId);
        ClientEntity entity = clientRepository.getRequired(clientId);
        entity.setBlockCommunication(isBlockCommunication);
        if (isBlockCommunication) {
            entity.getAttributes().put(BLOCK_COMMUNICATION, reason);
        } else {
            entity.getAttributes().remove(BLOCK_COMMUNICATION);
        }
    }

    @Override
    public Client get(Long clientId) {
        Validate.notNull(clientId);
        ClientEntity entity = clientRepository.getRequired(clientId);
        return entity.toValueObject();
    }

    @Override
    public Optional<Client> findByClientNumber(String clientNumber) {
        Validate.notNull(clientNumber);
        return clientRepository.getOptional(Entities.client.number.eq(clientNumber)).map(ClientEntity::toValueObject);
    }

    @Override
    public Optional<Client> findByPhone(String phone) {
        Validate.notNull(phone);
        return clientRepository.getOptional(Entities.client.phone.eq(phone).and(Entities.client.deleted.eq(false))).map(ClientEntity::toValueObject);
    }

    @Override
    public Optional<Client> findByDocumentNumber(String dni) {
        Validate.notNull(dni);
        return clientRepository.getOptional(Entities.client.documentNumber.eq(dni).and(Entities.client.deleted.eq(false))).map(ClientEntity::toValueObject);
    }

    @Override
    public void addToSegment(Long clientId, String... segments) {
        addToSegment(clientId, TimeMachine.now(), segments);
    }

    @Override
    public void addToSegment(Long clientId, LocalDateTime when, String... segments) {
        ClientEntity entity = clientRepository.getRequired(clientId);
        for (String segment : segments) {
            entity.getSegments().add(ClientSegmentEmbeddable.builder().segment(segment).addedAt(when).build());
        }
        updateSegmentsText(entity);
    }

    @Override
    public void removeFromSegment(Long clientId, String... segments) {
        ClientEntity entity = clientRepository.getRequired(clientId);
        for (String segment : segments) {
            entity.getSegments().removeIf(item -> segment.equals(item.getSegment()));
        }
        updateSegmentsText(entity);
    }

    @Override
    public void updateAcceptMarketing(ChangeAcceptMarketingCommand cmd) {
        ClientEntity entity = clientRepository.getRequired(cmd.clientId);
        entity.setAcceptMarketing(cmd.newValue);
        eventPublisher.publishEvent(new ClientMarketingConsentChanged(cmd));
    }

    private void updateSegmentsText(ClientEntity entity) {
        entity.setSegmentsText(entity.getSegments().stream().map(ClientSegmentEmbeddable::getSegment).sorted(String::compareTo).collect(Collectors.joining(", ")));
    }
}
