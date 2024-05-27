package fintech.bo.api.model.agents;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateAgentRequest {
    private String email;
    private List<String> taskTypes = new ArrayList<>();
}
