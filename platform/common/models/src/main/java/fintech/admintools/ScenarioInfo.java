package fintech.admintools;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ScenarioInfo implements Comparable<ScenarioInfo> {
    private String name;
    private List<String> requiredParameters;

    @Override
    public int compareTo(ScenarioInfo o) {
        if (o == null || o.getName() == null) {
            return 1;
        } else if (name == null) {
            return -1;
        } else {
            return name.compareTo(o.getName());
        }
    }
}
