package fintech.spain.alfa.app.demo;

import fintech.TimeMachine;
import fintech.admintools.DemoScenario;
import fintech.quartz.QuartzService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDemoScenario implements DemoScenario {

    private final String name;
    private final List<String> requiredParameters;
    private Map<String, String> parameters = new HashMap<>();

    @Autowired
    private QuartzService quartzService;

    public AbstractDemoScenario(String name, List<String> requiredParameters) {
        this.name = name;
        this.requiredParameters = requiredParameters;
    }

    public AbstractDemoScenario(String name) {
        this(name, Collections.emptyList());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void run() {
        try {
            quartzService.pauseScheduler();
            doRun();
        } finally {
            TimeMachine.useDefaultClock();
            quartzService.resumeScheduler();
        }
    }

    @Override
    public List<String> getRequiredParameters() {
        return requiredParameters;
    }

    @Override
    public DemoScenario withParameters(Map<String, String> parameters) {
        this.parameters.clear();
        if (parameters != null) {
            this.parameters.putAll(parameters);
        }
        return this;
    }

    protected Map<String, String> getParameters() {
        return parameters;
    }

    protected abstract void doRun();
}
