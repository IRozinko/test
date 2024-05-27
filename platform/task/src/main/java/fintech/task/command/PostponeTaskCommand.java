package fintech.task.command;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Accessors(chain = true)
public class PostponeTaskCommand {

    private Long taskId;
    private LocalDateTime postponeTo;
    private LocalDateTime expiresAt;
    private String resolution;
    private String resolutionDetail;
    private String resolutionSubDetail;
    private String comment;

    public Optional<LocalDateTime> getExpiresAt() {
        return Optional.ofNullable(expiresAt);
    }

}
