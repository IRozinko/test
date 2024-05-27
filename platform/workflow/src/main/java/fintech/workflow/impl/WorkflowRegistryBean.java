package fintech.workflow.impl;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import fintech.Validate;
import fintech.workflow.DynamicActivityListenersService;
import fintech.workflow.UpdateDynamicActivityListenerCommand;
import fintech.workflow.spi.ActivityDefinition;
import fintech.workflow.spi.WorkflowDefinition;
import fintech.workflow.spi.WorkflowRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WorkflowRegistryBean implements WorkflowRegistry {

    private final Multimap<String, com.google.common.base.Supplier<WorkflowDefinition>> definitions = HashMultimap.create();

    @Autowired
    private DynamicActivityListenersService dynamicActivityListenersService;

    @Autowired
    private TransactionTemplate txTemplate;

    @Override
    public void addDefinition(Supplier<WorkflowDefinition> definition, int cacheInSeconds) {
        WorkflowDefinition initial = definition.get();
        com.google.common.base.Supplier<WorkflowDefinition> internalSupplier =
            cacheInSeconds > 0 ?
                Suppliers.memoizeWithExpiration(definition::get, cacheInSeconds, TimeUnit.SECONDS) : definition::get;
        definitions.put(initial.getWorkflowName(), internalSupplier);

        txTemplate.execute((status) -> {

            initial.getActivities().forEach(activityDefinition -> {
                activityDefinition.getDynamicListeners().forEach(dl -> {
                    dynamicActivityListenersService.addOrEditListener(
                        new UpdateDynamicActivityListenerCommand()
                            .setActivityName(activityDefinition.getActivityName())
                            .setArgs(dl.getArgs())
                            .setListenerStatus(dl.getListenerStatus())
                            .setResolution(dl.getResolution())
                            .setTriggerName(dl.getTriggerName())
                            .setVersion(initial.getWorkflowVersion())
                            .setDelay(dl.getDelay())
                            .setName(activityDefinition.getActivityName())
                            .setFromMidnight(dl.getFromMidnight())
                            .setWorkflowName(initial.getWorkflowName())
                    );
                });
            });
            return true;
        });
    }

    @Override
    public List<WorkflowDefinition> getDefinitions() {
        return definitions.values().stream()
            .map(com.google.common.base.Supplier::get)
            .collect(Collectors.toList());
    }

    @Override
    public WorkflowDefinition getDefinition(String workflowName, int workflowVersion) {
        Validate.isTrue(definitions.containsKey(workflowName), "Workflow definition not found by name %s", workflowName);
        WorkflowDefinition definition = definitions.get(workflowName)
            .stream()
            .map(com.google.common.base.Supplier::get).filter(w -> w.getWorkflowVersion() == workflowVersion)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(String.format("Workflow definition version not found by name %s and version %s", workflowName, workflowVersion)));
        return definition;
    }

    public WorkflowDefinition getDefinition(String workflowName) {
        Validate.isTrue(definitions.containsKey(workflowName), "Workflow definition not found by name %s", workflowName);
        return definitions.get(workflowName)
            .stream()
            .map(com.google.common.base.Supplier::get)
            .max(Comparator.comparing(WorkflowDefinition::getWorkflowVersion))
            .orElseThrow(() -> new IllegalStateException(String.format("Workflow definition version not found by name %s", workflowName)));
    }

    @Override
    public Optional<ActivityDefinition> getActivityDefinition(String workflowName, int workflowVersion, String activityName) {
        return getDefinition(workflowName, workflowVersion).getActivity(activityName);
    }

    public void clear() {
        definitions.clear();
    }
}
