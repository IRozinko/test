package fintech.admintools.impl;

import com.google.common.collect.ImmutableList;
import fintech.admintools.AdminAction;
import fintech.admintools.AdminToolsService;
import fintech.admintools.DemoScenario;
import fintech.admintools.ExecuteAdminActionCommand;
import fintech.admintools.ScenarioInfo;
import fintech.admintools.TriggerSchedulerCommand;
import fintech.db.SystemEnvironment;
import fintech.quartz.QuartzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AdminToolsServiceBean implements AdminToolsService {

    @Autowired(required = false)
    private List<AdminAction> actions = new ArrayList<>();

    @Autowired(required = false)
    private List<DemoScenario> demoScenarios = new ArrayList<>();

    @Autowired
    private AdminActionsHelper actionsHelper;

    @Autowired
    private ActionExecutor executor;

    @Autowired
    private SystemEnvironment dbEnvironment;

    @Autowired
    private QuartzService quartzService;
    @Override
    public Long execute(ExecuteAdminActionCommand command) {
        Long id = actionsHelper.newAction(command);
        executor.execute(id);
        return id;
    }

    @Override
    public List<String> listAvailableActions() {
        return actions.stream().map(AdminAction::getName).collect(Collectors.toList());
    }

    @Override
    public void runDemoScenario(String name,Map<String,String> parameters) {
        if (dbEnvironment.isProd()) {
            throw new IllegalStateException("Can not execute demo scenarios on production!");
        }
        demoScenarios.stream().filter(s -> s.getName().equals(name)).findFirst().ifPresent(s -> {
            log.info("Running demo scenario [{}]", s.getName());
            s.withParameters(parameters).run();
        });
    }

    @Override
    public List<ScenarioInfo> listAvailableDemoScenarios() {
        if (dbEnvironment.isProd()) {
            return ImmutableList.of();
        }
        return demoScenarios.stream().map(s->new ScenarioInfo(s.getName(),s.getRequiredParameters())).sorted().collect(Collectors.toList());
    }

    @Override
    public void triggerScheduler(TriggerSchedulerCommand command) {
        quartzService.triggerJob(command.getName());
    }
}
