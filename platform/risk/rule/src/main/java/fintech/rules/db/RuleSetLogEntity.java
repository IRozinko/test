package fintech.rules.db;

import fintech.db.BaseEntity;
import fintech.rules.model.Decision;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "rule_set_log", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_rule_set_log_client_id"),
})
public class RuleSetLogEntity extends BaseEntity {

    @Column(nullable = false)
    private Long clientId;

    @Column
    private Long applicationId;

    @Column
    private Long loanId;

    @Column(nullable = false)
    private String ruleSet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Decision decision;

    @Column(nullable = false)
    private LocalDateTime executedAt;

    private String rejectReason;

    private String rejectReasonDetails;
}

