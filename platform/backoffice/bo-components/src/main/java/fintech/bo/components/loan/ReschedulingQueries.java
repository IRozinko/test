package fintech.bo.components.loan;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

import static fintech.bo.db.jooq.alfa.tables.LoanRescheduling.LOAN_RESCHEDULING;

@Component
public class ReschedulingQueries {

    @Autowired
    private DSLContext db;

    @Cacheable(value = "rescheduling_statuses", unless = "#result.size() == 0")
    public List<String> findStatuses() {
        return db.selectDistinct(LOAN_RESCHEDULING.STATUS)
            .from(LOAN_RESCHEDULING)
            .where(LOAN_RESCHEDULING.STATUS.isNotNull())
            .fetch()
            .map(record -> record.get(LOAN_RESCHEDULING.STATUS));
    }
}
