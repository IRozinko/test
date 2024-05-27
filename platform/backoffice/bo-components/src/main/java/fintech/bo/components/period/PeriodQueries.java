package fintech.bo.components.period;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static fintech.bo.components.period.PeriodConstants.STATUS_CLOSED;
import static fintech.bo.db.jooq.lending.tables.Period.PERIOD;

@Component
public class PeriodQueries {

    @Autowired
    private DSLContext db;

    public LocalDate nextClosingPeriod() {
        return (LocalDate) db
            .select(PERIOD.PERIOD_DATE.min())
            .from(PERIOD)
            .where(PERIOD.STATUS.ne(STATUS_CLOSED))
            .fetchOne()
            .get(0);
    }

}
