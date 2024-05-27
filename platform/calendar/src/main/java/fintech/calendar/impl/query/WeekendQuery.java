package fintech.calendar.impl.query;

import java.time.DayOfWeek;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;

public class WeekendQuery implements TemporalQuery<Boolean> {

    @Override
    public Boolean queryFrom(TemporalAccessor temporal) {
        switch (DayOfWeek.from(temporal)) {
            case SATURDAY:
            case SUNDAY:
                return true;
            default:
                return false;
        }
    }
}
