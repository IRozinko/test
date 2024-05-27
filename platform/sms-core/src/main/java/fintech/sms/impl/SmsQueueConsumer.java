package fintech.sms.impl;


import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.sms.db.SmsLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static fintech.sms.db.Entities.smsLog;

@Slf4j
@Component
public class SmsQueueConsumer {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private SmsSender sender;

    public void consume(LocalDateTime nextAttemptsBefore) {
        List<Long> smsIds = queryFactory.select(smsLog.id).from(smsLog).where(smsLog.sendingStatus.eq(SmsLogEntity.Status.PENDING)
            .and(smsLog.nextAttemptAt.before(nextAttemptsBefore))).orderBy(smsLog.nextAttemptAt.asc()).limit(100).fetch();
        if (smsIds.isEmpty()) {
            return;
        }
        log.info("Found {} sms to send", smsIds.size());
        for (Long id : smsIds) {
            sender.send(id);
        }
    }
}
