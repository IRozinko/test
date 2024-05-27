package fintech.task.command;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class AddAgentCommand {

    private String email;
    private List<String> taskTypes = new ArrayList<>();
}
