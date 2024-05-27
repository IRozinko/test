package fintech.lending.core;


import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public enum PeriodUnit {

    NA {
        @Override
        public TemporalUnit toTemporalUnit() {
            throw new UnsupportedOperationException();
        }
    },
    DAY {
        @Override
        public TemporalUnit toTemporalUnit() {
            return ChronoUnit.DAYS;
        }
    },
    MONTH {
        @Override
        public TemporalUnit toTemporalUnit() {
            return ChronoUnit.MONTHS;
        }
    };

    public abstract TemporalUnit toTemporalUnit();
}
