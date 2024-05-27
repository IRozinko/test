package fintech.bo.components.payments.disbursement;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import static fintech.bo.db.jooq.payment.Tables.DISBURSEMENT;

@Slf4j
@Component
public class DisbursementQueueCache {

    private final DSLContext db;

    @Autowired
    public DisbursementQueueCache(DSLContext db) {
        this.db = db;
    }

    @Cacheable(value = "disbursements_count", cacheManager = "cacheManager10Sec", sync = true)
    public Integer countPendingDisbursements() {
        try {
            log.debug("Counting pending disbursements");
            return db.selectCount().from(DISBURSEMENT).where(DISBURSEMENT.STATUS_DETAIL.eq(DisbursementConstants.STATUS_DETAIL_PENDING)).fetchOne(0, int.class);
        } catch (Exception e) {
            log.error("Failed to count pending disbursements", e);
            return -1;
        }
    }
}
