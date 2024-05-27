package fintech.notification

import fintech.notification.db.NotificationRepository
import fintech.sms.db.SmsLogEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.stream.Collectors

import static fintech.notification.db.Entities.notification

@Component
class NotificationHelper {

    @Autowired
    private NotificationRepository notificationRepository

    int countEmails(Long clientId, String cmsKey) {
        return notificationRepository.count(notification.emailLog.isNotNull() & notification.clientId.eq(clientId) & notification.cmsKey.eq(cmsKey))
    }

    int countEmails(String email, String cmsKey) {
        return notificationRepository.count(notification.emailLog.isNotNull() & notification.emailLog.to.eq(email) & notification.cmsKey.eq(cmsKey))
    }

    int countSms(Long clientId, String cmsKey) {
        return notificationRepository.count(notification.smsLog.isNotNull() & notification.clientId.eq(clientId) & notification.cmsKey.eq(cmsKey))
    }

    List<SmsLogEntity> findSms(Long clientId, String cmsKey) {
        return notificationRepository.findAll(notification.smsLog.isNotNull() & notification.clientId.eq(clientId) & notification.cmsKey.eq(cmsKey), notification.smsLog.id.desc())
            .stream()
            .map { n -> n.smsLog }
            .collect(Collectors.toList())
    }
}
