package fintech.dc.commands;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SaveAgentCommand {

    private String agent;
    private boolean disabled;
    private List<String> portfolios = new ArrayList<>();
}
