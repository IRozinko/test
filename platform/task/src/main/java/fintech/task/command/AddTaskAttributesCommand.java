package fintech.task.command;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class AddTaskAttributesCommand {

    private Long taskId;

    private Map<String, String> attributes = new HashMap<>();

}
