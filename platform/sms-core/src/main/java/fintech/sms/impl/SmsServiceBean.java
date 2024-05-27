package fintech.sms.impl;

import fintech.Validate;
import fintech.sms.IncomingSms;
import fintech.sms.Sms;
import fintech.sms.SmsDeliveryReport;
import fintech.sms.SmsService;
import fintech.sms.db.Entities;
import fintech.sms.db.IncomingSmsEntity;
import fintech.sms.db.IncomingSmsRepository;
import fintech.sms.db.SmsLogEntity;
import fintech.sms.db.SmsLogRepository;
import fintech.sms.spi.ReceivedIncomingSmsEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
@Component
class SmsServiceBean implements SmsService {

    // Fixme: remove using ExecutorService - bad practice, unhandled errors, silent failures, etc...
    // ToDo: what is the reason to use executorService ?
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private SmsLogRepository repository;

    @Autowired
    private IncomingSmsRepository incomingSmsRepository;

    @Transactional
    @Override
    public Long enqueue(Sms sms) {
        checkNotNull(sms, "Null sms");
        log.info("Enqueuing sms [{}]", sms);
        SmsLogEntity log = buildLog(sms);
        return repository.saveAndFlush(log).getId();
    }

    private SmsLogEntity buildLog(Sms sms) {
        SmsLogEntity log = new SmsLogEntity();
        log.setSenderId(sms.getSenderId());
        log.setTo(sms.getTo());
        log.setText(sms.getText());
        log.setNextAttemptAt(sms.getSendAt());
        log.setSendingStatus(SmsLogEntity.Status.PENDING);
        log.setMaxAttempts(sms.getMaxSendingAttempts());
        log.setAttemptTimeoutInSeconds(sms.getAttemptTimeoutInSeconds());
        return log;
    }

    @Transactional
    @Override
    public boolean deliveryReportReceived(SmsDeliveryReport deliveryReport) {
        Validate.notNull(deliveryReport.getProviderMessageId(), "Provider message id required");
        Validate.notNull(deliveryReport.getReceivedAt(), "Received at required");
        Validate.notNull(deliveryReport.getStatus(), "Status required");
        Optional<SmsLogEntity> maybe = repository.getOptional(Entities.smsLog.providerId.eq(deliveryReport.getProviderMessageId()));
        if (!maybe.isPresent()) {
            log.warn("SMS not found by delivery report: [{}]", deliveryReport);
            return false;
        } else {
            SmsLogEntity sms = maybe.get();
            log.info("Received delivery report [{}] for SMS [{}]", deliveryReport, sms);
            sms.setDeliveryReportReceivedAt(deliveryReport.getReceivedAt());
            sms.setDeliveryReportStatus(deliveryReport.getStatus());
            sms.setDeliveryReportStatus2(deliveryReport.getStatus2());
            sms.setDeliveryReportError(deliveryReport.getError());
            return true;
        }
    }

    @Transactional
    @Override
    @SneakyThrows
    public Long takeIncomingSms(IncomingSms sms) {
        IncomingSmsEntity entity = new IncomingSmsEntity();
        entity.setPhoneNumber(sms.getPhoneNumber());
        entity.setRawDataJson(sms.getRawDataJson());
        entity.setSource(sms.getSource());
        entity.setText(sms.getText());
        Long id = incomingSmsRepository.saveAndFlush(entity).getId();

        executor.submit(() -> eventPublisher.publishEvent(new ReceivedIncomingSmsEvent(sms)));
        return id;
    }

    @PreDestroy
    public void onPreDestroy() {
        shutdown();
    }

    @SneakyThrows
    private static void shutdown() {
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
    }

}
