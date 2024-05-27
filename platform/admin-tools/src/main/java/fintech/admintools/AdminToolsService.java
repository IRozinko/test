package fintech.admintools;

import java.util.List;
import java.util.Map;

public interface AdminToolsService {

    Long execute(ExecuteAdminActionCommand command);

    List<String> listAvailableActions();

    void runDemoScenario(String name, Map<String,String> parameters);

    List<ScenarioInfo> listAvailableDemoScenarios();
    void triggerScheduler(TriggerSchedulerCommand command);
}
