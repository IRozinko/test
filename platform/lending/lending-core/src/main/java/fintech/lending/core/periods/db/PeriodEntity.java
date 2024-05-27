package fintech.lending.core.periods.db;

import fintech.db.BaseEntity;
import fintech.lending.core.db.Entities;
import fintech.lending.core.periods.PeriodStatus;
import fintech.lending.core.periods.PeriodStatusDetail;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true)
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Entity
@Table(name = "period", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "periodDate", name = "idx_period_period_date"),
    @Index(columnList = "status", name = "idx_period_status"),
})
public class PeriodEntity extends BaseEntity {

    @Column(nullable = false, columnDefinition = "DATE", unique = true)
    private LocalDate periodDate;

    @Column(columnDefinition = "DATE")
    private LocalDate closeDate;

    @Enumerated(EnumType.STRING)
    private PeriodStatus status = PeriodStatus.OPEN;

    @Enumerated(EnumType.STRING)
    private PeriodStatusDetail statusDetail = PeriodStatusDetail.NEW;

    private String resultLog;

    private LocalDateTime closingStartedAt;

    private LocalDateTime closingEndedAt;

    public void open(PeriodStatusDetail statusDetail) {
        this.status = PeriodStatus.OPEN;
        this.statusDetail = statusDetail;
    }

    public void close(PeriodStatusDetail statusDetail, LocalDateTime when, String resultLog) {
        this.status = PeriodStatus.CLOSED;
        this.statusDetail = statusDetail;
        this.closingEndedAt = when;
        this.resultLog = resultLog;
    }

}
