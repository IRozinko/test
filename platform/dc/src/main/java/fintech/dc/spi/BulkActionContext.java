package fintech.dc.spi;

import fintech.dc.commands.LogDebtActionCommand;
import fintech.dc.model.DcSettings;
import fintech.dc.model.Debt;

import java.util.Optional;

public interface BulkActionContext {

    Debt getDebt();

    DcSettings getSettings();

    LogDebtActionCommand getCommand();

    <T> T getRequiredParam(String name, Class<T> paramClass);

    <T> Optional<T> getParam(String name, Class<T> paramClass);
}
