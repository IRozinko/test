package fintech.dc.db;

import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true, of = {"agent"})
@Entity
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(name = "agent", schema = Entities.SCHEMA)
public class DcAgentEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String agent;

    @Column(nullable = false)
    private boolean disabled;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "agent_portfolio", joinColumns = @JoinColumn(name = "agent_id"), schema = Entities.SCHEMA)
    private List<String> portfolios = new ArrayList<>();

    @OneToMany(mappedBy = "agent")
    private List<DcAgentAbsenceEntity> absence = new ArrayList<>();

}
