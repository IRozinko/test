package fintech.notification.db;

import fintech.db.BaseEntity;
import fintech.email.db.EmailLogEntity;
import fintech.notification.Notification;
import fintech.sms.db.SmsLogEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@ToString
@Entity
@Table(name = "notification", schema = Entities.SCHEMA)
@DynamicUpdate
public class NotificationEntity extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    protected LocalDateTime sentAt;

    private String cmsKey;

    private Long clientId;

    private Long loanId;

    private Long loanApplicationId;

    private Long debtId;

    private Long taskId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "email_log_id")
    private EmailLogEntity emailLog;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sms_log_id")
    private SmsLogEntity smsLog;

    public Notification toValueObject() {
        Notification notification = new Notification();
        notification.setSentAt(sentAt);
        notification.setCmsKey(cmsKey);
        notification.setClientId(clientId);
        notification.setLoanId(loanId);
        notification.setLoanApplicationId(loanApplicationId);
        notification.setDebtId(debtId);
        notification.setTaskId(taskId);
        notification.setEmail(Optional.ofNullable(emailLog).map(EmailLogEntity::toEmail).orElse(null));
        notification.setSms(Optional.ofNullable(smsLog).map(SmsLogEntity::toSms).orElse(null));
        return notification;
    }
}
