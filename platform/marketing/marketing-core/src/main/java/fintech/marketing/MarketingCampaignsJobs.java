package fintech.marketing;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static fintech.marketing.db.Entities.marketingCommunication;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketingCampaignsJobs {

    private final TransactionTemplate txTemplate;

    private final JPQLQueryFactory queryFactory;
    private final MarketingCampaignService marketingCampaignService;

    @Transactional(propagation = Propagation.NEVER)
    public int triggerActions(LocalDateTime when) {
        JPQLQuery<Long> query = queryFactory.select(marketingCommunication.id)
            .from(marketingCommunication)
            .where(marketingCommunication.status.eq(MarketingCommunicationStatus.QUEUED).and(marketingCommunication.nextActionAt.loe(when)))
            .orderBy(marketingCommunication.nextActionAt.asc(), marketingCommunication.id.asc());
        Stopwatch stopwatch = Stopwatch.createStarted();
        int c = 0;
        try (CloseableIterator<Long> iterator = query.iterate()) {
            while (iterator.hasNext()) {
                Long communicationId = iterator.next();
                try {
                    marketingCampaignService.triggerCommunication(communicationId);
                } catch (Exception e) {
                    log.error("Failed to trigger communication [" + communicationId + "] ", e);
                    logError(communicationId, e);
                }
                c++;
            }
        }
        if (c > 0) {
            log.info("Triggered {} dc actions in {} ms", c, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
        return c;
    }

    private void logError(Long communicationId, Exception e) {
        txTemplate.execute((TransactionCallback<Object>) status -> {
            queryFactory.update(marketingCommunication)
                .set(marketingCommunication.nextActionAt, Expressions.nullExpression())
                .set(marketingCommunication.status, MarketingCommunicationStatus.ERROR)
                .set(marketingCommunication.lastExecutionResult, Throwables.getStackTraceAsString(e))
                .where(marketingCommunication.id.eq(communicationId))
                .execute();
            return 1;
        });
    }

}
