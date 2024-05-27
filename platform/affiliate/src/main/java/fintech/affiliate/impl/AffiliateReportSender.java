package fintech.affiliate.impl;

import com.google.common.base.Throwables;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.TimeMachine;
import fintech.affiliate.db.AffiliateEventEntity;
import fintech.affiliate.db.AffiliateEventRepository;
import fintech.affiliate.model.ReportStatus;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

import static fintech.affiliate.db.Entities.event;

@Slf4j
@Component
public class AffiliateReportSender {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private TransactionTemplate txTemplate;

    @Autowired
    private AffiliateEventRepository eventRepository;

    @Transactional(propagation = Propagation.NEVER)
    public void send(LocalDateTime nextAttemptBefore) {
        List<Long> eventIds = queryFactory.select(event.id)
            .from(event)
            .where(event.reportStatus.eq(ReportStatus.PENDING).and(event.nextReportAttemptAt.before(nextAttemptBefore)))
            .orderBy(event.id.asc()).limit(100).fetch();
        for (Long id : eventIds) {
            try {
                txTemplate.execute(status -> {
                    AffiliateEventEntity event = eventRepository.getRequired(id);
                    sendHttpRequest(event.getReportUrl());
                    event.setReportStatus(ReportStatus.OK);
                    event.setReportedAt(TimeMachine.now());
                    return 1;
                });
            } catch (Exception e) {
                String errorMessage = Throwables.getRootCause(e).getMessage();
                log.warn("Affiliate event [{}] report request failed: [{}]", id, errorMessage);
                txTemplate.execute(status -> {
                    AffiliateEventEntity event = eventRepository.getRequired(id);
                    event.setReportError(errorMessage);
                    if (event.getReportRetryAttempts() >= 3) {
                        event.setReportStatus(ReportStatus.ERROR);
                    } else {
                        event.setReportRetryAttempts(event.getReportRetryAttempts() + 1);
                        event.setNextReportAttemptAt(nextAttemptBefore.plusMinutes(1));
                    }
                    return 0;
                });
            }
        }
    }

    @SneakyThrows
    private void sendHttpRequest(String reportUrl) {
        URL url = new URL(reportUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        try {
            int responseCode = con.getResponseCode();
            if (responseCode != 200 && responseCode != 201) {
                throw new IllegalStateException("Invalid response code: " + responseCode);
            }
        } finally {
            con.disconnect();
        }
    }
}
