package fintech.payxpert.spi;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.payxpert.PaymentRequestStatus;
import fintech.payxpert.PayxpertService;
import fintech.payxpert.db.Entities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class PayxpertBatchJobs {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private PayxpertService service;

    @Transactional(propagation = Propagation.NEVER)
    public void checkPaymentRequestStatuses(LocalDateTime when, Long maxAttempts, Long checkDelayInSeconds) {
        List<Long> ids = queryFactory.select(Entities.paymentRequest.id)
            .from(Entities.paymentRequest)
            .where(
                Entities.paymentRequest.status.eq(PaymentRequestStatus.PENDING),
                Entities.paymentRequest.statusCheckAttempts.lt(maxAttempts),
                Entities.paymentRequest.lastStatusCheckAt.isNull().or(Entities.paymentRequest.lastStatusCheckAt.lt(when.minusSeconds(checkDelayInSeconds)))
            ).limit(1000).fetch();
        if (!ids.isEmpty()) {
            log.info("Found [{}] payment requests to check status", ids.size());
        }
        for (Long requestId : ids) {
            try {
                service.updateStatusCheckAttempts(requestId, when);
                service.checkRequestStatus(requestId, when);
            } catch (Exception e) {
                log.error("Failed to check payment request [" + requestId + "] status", e);
            }
        }
    }
}
