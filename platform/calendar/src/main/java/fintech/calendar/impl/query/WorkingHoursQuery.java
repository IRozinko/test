package fintech.calendar.impl.query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;

public class WorkingHoursQuery implements TemporalQuery<LocalDateTime> {
    //TODO put in settings
    private static final LocalTime WORKING_TIME_START = LocalTime.of(7, 0);
    private static final LocalTime WORKING_TIME_END = LocalTime.of(17, 0);

    @Override
    public LocalDateTime queryFrom(TemporalAccessor temporal) {
        LocalDate date = temporal.query(TemporalQueries.localDate());
        LocalTime time = temporal.query(TemporalQueries.localTime());
        if (time.isAfter(WORKING_TIME_END)) {
            return LocalDateTime.of(date.plusDays(1), WORKING_TIME_START);
        }
        if (time.isBefore(WORKING_TIME_START)) {
            return LocalDateTime.of(date, WORKING_TIME_START);
        }
        return LocalDateTime.of(date, time);
    }
}
