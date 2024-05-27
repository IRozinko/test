package fintech.email.db;

import fintech.ExtraStringUtils;
import fintech.TimeMachine;
import fintech.db.BaseEntity;
import fintech.email.Email;
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
import java.util.stream.Collectors;

@Getter
@Setter
@ToString(callSuper = true, exclude = {"subject", "body"})
@Entity
@Table(name = "log", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "nextAttemptAt", name = "idx_log_next_attempt_at"),
})
@OptimisticLocking(type = OptimisticLockType.NONE)
@DynamicUpdate
public class EmailLogEntity extends BaseEntity {

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

    @Column(name = "send_from", nullable = false)
    private String from;

    @Column(name = "send_from_name", nullable = false)
    private String fromName;

    @Column(name = "send_to", nullable = false)
    private String to;

    private String replyTo;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String body;

    private String error;

    private String provider;

    private String providerId;

    private String providerMessage;

    private String attachmentFileIds;

    public Email toEmail() {
        Email email = new Email();
        email.setSubject(subject);
        email.setBody(body);
        email.setFrom(from);
        email.setFromName(fromName);
        email.setReplyTo(replyTo);
        email.setTo(to);
        email.setSendAt(nextAttemptAt);
        email.setMaxSendingAttempts(maxAttempts);
        email.setAttemptTimeoutInSeconds(attemptTimeoutInSeconds);
        email.setAttachmentFileIds(ExtraStringUtils.splitCommaSeparatedList(attachmentFileIds).stream().map(Long::valueOf).collect(Collectors.toList()));
        return email;
    }
}
