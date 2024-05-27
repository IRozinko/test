package fintech.calendar.db;

import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "holiday", schema = Entities.SCHEMA,
    indexes = {@Index(columnList = "date", name = "idx_unique_business_day_data", unique = true)}
)
public class HolidayEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private LocalDate date;

}
