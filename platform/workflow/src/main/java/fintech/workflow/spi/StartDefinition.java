package fintech.workflow.spi;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

public class StartDefinition {

    private final WorkflowBuilder.ActivityBuilder activityBuilder;

    private StartDefinition(WorkflowBuilder.ActivityBuilder activityBuilder) {
        this.activityBuilder = activityBuilder;
    }

    public static StartDefinition startActivity(WorkflowBuilder.ActivityBuilder activityBuilder) {
        return new StartDefinition(activityBuilder);
    }

    public StartDefinition immediately() {
        activityBuilder.add();
        return this;
    }

    public StartDefinition after(String after, String... resolutions) {
        activityBuilder.waitForAll(WorkflowBuilder.completed(after, resolutions));
        activityBuilder.add();
        return this;
    }

    @SafeVarargs
    public final StartDefinition afterAll(Pair<String, Set<String>>... afterAll) {
        activityBuilder.waitForAll(afterAll);
        activityBuilder.add();
        return this;
    }

    @SafeVarargs
    public final StartDefinition afterAny(Pair<String, Set<String>>... afterAny) {
        activityBuilder.waitForAny(afterAny);
        activityBuilder.add();
        return this;
    }
}
