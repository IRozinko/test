package fintech.bo.api.model.dc;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class SaveAgentRequest {

    private String agent;
    private boolean disabled;
    private Set<String> portfolios = new LinkedHashSet<>();
}
