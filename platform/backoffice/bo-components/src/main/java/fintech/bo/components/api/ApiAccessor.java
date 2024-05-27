package fintech.bo.components.api;

import fintech.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;


@Component
public class ApiAccessor {
    static private ApiAccessor INSTANCE;

    final private ApplicationContext context;

    @Autowired
    public ApiAccessor(ApplicationContext context) {
        this.context = context;
    }

    @PostConstruct
    private void init() {
        INSTANCE = this;
    }

    public static ApiAccessor gI() {
        Validate.validState(INSTANCE != null, "ApiAccessor not initialized");
        return INSTANCE;
    }

    public <T> T get(Class<T> tClass) {
        return context.getBean(tClass);
    }

    public <T> Collection<T> getAll(Class<T> tClass) {
        return context.getBeansOfType(tClass).values();
    }
}
