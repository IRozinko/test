package fintech.activity.impl;

import fintech.Validate;
import fintech.activity.model.Activity;
import fintech.activity.spi.BulkActionContext;

import java.util.Map;
import java.util.Optional;

public class BulkActionContextImpl implements BulkActionContext {

    private final String type;
    private final Activity activity;
    private final Map<String, Object> params;

    public BulkActionContextImpl(String type, Activity activity, Map<String, Object> params) {
        this.type = type;
        this.activity = activity;
        this.params = params;
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public <T> T getRequiredParam(String name, Class<T> paramClass) {
        Object value = this.params.get(name);
        Validate.notNull(value, "Param not found by name [%s] in bulk action [%s]", name, type);
        assertParamClass(name, paramClass, value);
        return (T) value;
    }

    @Override
    public <T> Optional<T> getParam(String name, Class<T> paramClass) {
        Object value = this.params.get(name);
        if (value == null) {
            return Optional.empty();
        }
        assertParamClass(name, paramClass, value);
        return Optional.of((T) value);
    }

    private <T> void assertParamClass(String name, Class<T> paramClass, Object value) {
        Validate.isAssignableFrom(paramClass, value.getClass(), "Invalid param [%s] class [%s], expected [%s], bulk action [%s]", name, value.getClass().getSimpleName(), paramClass.getSimpleName(), type);
    }
}
