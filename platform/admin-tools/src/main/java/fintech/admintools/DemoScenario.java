package fintech.admintools;

import java.util.List;
import java.util.Map;

public interface DemoScenario {

    String getName();

    List<String> getRequiredParameters();

    DemoScenario withParameters(Map<String, String> parameters);

    void run();
}
