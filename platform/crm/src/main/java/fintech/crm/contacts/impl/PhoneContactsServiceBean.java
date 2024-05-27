package fintech.crm.contacts.impl;

import com.querydsl.core.types.Predicate;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.crm.client.db.ClientEntity;
import fintech.crm.client.db.ClientRepository;
import fintech.crm.contacts.AddPhoneCommand;
import fintech.crm.contacts.AddPhoneVerificationCommand;
import fintech.crm.contacts.ClientPrimaryPhoneUpdatedEvent;
import fintech.crm.contacts.DuplicatePrimaryPhoneException;
import fintech.crm.contacts.PhoneAlreadyVerifiedException;
import fintech.crm.contacts.PhoneContact;
import fintech.crm.contacts.PhoneContactService;
import fintech.crm.contacts.PhoneVerificationCodeExpiredException;
import fintech.crm.contacts.PhoneVerifiedEvent;
import fintech.crm.contacts.UpdatePhoneContactCommand;
import fintech.crm.contacts.VerifyPhoneCommand;
import fintech.crm.contacts.db.PhoneContactEntity;
import fintech.crm.contacts.db.PhoneContactRepository;
import fintech.crm.contacts.db.PhoneVerificationEntity;
import fintech.crm.contacts.db.PhoneVerificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.crm.db.Entities.phoneContact;
import static fintech.crm.db.Entities.phoneVerification;

@Slf4j
@Component
class PhoneContactsServiceBean implements PhoneContactService {

    private final PhoneContactRepository phoneContactRepository;
    private final PhoneVerificationRepository phoneVerificationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ClientRepository clientRepository;

    @Autowired
    public PhoneContactsServiceBean(PhoneContactRepository phoneContactRepository, PhoneVerificationRepository phoneVerificationRepository,
                                    ApplicationEventPublisher eventPublisher, ClientRepository clientRepository) {
        this.phoneContactRepository = phoneContactRepository;
        this.phoneVerificationRepository = phoneVerificationRepository;
        this.eventPublisher = eventPublisher;
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional
    public Long addPhoneContact(AddPhoneCommand command) {
        log.info("Adding new phone contact: [{}]", command);
        Optional<PhoneContactEntity> existingPhoneContactEntity = findExistingPhoneContact(command.getClientId(), command.getCountryCode(), command.getLocalNumber());

        if (existingPhoneContactEntity.isPresent()) {
            return existingPhoneContactEntity.get().getId();
        }

        PhoneContactEntity entity = new PhoneContactEntity();
        entity.setClient(clientRepository.getRequired(command.getClientId()));
        entity.setCountryCode(command.getCountryCode());
        entity.setLocalNumber(command.getLocalNumber());
        entity.setPhoneType(command.getType());
        entity.setSource(command.getSource());
        entity.setActiveTill(command.getActiveTill());
        entity.setLegalConsent(command.isLegalConsent());
        entity.setActive(true);
        entity = phoneContactRepository.saveAndFlush(entity);

        return entity.getId();
    }

    @Override
    @Transactional
    public PhoneContact updatePhoneContact(long id, UpdatePhoneContactCommand command) {
        PhoneContactEntity phoneContact = phoneContactRepository.getRequired(id);
        phoneContact.setCountryCode(command.getCountryCode());
        phoneContact.setLocalNumber(command.getLocalNumber());
        phoneContact.setPhoneType(command.getType());
        phoneContact.setSource(command.getSource());
        phoneContact.setActiveTill(command.getActiveTill());
        return phoneContact.toValueObject();
    }

    @Override
    @Transactional
    public void makePhonePrimary(Long phoneContactId) throws DuplicatePrimaryPhoneException {
        PhoneContactEntity existingPhoneNumber = phoneContactRepository.getRequired(phoneContactId);
        if (!isPhoneNumberAvailableForClient(existingPhoneNumber.getClient().getId(), existingPhoneNumber.getCountryCode(), existingPhoneNumber.getLocalNumber())) {
            throw new DuplicatePrimaryPhoneException("Phone number already in use, phone contact id: " + phoneContactId);
        }
        Predicate clientPrimaryPhones = phoneContact.client.eq(existingPhoneNumber.getClient()).and(phoneContact.primary.isTrue());
        phoneContactRepository.findAll(clientPrimaryPhones).forEach(entity -> entity.setPrimary(false));
        existingPhoneNumber.setPrimary(true);
        existingPhoneNumber.getClient().setPhone(existingPhoneNumber.getLocalNumber());
        eventPublisher.publishEvent(new ClientPrimaryPhoneUpdatedEvent(existingPhoneNumber.toValueObject()));
    }

    @Override
    @Transactional
    public void toggleActive(long phoneContactId) {
        PhoneContactEntity phone = phoneContactRepository.getRequired(phoneContactId);
        if (phone.isActive()) {
            phone.setActiveTill(TimeMachine.today());
        } else {
            phone.setActiveTill(null);
        }
        phone.setActive(!phone.isActive());
    }

    @Override
    @Transactional
    public void toggleLegalConsent(long phoneContactId) {
        PhoneContactEntity phone = phoneContactRepository.getRequired(phoneContactId);
        phone.setLegalConsent(!phone.isLegalConsent());
    }

    @Override
    public boolean isPrimaryPhoneNumberAvailable(String localNumber) {
        return !phoneContactRepository.exists(
            phoneContact.localNumber.eq(localNumber)
                .and(phoneContact.primary.isTrue())
                .and(phoneContact.client.deleted.isFalse()));
    }

    @Override
    public Optional<PhoneContact> findPrimaryPhone(Long clientId) {
        return phoneContactRepository
            .getOptional(phoneContact.client.id.eq(clientId)
                .and(phoneContact.primary.isTrue()))
            .map(PhoneContactEntity::toValueObject);
    }

    @Override
    public List<PhoneContact> findAdditionalPhones(Long clientId) {
        return phoneContactRepository.findAll(
            phoneContact.client.id.eq(clientId)
                .and(phoneContact.primary.isFalse()
                    .and(phoneContact.active.isTrue())),
            phoneContact.createdAt.desc()
        ).stream().map(PhoneContactEntity::toValueObject).collect(Collectors.toList());
    }

    @Override
    public List<PhoneContact> findClientPhoneContacts(Long clientId) {
        return phoneContactRepository.findAll(phoneContact.client.id.eq(clientId)).stream()
            .map(PhoneContactEntity::toValueObject).collect(Collectors.toList());
    }

    @Override
    public Optional<PhoneContact> findActualAdditionalPhone(Long clientId) {
        return phoneContactRepository.findAll(
            phoneContact.client.id.eq(clientId)
                .and(phoneContact.primary.isFalse()
                    .and(phoneContact.active.isTrue())),
            phoneContact.createdAt.desc()
        ).stream()
            .findFirst()
            .map(PhoneContactEntity::toValueObject);
    }

    @Override
    public List<PhoneContact> findByLocalPhoneNumber(String localPhoneNumber) {
        return phoneContactRepository.findAll(
            phoneContact.localNumber.eq(localPhoneNumber)
                .and(phoneContact.client.deleted.isFalse()))
            .stream()
            .map(PhoneContactEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void addPhoneVerification(AddPhoneVerificationCommand command) {
        log.info("Adding phone verification: [{}]", command);
        PhoneContactEntity phone = phoneContactRepository.getRequired(command.getPhoneContactId());
        PhoneVerificationEntity entity = new PhoneVerificationEntity();
        entity.setPhoneContact(phone);
        entity.setClient(phone.getClient());
        entity.setCode(command.getCode());
        entity.setLatest(true);

        List<PhoneVerificationEntity> latest = phoneVerificationRepository.findAll(
            phoneVerification.phoneContact.eq(phone).and(phoneVerification.latest.isTrue()));
        latest.forEach(existing -> existing.setLatest(false));

        phoneVerificationRepository.saveAndFlush(entity);
    }

    @Transactional
    @Override
    public boolean verifyPhone(VerifyPhoneCommand command) {
        log.info("Verifying phone: [{}]", command);
        PhoneContactEntity phone = phoneContactRepository.getRequired(command.getPhoneContactId());
        if (phone.isVerified()) {
            throw new PhoneAlreadyVerifiedException("Phone is already verified");
        }

        Optional<PhoneVerificationEntity> maybeVerificationCode = findLatestVerificationCode(command.getPhoneContactId());
        Validate.isTrue(maybeVerificationCode.isPresent(), "No verification code found for phone %s", phone.getLocalNumber());

        PhoneVerificationEntity code = maybeVerificationCode.get();
        if (command.getCodeCreatedAfter().isAfter(code.getCreatedAt())) {
            throw new PhoneVerificationCodeExpiredException("Verification code expired");
        }

        int attempts = code.getAttempts();
        code.setAttempts(++attempts);
        if (!command.getCode().equals(code.getCode())) {
            log.info("Incorrect code");
            return false;
        }

        LocalDateTime verifiedAt = TimeMachine.now();
        code.setVerified(true);
        code.setVerifiedAt(verifiedAt);
        phone.setVerified(true);
        phone.setVerifiedAt(verifiedAt);
        log.info("Phone verified: [{}]", phone);
        eventPublisher.publishEvent(new PhoneVerifiedEvent(phone.toValueObject()));
        return true;
    }

    @Transactional
    @Override
    public void markPhoneUnVerified(Long phoneContactIdLong) {
        log.info("Make phone unverified: [{}]", phoneContactIdLong);
        PhoneContactEntity phone = phoneContactRepository.getRequired(phoneContactIdLong);
        phone.setVerified(false);
        phone.setVerifiedAt(null);
    }

    @Override
    public Optional<PhoneVerificationEntity> findLatestVerificationCode(Long phoneContactId) {
        PhoneContactEntity phone = phoneContactRepository.getRequired(phoneContactId);
        List<PhoneVerificationEntity> codes = phoneVerificationRepository.findAll(
            phoneVerification.phoneContact.eq(phone).and(phoneVerification.latest.isTrue()));
        return codes.stream().findFirst();
    }

    @Override
    public int countSentVerificationCodes(Long clientId, Long phoneContactId) {
        PhoneContactEntity phone = phoneContactRepository.getRequired(phoneContactId);
        ClientEntity client = clientRepository.getRequired(clientId);
        return (int) phoneVerificationRepository.count(phoneVerification.client.eq(client).and(phoneVerification.phoneContact.eq(phone)));
    }

    @Override
    public int getVerificationAttempts(Long phoneContactId) {
        return findLatestVerificationCode(phoneContactId)
            .map(PhoneVerificationEntity::getAttempts)
            .orElse(0);

    }

    private Optional<PhoneContactEntity> findExistingPhoneContact(Long clientId, String countryCode, String localNumber) {
        return phoneContactRepository.getOptional(
            phoneContact.client.id.eq(clientId)
                .and(phoneContact.countryCode.eq(countryCode))
                .and(phoneContact.localNumber.eq(localNumber)));
    }

    private boolean isPhoneNumberAvailableForClient(Long clientId, String countryCode, String localNumber) {
        return !phoneContactRepository.exists(
            phoneContact.countryCode.eq(countryCode)
                .and(phoneContact.localNumber.eq(localNumber))
                .and(phoneContact.primary.isTrue())
                .and(phoneContact.client.id.ne(clientId))
                .and(phoneContact.client.deleted.isFalse()));
    }
}
