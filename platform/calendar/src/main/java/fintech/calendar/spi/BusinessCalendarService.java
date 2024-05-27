package fintech.calendar.spi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Collection;

public interface BusinessCalendarService {

    LocalDateTime resolveBusinessTime(int amountToAdd, TemporalUnit unit);

    LocalDateTime resolveBusinessTime(LocalDateTime origin, int amountToAdd, TemporalUnit unit);

    LocalDateTime resolveWorkingHours(int amountToAdd, TemporalUnit unit);

    LocalDateTime resolveWorkingHours(LocalDateTime origin, int amountToAdd, TemporalUnit unit);

    boolean isHoliday(LocalDate date);

    void putHolidays(Collection<LocalDate> holidays);

    void removeHolidays(Collection<LocalDate> holidays);
}
