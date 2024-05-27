package fintech.activity.db;

import fintech.activity.model.Activity;
import fintech.db.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@ToString(callSuper = true, of = {"clientId", "action"})
@Table(name = "activity", schema = Entities.SCHEMA)
@DynamicUpdate
public class ActivityLogEntity extends BaseEntity {

    @Column(nullable = false)
    private Long clientId;

    private String agent;

    @Column(nullable = false)
    private String action;

    private String resolution;

    private String source;

    private String topic;

    private String comments;

    private String details;

    private Long applicationId;

    private Long loanId;

    private Long taskId;

    private Long debtId;

    private Long debtActionId;

    private Long paymentId;

    public Activity toValueObject() {
        Activity vo = new Activity();
        vo.setId(this.id);
        vo.setClientId(this.clientId);
        vo.setAgent(this.agent);
        vo.setAction(this.action);
        vo.setResolution(this.resolution);
        vo.setSource(this.source);
        vo.setTopic(this.topic);
        vo.setComments(this.comments);
        vo.setDetails(this.details);
        vo.setApplicationId(this.applicationId);
        vo.setLoanId(this.loanId);
        vo.setTaskId(this.taskId);
        vo.setDebtId(this.debtId);
        vo.setDebtActionId(this.debtActionId);
        vo.setPaymentId(this.paymentId);
        return vo;
    }
}
