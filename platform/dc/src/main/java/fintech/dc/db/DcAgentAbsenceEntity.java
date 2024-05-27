package fintech.dc.db;

import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@ToString(callSuper = true, of = {"dateFrom", "dateTo"})
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "agent_absence", schema = Entities.SCHEMA)
public class DcAgentAbsenceEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "agent_id")
    private DcAgentEntity agent;

    @Column(nullable = false)
    private LocalDate dateFrom;

    @Column(nullable = false)
    private LocalDate dateTo;

    private String reason;
}
