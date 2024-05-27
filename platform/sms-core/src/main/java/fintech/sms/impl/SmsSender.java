package fintech.sms.impl;


import com.google.common.base.Throwables;
import fintech.TimeMachine;
import fintech.sms.db.SmsLogEntity;
import fintech.sms.db.SmsLogRepository;
import fintech.sms.mock.MockSmsProvider;
import fintech.sms.spi.SmsException;
import fintech.sms.spi.SmsProvider;
import fintech.sms.spi.SmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
@Component
public class SmsSender {

    @Resource(name = "${sms.provider:" + MockSmsProvider.NAME + "}")
    private SmsProvider provider;

    @Autowired
    private SmsLogRepository repository;

    @Value("${sms.whitelistedNumbers:}")
    private String whitelistedNumbers;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(Long logId) {
        SmsLogEntity smsLog = repository.findOne(logId);

        checkNotNull(smsLog, "Sms not found by id %s", logId);
        checkArgument(smsLog.getSendingStatus() == SmsLogEntity.Status.PENDING, "Illegal status %s", smsLog.getSendingStatus());

        smsLog.setAttempts(smsLog.getAttempts() + 1);
        try {
            if (!StringUtils.isBlank(whitelistedNumbers) && !StringUtils.containsIgnoreCase(whitelistedNumbers, smsLog.getTo())) {
                log.info("Ignoring SMS, number [{}] is not whitelisted in: [{}]", smsLog.getTo(), whitelistedNumbers);
                smsLog.setSendingStatus(SmsLogEntity.Status.IGNORED);
                smsLog.setError("Number not in whitelist");
                return;
            }
            SmsResponse response = provider.send(smsLog.toSms());
            smsLog.setSendingStatus(SmsLogEntity.Status.SENT);
            smsLog.setProviderMessage(response.getMessage());
            smsLog.setProviderId(response.getId());
            smsLog.setProvider(response.getProviderName());
            log.info("Sms {} sent successfully, provider response {}", smsLog, response);
            return;
        } catch (SmsException e) {
            log.warn("Failed to send sms {}, error {}", smsLog, e.getMessage());
            smsLog.setError(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to send sms " + smsLog, e);
            smsLog.setError(Throwables.getRootCause(e).getMessage());
        }

        if (smsLog.getAttempts() >= smsLog.getMaxAttempts()) {
            smsLog.setSendingStatus(SmsLogEntity.Status.FAILED);
            log.info("Reached max {} sending attempts, marking sms as failed: {}", smsLog.getMaxAttempts(), smsLog);
        } else {
            int timeout = smsLog.getAttempts() * smsLog.getAttemptTimeoutInSeconds();
            smsLog.setNextAttemptAt(TimeMachine.now().plusSeconds(timeout));
        }
    }

    public void setWhitelistedNumbers(String whitelistedNumbers) {
        this.whitelistedNumbers = whitelistedNumbers;
    }
}
