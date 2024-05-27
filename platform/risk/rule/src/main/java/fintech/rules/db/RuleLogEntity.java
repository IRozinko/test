package fintech.rules.db;

import fintech.db.BaseEntity;
import fintech.rules.model.Decision;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;


@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "rule_log", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_rule_log_client_id"),
})
public class RuleLogEntity extends BaseEntity {

    @Column(nullable = false)
    private Long clientId;

    @Column
    private Long applicationId;

    @Column
    private Long loanId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rule_set_result_id")
    private RuleSetLogEntity ruleSetResult;

    @Column(nullable = false)
    private String rule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Decision decision;

    private String checksJson;

    private String reason;

    private String reasonDetails;
}

