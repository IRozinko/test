package fintech.calendar.db;

import fintech.db.BaseRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface HolidayRepository extends BaseRepository<HolidayEntity, Long> {

    Optional<HolidayEntity> findByDate(LocalDate dates);

    List<HolidayEntity> findByDateIn(Collection<LocalDate> dates);

    void removeByDateIn(Collection<LocalDate> holidays);

}
