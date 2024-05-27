package fintech.calendar.impl.query;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;

public class Calculator implements TemporalQuery<LocalDateTime> {
    private final TemporalQuery<Boolean> holidayQuery;
    private final TemporalQuery<LocalDateTime> workingHoursQuery;
    private final int amountToAdd;
    private final TemporalUnit unit;

    public Calculator(TemporalQuery<Boolean> holidayQuery, TemporalQuery<LocalDateTime> workingHoursQuery, int amountToAdd, TemporalUnit unit) {
        this.holidayQuery = holidayQuery;
        this.workingHoursQuery = workingHoursQuery;
        this.amountToAdd = amountToAdd;
        this.unit = unit;
    }

    @Override
    public LocalDateTime queryFrom(TemporalAccessor temporal) {
        final LocalDateTime origin = LocalDateTime.from(temporal);
        final LocalDateTime targetTime = origin.plus(amountToAdd, unit).query(workingHoursQuery);

        long days = ChronoUnit.DAYS.between(origin.toLocalDate(), targetTime.toLocalDate());
        long daysPlus = 0;
        for (long i = 1L; i <= days + daysPlus; i++) {
            if (origin.plusDays(i).query(holidayQuery)) {
                daysPlus++;
            }
        }
        return targetTime.plusDays(daysPlus);
    }
}
