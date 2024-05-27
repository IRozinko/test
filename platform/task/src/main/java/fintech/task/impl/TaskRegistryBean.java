package fintech.task.impl;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import fintech.Validate;
import fintech.task.spi.TaskDefinition;
import fintech.task.spi.TaskRegistry;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class TaskRegistryBean implements TaskRegistry {

    private Map<String, com.google.common.base.Supplier<TaskDefinition>> definitions = new HashMap<>();

    @Override
    public void addDefinition(Supplier<TaskDefinition> definition) {
        addDefinition(definition, 30);
    }

    @Override
    public void addDefinition(Supplier<TaskDefinition> definition, int cacheInSeconds) {
        TaskDefinition initial = definition.get();
        com.google.common.base.Supplier<TaskDefinition> internalSupplier =
            cacheInSeconds > 0 ?
                Suppliers.memoizeWithExpiration(definition::get, cacheInSeconds, TimeUnit.SECONDS) : definition::get;
        definitions.put(initial.getType(), internalSupplier);
    }

    @Override
    public TaskDefinition getDefinition(String key) {
        Supplier<TaskDefinition> taskDefinitionSupplier = definitions.get(key)::get;
        Validate.notNull(taskDefinitionSupplier, "Task definition not found by key %s", key);

        TaskDefinition definition = taskDefinitionSupplier.get();
        return Validate.notNull(definition, "Task definition not found by key %s", key);
    }

    @Override
    public boolean hasDefinition(String key) {
        return definitions.containsKey(key);
    }

    @Override
    public List<TaskDefinition> getAll() {
        return ImmutableList.copyOf(definitions.values().stream().map(com.google.common.base.Supplier::get).collect(Collectors.toList()));
    }
}
