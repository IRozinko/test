package fintech.dc.impl;

import fintech.Validate;
import fintech.dc.model.DcSettings;
import fintech.dc.model.Debt;
import fintech.dc.spi.ConditionContext;

import java.util.Optional;

public class ConditionContextImpl implements ConditionContext {

    private final Debt debt;
    private final DcSettings.Condition condition;
    private final DcSettings.Trigger trigger;
    private final DcSettings settings;

    public ConditionContextImpl(Debt debt, DcSettings.Condition condition, DcSettings.Trigger trigger, DcSettings settings) {
        this.debt = debt;
        this.condition = condition;
        this.trigger = trigger;
        this.settings = settings;
    }

    @Override
    public Debt getDebt() {
        return debt;
    }

    @Override
    public DcSettings.Condition getCondition() {
        return condition;
    }

    @Override
    public DcSettings getSettings() {
        return settings;
    }

    @Override
    public <T> T getRequiredParam(String name, Class<T> paramClass) {
        Object value = this.condition.getParams().get(name);
        Validate.notNull(value, "Param not found by name [%s] in condition [%s]", name, condition.getType());
        assertParamClass(name, paramClass, value);
        return (T) value;
    }

    @Override
    public <T> Optional<T> getParam(String name, Class<T> paramClass) {
        Object value = this.condition.getParams().get(name);
        if (value == null) {
            return Optional.empty();
        }
        assertParamClass(name, paramClass, value);
        return Optional.of((T) value);
    }

    @Override
    public DcSettings.Trigger getTrigger() {
        return trigger;
    }

    private <T> void assertParamClass(String name, Class<T> paramClass, Object value) {
        Validate.isAssignableFrom(paramClass, value.getClass(), "Invalid param [%s] class [%s], expected [%s], condition [%s]", name, value.getClass().getSimpleName(), paramClass.getSimpleName(), condition.getType());
    }
}
