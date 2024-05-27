package fintech.dc.impl;

import fintech.Validate;
import fintech.dc.model.DcSettings;
import fintech.dc.model.Debt;
import fintech.dc.spi.ActionContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActionContextImpl implements ActionContext {

    private final Debt debt;
    private final DcSettings.Action action;
    private final DcSettings.Trigger trigger;
    private final DcSettings settings;

    public ActionContextImpl(Debt debt, DcSettings.Action action, DcSettings.Trigger trigger, DcSettings settings) {
        this.debt = debt;
        this.action = action;
        this.trigger = trigger;
        this.settings = settings;
    }

    @Override
    public Debt getDebt() {
        return this.debt;
    }

    @Override
    public DcSettings.Action getAction() {
        return this.action;
    }

    @Override
    public DcSettings getSettings() {
        return settings;
    }

    @Override
    public <T> T getRequiredParam(String name, Class<T> paramClass) {
        Object value = this.action.getParams().get(name);
        Validate.notNull(value, "Param not found by name [%s] in action [%s]", name, action.getType());
        assertParamClass(name, paramClass, value);
        return (T) value;
    }

    @Override
    public <T> Optional<T> getParam(String name, Class<T> paramClass) {
        Object value = this.action.getParams().get(name);
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
        Validate.isAssignableFrom(paramClass, value.getClass(), "Invalid param [%s] class [%s], expected [%s], action [%s]", name, value.getClass().getSimpleName(), paramClass.getSimpleName(), action.getType());
    }
}
