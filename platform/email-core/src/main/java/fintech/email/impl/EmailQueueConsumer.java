package fintech.email.impl;


import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.TimeMachine;
import fintech.email.db.EmailLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static fintech.email.db.Entities.emailLog;

@Slf4j
@Component
public class EmailQueueConsumer {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private EmailSender sender;

    public void consumeNow() {
        consume(TimeMachine.now());
    }

    public void consume(LocalDateTime nextAttemptsBefore) {
        List<Long> emailIds = queryFactory.select(emailLog.id).from(emailLog).where(emailLog.sendingStatus.eq(EmailLogEntity.Status.PENDING)
            .and(emailLog.nextAttemptAt.before(nextAttemptsBefore))).orderBy(emailLog.nextAttemptAt.asc()).limit(100).fetch();
        if (emailIds.isEmpty()) {
            return;
        }
        log.info("Found {} emails to send", emailIds.size());
        for (Long id : emailIds) {
            sender.send(id);
        }
    }
}
