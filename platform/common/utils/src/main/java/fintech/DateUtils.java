package fintech;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.SECONDS;

public abstract class DateUtils {

    // Timings
    public static final int I_2_MINUTES = (int) secondsInMinutes(2);
    public static final int I_5_MINUTES = (int) secondsInMinutes(5);
    public static final int I_15_MINUTES = (int) secondsInMinutes(15);
    public static final int I_30_MINUTES = (int) secondsInMinutes(30);
    public static final int I_1_HOUR = (int) secondsInHours(1);
    public static final int I_2_HOUR = (int) secondsInHours(2);
    public static final int I_4_HOUR = (int) secondsInHours(4);
    public static final int I_24_HOURS = (int) secondsInHours(24);
    public static final int I_48_HOURS = (int) secondsInHours(48);
    public static final int I_72_HOURS = (int) secondsInHours(72);
    public static final int I_96_HOURS = (int) secondsInHours(96);
    public static final int I_5_DAYS = (int) secondsInHours(120);

    public static final Duration D_2_MINUTES = Duration.ofMinutes(2);
    public static final Duration D_5_MINUTES = Duration.ofMinutes(5);
    public static final Duration D_15_MINUTES = Duration.ofMinutes(15);
    public static final Duration D_20_MINUTES = Duration.ofMinutes(20);
    public static final Duration D_25_MINUTES = Duration.ofMinutes(25);
    public static final Duration D_48_HOURS = Duration.ofHours(48);
    public static final Duration D_72_HOURS = Duration.ofHours(72);
    public static final Duration D_96_HOURS = Duration.ofHours(96);


    public static LocalDate date(String dateStr) {
        return date(dateStr, "yyyy-MM-dd");
    }

    public static LocalDate date(String dateStr, String format) {
        Preconditions.checkNotNull(dateStr, "Empty date string");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDate date = LocalDate.parse(dateStr, formatter);
        return date;
    }

    public static LocalDateTime dateTime(String dateStr) {
        Preconditions.checkNotNull(dateStr, "Empty date string");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
        return date;
    }

    public static boolean isValidLocalDateTime(String dateStr) {
        return isValidLocalDateTime(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    public static boolean isValidLocalDateTime(String dateStr, String format) {
        if (StringUtils.isBlank(dateStr)) {
            return false;
        }

        try {
            Preconditions.checkNotNull(dateStr, "Empty date string");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(LocalDate date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.of(date, LocalTime.of(0, 0));
    }

    public static Date toDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String toYyyyMmDd(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date);
    }

    public static String localDateTimeToString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime);
    }

    public static long daysUntilEndOfMonth(LocalDate date) {
        return ChronoUnit.DAYS.between(date, date.withDayOfMonth(date.lengthOfMonth()));
    }

    public static long daysAfterStartOfMonth(LocalDate date) {
        return ChronoUnit.DAYS.between(date.withDayOfMonth(1), date);
    }

    public static long toSecondsBetween(LocalDateTime date1, LocalDateTime date2) {
        return SECONDS.between(date1, date2);
    }

    public static boolean lt(LocalDate left, LocalDate other) {
        return left.compareTo(other) < 0;
    }

    public static boolean gt(LocalDate left, LocalDate other) {
        return left.compareTo(other) > 0;
    }

    public static boolean gt(LocalDateTime left, LocalDateTime other) {
        return left.compareTo(other) > 0;
    }

    public static boolean loe(LocalDateTime left, LocalDateTime other) {
        return left.compareTo(other) <= 0;
    }

    public static boolean loe(LocalDate left, LocalDate other) {
        return left.compareTo(other) <= 0;
    }

    public static boolean goe(LocalDate left, LocalDate other) {
        return left.compareTo(other) >= 0;
    }

    public static LocalDate farFarInFuture() {
        return date("2100-01-01");
    }

    public static LocalDate farFarInPast() {
        return date("1900-01-01");
    }

    public static boolean betweenInclusive(LocalDate value, LocalDate from, LocalDate to) {
        return goe(value, from) && loe(value, to);
    }

    public static LocalDate min(LocalDate left, LocalDate right) {
        return lt(left, right) ? left : right;
    }

    public static LocalDate max(LocalDate left, LocalDate right) {
        return gt(left, right) ? left : right;
    }

    public static LocalDateTime max(LocalDateTime left, LocalDateTime right) {
        return gt(left, right) ? left : right;
    }

    public static long hoursBetween(LocalDateTime start, LocalDateTime end, List<DayOfWeek> ignore) {
        return Stream.iterate(start, d -> d.plusHours(1))
            .limit(start.until(end, ChronoUnit.HOURS))
            .filter(d -> !ignore.contains(d.getDayOfWeek()))
            .count();
    }

    public static BigDecimal daysBetween(LocalDateTime from, LocalDateTime to) {
        long hours = from.until(to, ChronoUnit.HOURS);
        return BigDecimal.valueOf(hours / (double) 24).setScale(3, RoundingMode.HALF_UP);
    }

    public static long secondsInHours(long hours) {
        return Duration.ofHours(hours).getSeconds();
    }

    public static long secondsInMinutes(long minutes) {
        return Duration.ofMinutes(minutes).getSeconds();
    }

    public static LocalDate dayOnMonth(YearMonth month, Integer invoiceDayOfMonth) {
        int daysInMonth = month.lengthOfMonth();
        return invoiceDayOfMonth > daysInMonth ?
            month.atDay(daysInMonth) : month.atDay(invoiceDayOfMonth);
    }
}
