package fintech.dc.impl;

import fintech.Validate;
import fintech.dc.commands.LogDebtActionCommand;
import fintech.dc.model.DcSettings;
import fintech.dc.model.Debt;
import fintech.dc.spi.BulkActionContext;

import java.util.Map;
import java.util.Optional;

public class BulkActionContextImpl implements BulkActionContext {

    private final String type;
    private final LogDebtActionCommand command;
    private final Debt debt;
    private final DcSettings settings;
    private final Map<String, Object> params;

    public BulkActionContextImpl(String type, LogDebtActionCommand command, Debt debt, DcSettings settings, Map<String, Object> params) {
        this.type = type;
        this.command = command;
        this.debt = debt;
        this.settings = settings;
        this.params = params;
    }

    @Override
    public Debt getDebt() {
        return this.debt;
    }

    @Override
    public DcSettings getSettings() {
        return settings;
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

    @Override
    public LogDebtActionCommand getCommand() {
        return command;
    }

    private <T> void assertParamClass(String name, Class<T> paramClass, Object value) {
        Validate.isAssignableFrom(paramClass, value.getClass(), "Invalid param [%s] class [%s], expected [%s], bulk action [%s]", name, value.getClass().getSimpleName(), paramClass.getSimpleName(), type);
    }
}
