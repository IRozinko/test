package fintech.calendar.impl;

import fintech.TimeMachine;
import fintech.Validate;
import fintech.calendar.db.HolidayEntity;
import fintech.calendar.db.HolidayRepository;
import fintech.calendar.impl.query.Calculator;
import fintech.calendar.impl.query.WeekendQuery;
import fintech.calendar.impl.query.WorkingHoursQuery;
import fintech.calendar.spi.BusinessCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@DependsOn("flyway.calendar")
public class BusinessCalendarServiceBean implements BusinessCalendarService {

    private final HolidayRepository repository;
    private final static WeekendQuery WEEKEND_QUERY_INSTANCE = new WeekendQuery();
    private final static WorkingHoursQuery WORKING_HOURS_QUERY_INSTANCE = new WorkingHoursQuery();

    @Autowired
    public BusinessCalendarServiceBean(HolidayRepository repository) {
        this.repository = repository;
    }

    @Override
    public LocalDateTime resolveBusinessTime(int amountToAdd, TemporalUnit unit) {
        return resolveBusinessTime(TimeMachine.now(), amountToAdd, unit);
    }

    @Override
    public LocalDateTime resolveBusinessTime(LocalDateTime origin, int amountToAdd, TemporalUnit unit) {
        Validate.notNull(origin, "Origin can not be null");
        return origin.query(new Calculator(temporal -> this.isHoliday(LocalDate.from(temporal)), LocalDateTime::from, amountToAdd, unit));
    }

    @Override
    public LocalDateTime resolveWorkingHours(int amountToAdd, TemporalUnit unit) {
        return resolveWorkingHours(TimeMachine.now(), amountToAdd, unit);
    }

    @Override
    public LocalDateTime resolveWorkingHours(LocalDateTime origin, int amountToAdd, TemporalUnit unit) {
        Validate.notNull(origin, "Origin can not be null");
        return origin.query(new Calculator(temporal -> this.isHoliday(LocalDate.from(temporal)), WORKING_HOURS_QUERY_INSTANCE, amountToAdd, unit));
    }

    @Override
    public boolean isHoliday(LocalDate date) {
        return repository.findByDate(date).map(holidayEntity -> true).orElseGet(() -> date.query(WEEKEND_QUERY_INSTANCE));
    }

    @Override
    @Transactional
    public void putHolidays(Collection<LocalDate> holidays) {
        List<HolidayEntity> byBusinessDayIn = repository.findByDateIn(holidays);
        Set<LocalDate> existingHolidays = byBusinessDayIn.stream().map(HolidayEntity::getDate).collect(Collectors.toSet());

        List<HolidayEntity> dateEntities = holidays.stream()
            .filter(date -> !existingHolidays.contains(date))
            .map(day -> {
                HolidayEntity entity = new HolidayEntity();
                entity.setDate(day);
                return entity;
            })
            .collect(Collectors.toList());
        repository.save(dateEntities);
    }

    @Override
    @Transactional
    public void removeHolidays(Collection<LocalDate> holidays) {
        repository.removeByDateIn(holidays);
    }

}
