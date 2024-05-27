package fintech.sms.db;

import fintech.TimeMachine;
import fintech.db.BaseEntity;
import fintech.sms.Sms;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(callSuper = true, exclude = "text")
@Entity
@Table(name = "log", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "nextAttemptAt", name = "idx_log_next_attempt_at"),
})
@OptimisticLocking(type = OptimisticLockType.NONE)
@DynamicUpdate
public class SmsLogEntity extends BaseEntity {

    public enum Status {
        PENDING, SENT, IGNORED, FAILED
    }

    @Column(nullable = false)
    private LocalDateTime nextAttemptAt = TimeMachine.now();

    @Column(nullable = false)
    private int attempts;

    @Column(nullable = false)
    private int maxAttempts;

    @Column(nullable = false)
    private int attemptTimeoutInSeconds;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status sendingStatus;

    @Column(nullable = false)
    private String senderId;

    @Column(name = "send_to", nullable = false)
    private String to;

    @Column(name = "sms_text", nullable = false)
    private String text;

    private String error;

    private String provider;

    private String providerId;

    private String providerMessage;

    private String deliveryReportStatus;

    private String deliveryReportStatus2;

    private String deliveryReportError;

    private LocalDateTime deliveryReportReceivedAt;

    public Sms toSms() {
        Sms val = new Sms();
        val.setSenderId(this.senderId);
        val.setTo(this.to);
        val.setText(this.text);
        val.setSendAt(this.nextAttemptAt);
        val.setMaxSendingAttempts(this.maxAttempts);
        val.setAttemptTimeoutInSeconds(this.attemptTimeoutInSeconds);
        return val;
    }
}
