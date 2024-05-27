package fintech.marketing;

import fintech.crm.client.ClientService;
import fintech.crm.client.model.ChangeAcceptMarketingCommand;
import fintech.crm.logins.EmailLogin;
import fintech.crm.logins.EmailLoginService;
import fintech.crm.security.InvalidEmailException;
import fintech.crm.security.OneTimeTokenService;
import fintech.crm.security.db.TokenType;
import fintech.marketing.db.MarketingCommunicationEntity;
import fintech.marketing.db.MarketingCommunicationRepository;
import lombok.extern.slf4j.Slf4j;
import net.agkn.hll.HLL;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;


@Slf4j
@Service
@Transactional
public class MarketingService {

    @Autowired
    private EmailLoginService emailLoginService;

    @Autowired
    private OneTimeTokenService oneTimeTokenService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private MarketingCommunicationRepository communicationRepository;

    private final static Hashids viewsHashIds = new Hashids("46fewt56t43fgr5cgfrey5hg46", 20);
    private final static Hashids clicksHashIds = new Hashids("84kv6jdl132ocxawoibujh577", 20);

    public void unsubscribe(String onetimeToken, String email) {
        Optional<EmailLogin> emailLoginMaybe = emailLoginService.findByEmail(email);
        if (!emailLoginMaybe.isPresent()) {
            throw new InvalidEmailException("Invalid email");
        }
        Long clientId = oneTimeTokenService.getClientIdByToken(TokenType.MARKETING_UNSUBSCRIBE, onetimeToken);

        if (!clientId.equals(emailLoginMaybe.get().getClientId())) {
            throw new InvalidEmailException("Invalid email");
        }
        oneTimeTokenService.validateToken(TokenType.MARKETING_UNSUBSCRIBE, onetimeToken);

        clientService.updateAcceptMarketing(new ChangeAcceptMarketingCommand(clientId, false, "email_unsubscribe_link"));
    }

    public void trackViews(String uuid) {
        long[] array;
        try {
            array = viewsHashIds.decode(uuid);
        } catch (RuntimeException e) {
            log.info(e.getMessage(), e);
            return;
        }

        if (array.length != 2) {
            log.info(String.format("Invalid marketing view uuid [%s]", uuid));
            return;
        }

        long clientId = array[0];
        long communicationId = array[1];

        MarketingCommunicationEntity entity = communicationRepository.lock(communicationId);

        HLL communicationHll = HllUtils.fromXex(entity.getViewsHllHex());
        communicationHll.addRaw(clientId);
        if (entity.getTargetedUsers() != null && entity.getTargetedUsers() > 0) {
            entity.setViewRate(BigDecimal.valueOf((double) communicationHll.cardinality() / entity.getTargetedUsers()));
        } else {
            entity.setViewRate(null);
        }
        entity.setViewsHllHex(HllUtils.toXex(communicationHll));
    }

    public void trackClicks(String uuid) {
        long[] array;
        try {
            array = clicksHashIds.decode(uuid);
        } catch (RuntimeException e) {
            log.info(e.getMessage(), e);
            return;
        }

        if (array.length != 2) {
            log.info(String.format("Invalid marketing click uuid [%s]", uuid));
            return;
        }

        long clientId = array[0];
        long communicationId = array[1];

        MarketingCommunicationEntity entity = communicationRepository.lock(communicationId);

        HLL communicationHll = HllUtils.fromXex(entity.getClicksHllHex());
        communicationHll.addRaw(clientId);
        if (entity.getTargetedUsers() != null && entity.getTargetedUsers() > 0) {
            entity.setClickRate(BigDecimal.valueOf((double) communicationHll.cardinality() / entity.getTargetedUsers()));
        } else {
            entity.setClickRate(null);
        }
        entity.setClicksHllHex(HllUtils.toXex(communicationHll));
    }

    public String getTrackClickUuid(long clientId, long communicationId) {
        return clicksHashIds.encode(clientId, communicationId);
    }

    public String getTrackViewUuid(long clientId, long communicationId) {
        return viewsHashIds.encode(clientId, communicationId);
    }

}
