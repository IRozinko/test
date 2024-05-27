package fintech.crm.logins.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import fintech.TimeMachine;
import fintech.crm.client.db.ClientRepository;
import fintech.crm.db.Entities;
import fintech.crm.logins.*;
import fintech.crm.logins.db.EmailLoginEntity;
import fintech.crm.logins.db.EmailLoginRepository;
import fintech.crm.logins.db.ResetPasswordTokenEntity;
import fintech.crm.logins.db.ResetPasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static fintech.crm.db.Entities.resetPasswordToken;
import static java.lang.String.format;

@Transactional
@Component
public class ResetPasswordServiceBean implements ResetPasswordService {

    @Autowired
    private ResetPasswordTokenRepository tokenRepository;

    @Autowired
    private EmailLoginRepository loginRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public String generateToken(GenerateTokenCommand command) {

        ResetPasswordTokenEntity entity = new ResetPasswordTokenEntity();

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
    public EmailLogin resetPassword(ResetPasswordCommand command) throws ResetPasswordException {
        ResetPasswordTokenEntity tokenEntity = getValidTokenEntry(command);
        EmailLoginEntity login = getEmailLogin(tokenEntity);
        updateLoginEntity(command, login);
        updateTokenEntity(tokenEntity);
        return login.toValueObject();
    }

    @Override
    public List<String> findTokensByClient(Long clientId) {
        return tokenRepository.findAll(Entities.resetPasswordToken.client.id.eq(clientId))
            .stream().map(ResetPasswordTokenEntity::getToken).collect(Collectors.toList());
    }

    private void updateTokenEntity(ResetPasswordTokenEntity tokenEntity) {
        tokenEntity.setUsed(true);
        tokenRepository.saveAndFlush(tokenEntity);
    }

    private void updateLoginEntity(ResetPasswordCommand command, EmailLoginEntity login) {
        String passwordHash = PasswordHash.createHash(command.getPassword());
        login.setPassword(passwordHash);
        login.setTemporaryPassword(false);
        loginRepository.saveAndFlush(login);
    }

    private EmailLoginEntity getEmailLogin(ResetPasswordTokenEntity tokenEntity) throws ResetPasswordException {
        Optional<EmailLoginEntity> emailLogin = loginRepository
            .getOptional(Entities.emailLogin.client.eq(tokenEntity.getClient()));

        return emailLogin
            .orElseThrow(() -> clientNotFoundException(tokenEntity.getClient().getId()));
    }

    private ResetPasswordTokenEntity getValidTokenEntry(ResetPasswordCommand command) throws ResetPasswordException {
        Optional<ResetPasswordTokenEntity> optionalTokenEntry = tokenRepository
            .getOptional(byValidToken(command.getToken()));
        return optionalTokenEntry.orElseThrow(this::tokenNotFoundException);
    }

    private BooleanExpression byValidToken(String token) {
        return resetPasswordToken.token.eq(token)
            .and(resetPasswordToken.expiresAt.gt(TimeMachine.now()))
            .and(resetPasswordToken.used.eq(false));
    }

    private ResetPasswordException clientNotFoundException(long clientId) {
        return new ResetPasswordException(format("Client with id: %s not found", clientId));
    }

    private ResetPasswordException tokenNotFoundException() {
        return new ResetPasswordException("Valid reset password token not found");
    }
}
