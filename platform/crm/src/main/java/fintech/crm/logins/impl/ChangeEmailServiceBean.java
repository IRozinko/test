package fintech.crm.logins.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.crm.client.db.ClientRepository;
import fintech.crm.contacts.AddEmailContactCommand;
import fintech.crm.contacts.EmailContactService;
import fintech.crm.logins.ChangeEmailCommand;
import fintech.crm.logins.ChangeEmailService;
import fintech.crm.logins.EmailLoginService;
import fintech.crm.logins.GenerateTokenCommand;
import fintech.crm.logins.VerifyEmailException;
import fintech.crm.logins.db.EmailLoginEntity;
import fintech.crm.logins.db.EmailLoginRepository;
import fintech.crm.logins.db.VerifyEmailTokenEntity;
import fintech.crm.logins.db.VerifyEmailTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static fintech.crm.db.Entities.emailLogin;
import static fintech.crm.db.Entities.verifyEmailToken;
import static java.lang.String.format;

@Component
public class ChangeEmailServiceBean implements ChangeEmailService {

    @Autowired
    private VerifyEmailTokenRepository tokenRepository;

    @Autowired
    private EmailLoginRepository loginRepository;

    @Autowired
    private EmailContactService emailContactService;

    @Autowired
    private EmailLoginService emailLoginService;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    @Transactional
    public String generateToken(GenerateTokenCommand command) {
        VerifyEmailTokenEntity entity = new VerifyEmailTokenEntity();
        entity.setClient(clientRepository.getRequired(command.getClientId()));
        LocalDateTime currentTime = TimeMachine.now();
        LocalDateTime expirationDate = currentTime.plusHours(command.getValidityInHours());
        entity.setExpiresAt(expirationDate);
        entity.setToken(UUID.randomUUID().toString());
        entity.setUsed(false);
        tokenRepository.saveAndFlush(entity);
        return entity.getToken();
    }

    @Override
    public boolean isEmailAvailable(Long clientId, String email) {
        email = Validate.notBlank(email, "Empty email").toLowerCase().trim();
        boolean isEmailLoginAvailable = emailLoginService.isEmailAvailable(email);
        boolean isEmailContactAvailable = emailContactService.isEmailAvailableForClient(clientId, email);
        return isEmailLoginAvailable && isEmailContactAvailable;
    }

    @Override
    @Transactional
    public void verifyAndChange(ChangeEmailCommand command) throws VerifyEmailException {
        VerifyEmailTokenEntity tokenEntity = getValidTokenEntry(command);
        EmailLoginEntity emailLoginEntity = getEmailLogin(tokenEntity.getClient().getId());
        updateLoginEntity(command, emailLoginEntity);
        updatePrimaryEmail(tokenEntity.getClient().getId(), command.getNewEmail());
        updateTokenEntity(tokenEntity);
    }

    private VerifyEmailTokenEntity getValidTokenEntry(ChangeEmailCommand command) throws VerifyEmailException {
        Optional<VerifyEmailTokenEntity> optionalTokenEntry = tokenRepository.getOptional(byValidToken(command.getToken()));
        return optionalTokenEntry.orElseThrow(this::tokenNotFoundException);
    }

    private BooleanExpression byValidToken(String token) {
        return verifyEmailToken.token.eq(token)
            .and(verifyEmailToken.expiresAt.gt(TimeMachine.now()))
            .and(verifyEmailToken.used.eq(false));
    }

    private EmailLoginEntity getEmailLogin(Long clientId) throws VerifyEmailException {
        Optional<EmailLoginEntity> emailLoginEntity = loginRepository.getOptional(emailLogin.client.id.eq(clientId));
        return emailLoginEntity.orElseThrow(() -> clientNotFoundException(clientId));
    }

    private void updateLoginEntity(ChangeEmailCommand command, EmailLoginEntity login) {
        String newEmail = Validate.notBlank(command.getNewEmail(), "Empty new email").toLowerCase().trim();
        login.setEmail(newEmail);
        loginRepository.saveAndFlush(login);
    }

    private void updatePrimaryEmail(Long clientId, String newEmail) {
        newEmail = Validate.notBlank(newEmail, "Empty new email").toLowerCase().trim();
        AddEmailContactCommand emailCommand = new AddEmailContactCommand();
        emailCommand.setClientId(clientId);
        emailCommand.setEmail(newEmail);
        final Long emailContactId = emailContactService.addEmailContact(emailCommand);
        emailContactService.makeEmailPrimary(emailContactId);
    }

    private void updateTokenEntity(VerifyEmailTokenEntity tokenEntity) {
        tokenEntity.setUsed(true);
        tokenRepository.saveAndFlush(tokenEntity);
    }

    private VerifyEmailException clientNotFoundException(long clientId) {
        return new VerifyEmailException(format("Client with id: %s not found", clientId));
    }

    private VerifyEmailException tokenNotFoundException() {
        return new VerifyEmailException("Valid verify email token not found");
    }
}
