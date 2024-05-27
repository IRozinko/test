package fintech.task.spi;

import java.util.List;
import java.util.function.Supplier;

public interface TaskRegistry {

    void addDefinition(Supplier<TaskDefinition> definition);

    void addDefinition(Supplier<TaskDefinition> definition, int cacheInSeconds);

    TaskDefinition getDefinition(String key);

    boolean hasDefinition(String key);

    List<TaskDefinition> getAll();
}
