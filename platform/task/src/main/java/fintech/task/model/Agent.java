package fintech.task.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Agent {

    private Long id;
    private String email;
    private List<String> taskTypes = new ArrayList<>();
    private boolean disabled;
}
