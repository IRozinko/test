package fintech.crm.security;

import fintech.TimeMachine;
import fintech.crm.security.db.OneTimeTokenEntity;
import fintech.crm.security.db.OneTimeTokenRepository;
import fintech.crm.security.db.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.temporal.TemporalAmount;
import java.util.UUID;

import static fintech.crm.db.Entities.oneTimeToken;

@Component
@Transactional
public class OneTimeTokenService {

    @Autowired
    private OneTimeTokenRepository tokenRepository;

    public String generateOrUpdateToken(TokenType type, Long clientId, TemporalAmount timeToLive) {
        OneTimeTokenEntity tokenEntity = tokenRepository.getOptional(
            oneTimeToken.tokenType.eq(type)
                .and(oneTimeToken.usedAt.isNull())
                .and(oneTimeToken.expiresAt.after(TimeMachine.now()))
        )
            .orElseGet(() -> {
                OneTimeTokenEntity t = new OneTimeTokenEntity();
                t.setClientId(clientId);
                t.setTokenType(type);
                t.setToken(UUID.randomUUID().toString());
                return t;
            });
        tokenEntity.setExpiresAt(TimeMachine.now().plus(timeToLive));
        tokenEntity = tokenRepository.save(tokenEntity);
        return tokenEntity.getToken();
    }

    public void validateToken(TokenType tokenType, String token) {
        OneTimeTokenEntity tokenEntity = tokenRepository.getOptional(
            oneTimeToken.token.eq(token)
                .and(oneTimeToken.tokenType.eq(tokenType))
                .and(oneTimeToken.usedAt.isNull())
                .and(oneTimeToken.expiresAt.after(TimeMachine.now())))
            .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        tokenEntity.setUsedAt(TimeMachine.now());
        tokenRepository.save(tokenEntity);
    }

    public Long getClientIdByToken(TokenType tokenType, String token) {
        return tokenRepository.getOptional(oneTimeToken.token.eq(token).and(oneTimeToken.tokenType.eq(tokenType)))
            .map(OneTimeTokenEntity::getClientId)
            .orElseThrow(() -> new InvalidTokenException("Invalid token"));
    }
}
