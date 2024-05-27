package fintech.dc.spi;

import fintech.dc.model.DcSettings;
import fintech.dc.model.Debt;

import java.util.List;
import java.util.Optional;

public interface ActionContext {

    Debt getDebt();

    DcSettings.Action getAction();

    DcSettings.Trigger getTrigger();

    DcSettings getSettings();

    <T> T getRequiredParam(String name, Class<T> paramClass);

    <T> Optional<T> getParam(String name, Class<T> paramClass);
}
