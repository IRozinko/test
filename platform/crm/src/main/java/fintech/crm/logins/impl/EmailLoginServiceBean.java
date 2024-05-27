package fintech.crm.logins.impl;

import fintech.Validate;
import fintech.crm.client.db.ClientRepository;
import fintech.crm.logins.*;
import fintech.crm.logins.db.EmailLoginEntity;
import fintech.crm.logins.db.EmailLoginRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static fintech.crm.db.Entities.emailLogin;
import static java.lang.String.format;

@Slf4j
@Component
public class EmailLoginServiceBean implements EmailLoginService {

    private final EmailLoginRepository repository;
    private final ClientRepository clientRepository;

    @Autowired
    public EmailLoginServiceBean(EmailLoginRepository repository, ClientRepository clientRepository) {
        this.repository = repository;
        this.clientRepository = clientRepository;
    }

    @Transactional
    @Override
    public Long add(AddEmailLoginCommand command) throws DuplicateEmailLoginException {
        String email = Validate.notNull(command.getEmail(), "Empty email").toLowerCase().trim();

        Optional<EmailLoginEntity> existingEmailLogin = repository
            .getOptional(emailLogin.email.equalsIgnoreCase(email));
        if (existingEmailLogin.isPresent()) {
            throw new DuplicateEmailLoginException(format("Email login already in use: %s", email));
        }

        EmailLoginEntity login = new EmailLoginEntity();
        login.setClient(clientRepository.getRequired(command.getClientId()));
        login.setEmail(email);
        String passwordHash = PasswordHash.createHash(command.getPassword());
        login.setPassword(passwordHash);
        login.setTemporaryPassword(command.isTemporaryPassword());
        repository.saveAndFlush(login);

        return login.getId();
    }

    @Transactional
    @Override
    public void changeEmail(ChangeEmailCommand command) throws DuplicateEmailLoginException {
        String newEmail = Validate.notBlank(command.getNewEmail(), "Empty new email").toLowerCase().trim();
        String currentEmail = Validate.notBlank(command.getCurrentEmail(), "Empty current email").toLowerCase().trim();

        Optional<EmailLoginEntity> existingEmailLogin = repository
            .getOptional(emailLogin.email.equalsIgnoreCase(newEmail));
        if (existingEmailLogin.isPresent()) {
            throw new DuplicateEmailLoginException(format("Email login already in use: %s", newEmail));
        }

        EmailLoginEntity emailLoginEntity = repository.findOne(emailLogin.email.equalsIgnoreCase(currentEmail)
            .and(emailLogin.client.id.eq(command.getClientId())));

        if (emailLoginEntity == null) {
            log.warn("Changing email for client {}: login entity not found", command.getClientId());
            return;
        }

        emailLoginEntity.setEmail(newEmail);
        repository.saveAndFlush(emailLoginEntity);
    }

    @Transactional
    @Override
    public void changePassword(ChangePasswordCommand command) throws CurrentPasswordMatchException {
        Validate.notEmpty(command.getNewPassword(), "Empty password");
        EmailLoginEntity emailLoginEntity = repository.findOne(emailLogin.email.equalsIgnoreCase(command.getEmail())
            .and(emailLogin.client.id.eq(command.getClientId())));

        if (!emailLoginEntity.isTemporaryPassword()) {
            if (!PasswordHash.verifyPassword(command.getCurrentPassword(), emailLoginEntity.getPassword())) {
                throw new CurrentPasswordMatchException(format("Current password doesn't match for user: %s", command.getEmail()));
            }
        }
        String newPasswordHash = PasswordHash.createHash(command.getNewPassword());
        emailLoginEntity.setPassword(newPasswordHash);
        emailLoginEntity.setTemporaryPassword(false);
        repository.saveAndFlush(emailLoginEntity);
    }

    @Transactional
    @Override
    public void setTemporaryPassword(Long clientId, String email, String password) {
        Validate.notBlank(password, "Cannot set empty password");
        EmailLoginEntity emailLoginEntity = repository.findOne(emailLogin.email.equalsIgnoreCase(email)
            .and(emailLogin.client.id.eq(clientId)));
        String newPasswordHash = PasswordHash.createHash(password);
        emailLoginEntity.setPassword(newPasswordHash);
        emailLoginEntity.setTemporaryPassword(true);
        repository.saveAndFlush(emailLoginEntity);
    }

    @Override
    public Optional<EmailLogin> findByEmail(String email) {
        Validate.notBlank(email, "Blank email");
        String normalizedEmail = email.toLowerCase().trim();
        return repository.getOptional(
            emailLogin.email.eq(normalizedEmail)
                .and(emailLogin.client.deleted.isFalse()))
            .map(EmailLoginEntity::toValueObject);
    }

    @Override
    public Optional<EmailLogin> findByClientId(Long clientId) {
        Validate.notNull(clientId, "Null client id");
        return repository.getOptional(
            emailLogin.client.id.eq(clientId)
                .and(emailLogin.client.deleted.isFalse()))
            .map(EmailLoginEntity::toValueObject);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !repository.exists(emailLogin.email.equalsIgnoreCase(email));
    }

    @Override
    public void delete(String email) {
        Validate.notBlank(email, "Blank email");
        String normalizedEmail = email.toLowerCase().trim();
        repository.deleteByEmail(normalizedEmail);
    }

    @Override
    public void delete(Long clientId) {
        repository.deleteByClientId(clientId);
    }
}
