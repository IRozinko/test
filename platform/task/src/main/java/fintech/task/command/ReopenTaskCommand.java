package fintech.task.command;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class ReopenTaskCommand {

    private Long taskId;
    private String reason;
    private LocalDateTime dueAt;
    private LocalDateTime expiresAt;
    private Map<String, String> attributes = new HashMap<>();
}
