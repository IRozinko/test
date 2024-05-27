package fintech.notification.impl;

import com.querydsl.core.types.Predicate;
import fintech.TimeMachine;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.email.EmailService;
import fintech.email.db.EmailLogEntity;
import fintech.notification.Notification;
import fintech.notification.NotificationQuery;
import fintech.notification.NotificationService;
import fintech.notification.db.NotificationEntity;
import fintech.notification.db.NotificationRepository;
import fintech.sms.SmsService;
import fintech.sms.db.SmsLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.querydsl.core.types.ExpressionUtils.allOf;
import static fintech.notification.db.Entities.notification;

@Slf4j
@Component
public class NotificationServiceBean implements NotificationService {

    private final NotificationRepository repository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final ClientService clientService;
    private final EntityManager entityManager;

    public NotificationServiceBean(NotificationRepository repository, EmailService emailService,
                                   SmsService smsService, ClientService clientService, EntityManager entityManager) {
        this.repository = repository;
        this.emailService = emailService;
        this.smsService = smsService;
        this.clientService = clientService;
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public Long send(Notification notification) {
        boolean blockCommunication = Optional.ofNullable(notification.getClientId())
            .map(clientService::get)
            .map(Client::isBlockCommunication)
            .orElse(false);

        if (blockCommunication) {
            log.warn("Ignoring notification, communication with client [{}] blocked", notification.getClientId());
            return saveNotification(notification, null, null);
        }

        Long emailId = null;
        if (notification.getEmail() != null) {
            emailId = emailService.enqueue(notification.getEmail());
        }
        Long smsId = null;
        if (notification.getSms() != null) {
            smsId = smsService.enqueue(notification.getSms());
        }

        if (emailId == null && smsId == null) {
            log.warn("Ignoring notification, no email or sms will be sent for client id [{}]", notification.getClientId());
        }
        return saveNotification(notification, emailId, smsId);
    }

    @Override
    public List<Notification> find(NotificationQuery query) {
        return repository.findAll(allOf(toPredicates(query)), notification.id.desc())
            .stream()
            .map(NotificationEntity::toValueObject)
            .collect(Collectors.toList());
    }

    private List<Predicate> toPredicates(NotificationQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getClientId() != null) {
            predicates.add(notification.clientId.eq(query.getClientId()));
        }
        if (query.getCmsKey() != null) {
            predicates.add(notification.cmsKey.eq(query.getCmsKey()));
        }
        if (query.getSentAfter() != null) {
            predicates.add(notification.sentAt.after(query.getSentAfter()));
        }
        return predicates;
    }

    private Long saveNotification(Notification notification, Long emailId, Long smsId) {
        NotificationEntity entity = new NotificationEntity();
        entity.setCmsKey(notification.getCmsKey());
        entity.setClientId(notification.getClientId());
        entity.setDebtId(notification.getDebtId());
        entity.setLoanApplicationId(notification.getLoanApplicationId());
        entity.setLoanId(notification.getLoanId());
        entity.setTaskId(notification.getTaskId());
        entity.setSentAt(TimeMachine.now());
        if (emailId != null) {
            entity.setEmailLog(entityManager.getReference(EmailLogEntity.class, emailId));
        }
        if (smsId != null) {
            entity.setSmsLog(entityManager.getReference(SmsLogEntity.class, smsId));
        }
        return repository.saveAndFlush(entity).getId();
    }
}
