package fintech.bo.components;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    public static Long yearsFromNow(LocalDate date) {
        if (date == null) {
            return null;
        }
        return ChronoUnit.YEARS.between(date, LocalDate.now());
    }

    public static Long daysFromNow(LocalDate date) {
        if (date == null) {
            return null;
        }
        return ChronoUnit.DAYS.between(date, LocalDate.now());
    }

    public static Long hoursFromNow(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return ChronoUnit.HOURS.between(date, LocalDateTime.now());
    }

}
