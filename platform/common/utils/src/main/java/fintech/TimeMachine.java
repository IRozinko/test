package fintech;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeMachine {

    private static volatile Clock clock = Clock.systemDefaultZone();
    private static final ZoneId zoneId = ZoneId.systemDefault();

    public static LocalDateTime now() {
        return LocalDateTime.now(clock());
    }

    public static LocalDate today() {
        return LocalDate.now(clock());
    }

    public static Instant currentInstant() {
        return clock().instant();
    }

    public static void useFixedClockAt(LocalDateTime date) {
        clock = Clock.fixed(date.atZone(zoneId).toInstant(), zoneId);
    }

    public static void useFixedClockAt(LocalDate date) {
        clock = Clock.fixed(date.atTime(0, 0).atZone(zoneId).toInstant(), zoneId);
    }

    public static void useDefaultClock() {
        clock = Clock.systemDefaultZone();
    }

    public static Clock clock() {
        return clock;
    }
}
